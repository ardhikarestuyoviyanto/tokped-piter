@file:Suppress("DEPRECATION")

package com.dhikapro.piter.ui.presensi

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.darwin.viola.still.FaceDetectionListener
import com.darwin.viola.still.Viola
import com.darwin.viola.still.model.FaceDetectionError
import com.darwin.viola.still.model.Result
import com.dhikapro.piter.R
import com.dhikapro.piter.app.interfaces.presensi.AbsenInterface
import com.dhikapro.piter.app.models.presensi.AbsenModel
import com.dhikapro.piter.app.repository.presensi.PostAbsenRepo
import com.dhikapro.piter.app.session.SharedPreference
import com.dhikapro.piter.app.utils.DisableDarkMode
import com.dhikapro.piter.app.utils.PermissionCheck
import com.dhikapro.piter.app.utils.UploadRequestBody
import com.dhikapro.piter.app.utils.snackbar
import com.dhikapro.piter.databinding.ActivityPostAbsenBinding
import com.dhikapro.piter.ui.auth.LoginActivity
import com.dhikapro.piter.ui.dashboard.DashboardActivity
import com.tapadoo.alerter.Alerter
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*

class PostAbsenActivity : AppCompatActivity(), AbsenInterface {

    private lateinit var binding: ActivityPostAbsenBinding
    private lateinit var sharedPreference: SharedPreference
    private lateinit var postAbsenRepo: PostAbsenRepo
    private lateinit var previewView: PreviewView
    private lateinit var imageCapture: ImageCapture
    private lateinit var progressDialog: ProgressDialog

    private var preview: Preview?=null
    private var imageBitmap: Bitmap?=null
    private var countRotation:Int = 0
    private var filePhoto: File?=null


    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityPostAbsenBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        DisableDarkMode(this).disable()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPreference = SharedPreference(this)
        postAbsenRepo = PostAbsenRepo(this, this)
        progressDialog = ProgressDialog(this)

        if(intent.getStringExtra("type_absen") == null){
            startActivity(Intent(this, DashboardActivity::class.java))
            overridePendingTransition(0, 0)
        }
        if(intent.getStringExtra("type_absen") == "masuk"){
            supportActionBar?.setTitle(getString(R.string.app_bar_absen_masuk))
        }else{
            supportActionBar?.setTitle(getString(R.string.app_bar_absen_pulang))
        }

        if(PermissionCheck().checkPermissions(this)){
            startCamera()
        }else{
            disabledForm("Permission Camera Required")
        }

        binding.takeFoto.setOnClickListener {
            progressDialog.setTitle("Mengambil Foto")
            progressDialog.setMessage("Loading ...")
            progressDialog.show()

            imageCapture.takePicture(
                ContextCompat.getMainExecutor(this),
                object : ImageCapture.OnImageCapturedCallback(){
                    override fun onCaptureSuccess(image: ImageProxy) {
                        imageBitmap = image.convertImageProxyToBitmap()
                        image.close()
                        showPreviewDialogFoto()
                    }
                    override fun onError(exception: ImageCaptureException) {
                        binding.root.snackbar("Terjadi kesalahan pada system")
                    }

                }
            )
        }

    }

    private fun startCamera(){
        preview = Preview.Builder().build()
        previewView = binding.cameraxView
        preview?.setSurfaceProvider(previewView.surfaceProvider)

        imageCapture = ImageCapture.Builder()
            .setJpegQuality(50)
            .build()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()

        lifecycleScope.launch {
            val cameraProvider = ProcessCameraProvider.getInstance(this@PostAbsenActivity).await()
            try{
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this@PostAbsenActivity,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            }catch (e:Exception){
                disabledForm("Kamera Depan Tidak Ada")
            }
        }
    }

    private fun showPreviewDialogFoto(){
        progressDialog.hide()
        val alertView: View = layoutInflater.inflate(R.layout.preview_foto, null)
        val imageView: ImageView = alertView.findViewById(R.id.alert_preview_camerax)
        Glide.with(this).load(this.imageBitmap).into(imageView)
        val alertDialog = AlertDialog.Builder(this)

        // rotate foto
        val buttonRotateImage: Button = alertView.findViewById(R.id.rotate_image_button)
        buttonRotateImage.setOnClickListener {
            imageView.rotation = imageView.rotation + 90f
            countRotation++
        }

        // alert if success
        alertDialog.setTitle("Foto Anda")
        alertDialog.setPositiveButton("Simpan"){_,_->
            // get bitmap and rotate it
            val drawable: Drawable = imageView.drawable
            val bitmap = drawable.toBitmap()
            imageBitmap = rotateBitmap(bitmap, this.countRotation * 90f)
            // ai
            val viola = Viola(listener)
            viola.detectFace(imageBitmap!!)
            // show loading
            // loading start
            progressDialog.setTitle("Deteksi Wajah")
            progressDialog.setMessage("Loading ...")
            progressDialog.show()

        }
        alertDialog.setNegativeButton("Batal"){_,_->}
        alertDialog.setView(alertView)
        alertDialog.show()
    }

    private val listener: FaceDetectionListener = object : FaceDetectionListener {
        override fun onFaceDetected(result: Result) {
            progressDialog.hide()
            countRotation = 0
            faceDetected(result)
        }
        override fun onFaceDetectionFailed(error: FaceDetectionError, message: String) {
            progressDialog.hide()
            countRotation = 0
            binding.root.snackbar("Wajah anda tidak terdeteksi pada foto, silahkan coba lagi ...")
        }
    }

    private fun faceDetected(result: Result){

        if(result.faceCount == 1 && this.imageBitmap != null){
            // set bitmap to file
            filePhoto = bitmapToFile(this.imageBitmap!!)
            val body = UploadRequestBody(this.filePhoto!!, "image/jpeg")
            // check absen masuk or absen pulang
            if(intent.getStringExtra("type_absen") == "masuk"){
                val fileMP = MultipartBody.Part.createFormData("foto_masuk", filePhoto!!.name, body)
                postAbsenRepo.postAbsenMasuk(
                    fileMP,
                    sharedPreference.getTokenJWT()!!
                )
            }else{
                val fileMP = MultipartBody.Part.createFormData("foto_pulang", filePhoto!!.name, body)
                postAbsenRepo.postAbsenPulang(
                    fileMP,
                    sharedPreference.getTokenJWT()!!
                )
            }

        }else{
            Alerter.create(this)
                .setTitle("Info")
                .setText("Sistem mendeteksi terdapat "+result.faceCount.toString()+" wajah pada foto, silahkan coba lagi")
                .setDuration(3000)
                .setBackgroundColorRes(R.color.danger)
                .show()
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onSuccessAbsen(absenModel: AbsenModel) {
        Alerter.create(this)
            .setTitle("Presensi Berhasil")
            .setText(absenModel.message!!)
            .setBackgroundColorRes(R.color.success)
            .show()
    }

    override fun onBadRequest(message: String) {
        Alerter.create(this)
            .setTitle("Terjadi Kesalahan")
            .setText(message)
            .setBackgroundColorRes(R.color.danger)
            .show()
    }

    override fun onUnAuthorized(message: String) {
        sharedPreference.removeToken()
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("onUnAuthorized", message)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun disabledForm(textDisabled: String){
        binding.takeFoto.isEnabled = false
        binding.takeFoto.background = ContextCompat.getDrawable(this, R.drawable.border_button_warning)
        binding.takeFoto.text = textDisabled
    }

    companion object{
        fun ImageProxy.convertImageProxyToBitmap(): Bitmap {
            val buffer = planes[0].buffer
            buffer.rewind()
            val bytes = ByteArray(buffer.capacity())
            buffer.get(bytes)
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }
        fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
            val matrix = Matrix()
            matrix.postRotate(angle)
            return Bitmap.createBitmap(
                source, 0, 0, source.width, source.height, matrix, true
            )
        }
        fun bitmapToFile(bitmap: Bitmap): File? {
            var file: File? = null
            try {
                val dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                file = File(dir, UUID.randomUUID().toString()+".jpeg")
                file.createNewFile()

                val bos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
                val bitmapData = bos.toByteArray()

                val fos = FileOutputStream(file)
                fos.write(bitmapData)
                fos.flush()
                fos.close()
            }catch (e: Exception){
                e.printStackTrace()
            }
            return file
        }
    }

}