package com.dhikapro.piter.ui.lembur.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dhikapro.piter.R
import com.dhikapro.piter.app.models.lembur.RekapLemburItemModel
import com.dhikapro.piter.app.repository.lembur.RekapLemburRepo
import com.dhikapro.piter.ui.lembur.DetailLemburActivity

class RekapLemburAdapter(private val rekapLemburRepo: RekapLemburRepo, private val context: Context, private val token: String): RecyclerView.Adapter<RekapLemburAdapter.MyHolder>() {

    var dataList = emptyList<RekapLemburItemModel>()
    var alertDialog = AlertDialog.Builder(context)

    internal fun setDataList(dataList: List<RekapLemburItemModel>){
        this.dataList = dataList
    }

    inner class MyHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var tanggal: TextView = itemView.findViewById(R.id.tanggal)
        var jamMulai: TextView = itemView.findViewById(R.id.jam_mulai)
        var jamSelesai: TextView = itemView.findViewById(R.id.jam_selesai)
        var kegiatan: TextView = itemView.findViewById(R.id.kegiatan)
        var bukti: TextView = itemView.findViewById(R.id.bukti)
        var totalJam: TextView = itemView.findViewById(R.id.total_jam)
        var edit: TextView = itemView.findViewById(R.id.edit)
        var hapus: TextView = itemView.findViewById(R.id.hapus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_lembur, parent, false)
        return MyHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val listData = dataList[position]
        holder.tanggal.text = listData.tanggal
        holder.jamMulai.text = listData.jam_mulai
        holder.jamSelesai.text = listData.jam_selesai
        holder.kegiatan.text = listData.kegiatan
        holder.totalJam.text = "${listData.total_jam} Jam"
        holder.bukti.setOnClickListener {
            holder.itemView.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(listData.bukti)))
        }
        holder.edit.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailLemburActivity::class.java)
            intent.putExtra("lembur_id", listData.id)
            holder.itemView.context.startActivity(intent)
        }
        holder.hapus.setOnClickListener {
            alertDialog.setTitle("Hapus Lembur")
            alertDialog.setMessage(listData.kegiatan)
            alertDialog.setPositiveButton("Oke") {_,_->
                rekapLemburRepo.deleteLembur(
                    listData.id!!,
                    token
                )
            }
            alertDialog.setNegativeButton("Batal"){_,_->}
            alertDialog.show()
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}