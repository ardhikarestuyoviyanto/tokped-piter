package com.dhikapro.piter.ui.info.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dhikapro.piter.R
import com.dhikapro.piter.app.models.info.InfoItemModel
import com.dhikapro.piter.ui.info.DetailInfoActivity

class ListInfoAdapter(
    private val context: Context
): RecyclerView.Adapter<ListInfoAdapter.MyHolder>() {

    var dataList = emptyList<InfoItemModel>()

    internal fun setDataList(dataList: List<InfoItemModel>){
        this.dataList = dataList
    }

    inner class MyHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var namaKategori: TextView = itemView.findViewById(R.id.nama_kategori)
        var judul: TextView = itemView.findViewById(R.id.judul)
        var tanggal: TextView = itemView.findViewById(R.id.tanggal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_info, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val listData = dataList[position]
        holder.namaKategori.text = listData.nama_kategori
        holder.judul.text = listData.judul
        holder.tanggal.text = listData.tanggal

        holder.judul.setOnClickListener {
            val intent = Intent(context, DetailInfoActivity::class.java)
            intent.putExtra("info_id", listData.id)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}