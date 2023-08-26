package com.dhikapro.piter.app.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class UriToFileUpload(private val fileUri: Uri, val context: Context){
    @SuppressLint("Recycle")
    fun getRequestBodyFile(): RequestBody {
        val parcelFileDescriptor = context.contentResolver?.openFileDescriptor(fileUri, "r", null)
        val inputStream = FileInputStream(parcelFileDescriptor?.fileDescriptor)
        val file = context.contentResolver?.getFileName(fileUri)?.let { File(context.cacheDir, it) }
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        return file!!.asRequestBody("multipart/form-data".toMediaTypeOrNull())
    }

    @SuppressLint("Recycle")
    fun getFile(): File{
        val parcelFileDescriptor = context.contentResolver?.openFileDescriptor(fileUri, "r", null)
        val inputStream = FileInputStream(parcelFileDescriptor?.fileDescriptor)
        val file = context.contentResolver?.getFileName(fileUri)?.let { File(context.cacheDir, it) }
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)

        return file!!
    }
}

fun ContentResolver.getFileName(fileUri: Uri):String{
    var name = ""
    val returnCursor = this.query(fileUri, null, null, null, null)
    if(returnCursor != null){
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        name = returnCursor.getString(nameIndex)
        returnCursor.close()
    }
    return name
}

class UploadRequestBody(
    private val file: File,
    private val contentType: String,
):RequestBody() {
    override fun contentType() = "$contentType/*".toMediaTypeOrNull()

    override fun writeTo(sink: BufferedSink) {
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val fileInputStream = FileInputStream(file)
        var uploaded = 0L
        fileInputStream.use {inputStream ->
            var read:Int
            while (inputStream.read(buffer).also { read = it } != -1){
                uploaded+=read
                sink.write(buffer,0,read)
            }
        }
    }

    companion object {
        private const val DEFAULT_BUFFER_SIZE = 2048
    }
}