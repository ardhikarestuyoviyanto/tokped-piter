package com.dhikapro.piter.ui.lkh.adapter

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dhikapro.piter.R
import com.dhikapro.piter.app.models.lkh.RekapLkhItemModel
import com.dhikapro.piter.app.repository.lkh.RekapLkhRepo
import com.dhikapro.piter.ui.lkh.DetailLkhActivity

class RekapLkhAdapter(private val rekapLkhRepo: RekapLkhRepo, private val context: Context, private val token: String): RecyclerView.Adapter<RekapLkhAdapter.MyHolder>() {

    var dataList = emptyList<RekapLkhItemModel>()
    var alertDialog = AlertDialog.Builder(context)

    internal fun setDataList(dataList: List<RekapLkhItemModel>){
        this.dataList = dataList
    }

    inner class MyHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val tanggal: TextView = itemView.findViewById(R.id.tanggal)
        val kegiatan: TextView = itemView.findViewById(R.id.kegiatan)
        val namaKategori: TextView = itemView.findViewById(R.id.nama_kategori)
        val status: TextView = itemView.findViewById(R.id.status)
        val edit: TextView = itemView.findViewById(R.id.edit)
        val hapus: TextView = itemView.findViewById(R.id.hapus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_lkh, parent, false)
        return MyHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val listData = dataList[position]
        holder.tanggal.text = listData.tanggal
        holder.kegiatan.text = listData.kegiatan
        holder.namaKategori.text = listData.nama_kategori
        if(listData.status == "0"){
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.danger))
            holder.status.text = "Belum Acc"
        }else{
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.success))
            holder.status.text = "Sudah Acc"
        }

        holder.hapus.setOnClickListener {
            alertDialog.setTitle("Hapus LKH")
            alertDialog.setMessage(listData.kegiatan)
            alertDialog.setPositiveButton("Oke") {_,_->
                rekapLkhRepo.deleteLkh(
                    listData.id!!,
                    token
                )
            }
            alertDialog.setNegativeButton("Batal"){_,_->}
            alertDialog.show()
        }
        holder.edit.setOnClickListener {
            val intent = Intent(context, DetailLkhActivity::class.java)
            intent.putExtra("lkh_id", listData.id)
            holder.itemView.context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }


}