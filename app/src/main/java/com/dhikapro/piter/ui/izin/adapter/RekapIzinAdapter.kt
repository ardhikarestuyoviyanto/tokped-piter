package com.dhikapro.piter.ui.izin.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dhikapro.piter.R
import com.dhikapro.piter.app.models.perizinan.RekapPerizinanItemModel
import com.dhikapro.piter.ui.izin.DetailIzinActivity

class RekapIzinAdapter(private val context: Context): RecyclerView.Adapter<RekapIzinAdapter.MyHolder>() {

    var dataList = emptyList<RekapPerizinanItemModel>()

    internal fun setDataList(dataList: List<RekapPerizinanItemModel>){
        this.dataList = dataList
    }

    inner class MyHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var tglDiajukan: TextView = itemView.findViewById(R.id.tanggal_diajukan)
        var tanggalMulai: TextView = itemView.findViewById(R.id.tanggal_mulai)
        var tanggalSelesai: TextView = itemView.findViewById(R.id.tanggal_selesai)
        var kategoriPerizinan: TextView = itemView.findViewById(R.id.kategori_perizinan)
        var filePendukung: TextView = itemView.findViewById(R.id.file_pendukung)
        var statusPerizinan: TextView = itemView.findViewById(R.id.status_perizinan)
        var edit: TextView = itemView.findViewById(R.id.edit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_izin, parent, false)
        return MyHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val listData = dataList[position]
        holder.tglDiajukan.text = "Diajukan Tgl ${listData.tanggal_diajukan}"
        holder.tanggalMulai.text = listData.tanggal_mulai
        holder.tanggalSelesai.text = listData.tanggal_selesai
        holder.kategoriPerizinan.text = listData.kategori_perizinan
        holder.statusPerizinan.text = listData.status_perizinan
        when (listData.status_perizinan) {
            "DIVERIFIKASI" -> {
                holder.statusPerizinan.setTextColor(ContextCompat.getColor(context, R.color.primary))
            }
            "TIDAK DISETUJUI" -> {
                holder.statusPerizinan.setTextColor(ContextCompat.getColor(context, R.color.danger))
            }
            else -> {
                holder.statusPerizinan.setTextColor(ContextCompat.getColor(context, R.color.success))
            }
        }
        holder.filePendukung.setOnClickListener {
            holder.itemView.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(listData.file_pendukung)))
        }
        holder.edit.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailIzinActivity::class.java)
            intent.putExtra("perizinan_id", listData.id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }


}