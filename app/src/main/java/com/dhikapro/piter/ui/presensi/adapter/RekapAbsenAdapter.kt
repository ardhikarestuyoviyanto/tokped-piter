package com.dhikapro.piter.ui.presensi.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dhikapro.piter.R
import com.dhikapro.piter.app.models.presensi.RekapAbsenItemModel

class RekapAbsenAdapter(private val context: Context): RecyclerView.Adapter<RekapAbsenAdapter.MyHolder>() {

    var dataList = emptyList<RekapAbsenItemModel>()
    private val alertDialog = AlertDialog.Builder(context)
    val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    internal fun setDataList(dataList: List<RekapAbsenItemModel>){
        this.dataList = dataList
    }

    inner class MyHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        val tanggal: TextView = itemView.findViewById(R.id.tanggal)
        val absenMasuk: TextView = itemView.findViewById(R.id.absen_masuk)
        val absenPulang: TextView = itemView.findViewById(R.id.absen_pulang)
        val tlKode: TextView = itemView.findViewById(R.id.kode_tl)
        val pswKode: TextView = itemView.findViewById(R.id.kode_psw)
        val potonganTotal: TextView = itemView.findViewById(R.id.potongan_total)
        val fotoMasuk: TextView = itemView.findViewById(R.id.foto_masuk)
        val fotoPulang: TextView = itemView.findViewById(R.id.foto_pulang)
        val keterangan: TextView = itemView.findViewById(R.id.keterangan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_rekap_absen, parent, false)
        return MyHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val listData = dataList[position]

        val tlKodeText = if(listData.kode_tl == "-"){
            listData.kode_tl
        }else{
            "${listData.kode_tl} ( ${listData.potongan_tl}% )"
        }

        val pswKodeText = if(listData.kode_psw == "-"){
            listData.kode_psw
        }else{
            "${listData.kode_psw} ( ${listData.potongan_psw}% )"
        }

        holder.tanggal.text = listData.tanggal
        holder.absenMasuk.text = listData.absen_masuk
        holder.absenPulang.text = listData.absen_pulang
        holder.tlKode.text = tlKodeText
        holder.pswKode.text = pswKodeText
        holder.potonganTotal.text = "${listData.potongan_tl!! +listData.potongan_psw!!} %"
        holder.keterangan.text = listData.keterangan

        val alertView:View = layoutInflater.inflate(R.layout.show_foto_absen, null)
        val imageView: ImageView = alertView.findViewById(R.id.foto)

        holder.fotoMasuk.setOnClickListener {
            Glide.with(context).load(listData.foto_masuk).into(imageView)
            if(alertView.parent != null){
                (alertView.parent as? ViewGroup)?.removeView(alertView)
            }
            alertDialog.setTitle("Foto Absen Masuk")
            alertDialog.setPositiveButton("Oke"){_,_->}
            alertDialog.setView(alertView)
            alertDialog.show()
        }
        holder.fotoPulang.setOnClickListener {
            Glide.with(context).load(listData.foto_pulang).into(imageView)
            if(alertView.parent != null){
                (alertView.parent as? ViewGroup)?.removeView(alertView)
            }
            alertDialog.setTitle("Foto Absen Pulang")
            alertDialog.setPositiveButton("Oke"){_,_->}
            alertDialog.setView(alertView)
            alertDialog.show()
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}