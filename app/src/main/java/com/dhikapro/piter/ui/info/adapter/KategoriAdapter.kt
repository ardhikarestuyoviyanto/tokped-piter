package com.dhikapro.piter.ui.info.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dhikapro.piter.R
import com.dhikapro.piter.app.models.info.KategoriItemModel
import com.dhikapro.piter.app.repository.info.InfoRepo

class KategoriAdapter(
    private val context: Context,
    private val infoRepo: InfoRepo,
    private val token: String
): RecyclerView.Adapter<KategoriAdapter.MyHolder>() {

    var dataList = emptyList<KategoriItemModel>()
    private var rowIndex: Int = 0

    internal fun setDataList(dataList: List<KategoriItemModel>){
        this.dataList =dataList
    }

    inner class MyHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var layoutKategori: LinearLayout = itemView.findViewById(R.id.layout_kategori)
        var namaKategori: TextView = itemView.findViewById(R.id.nama_kategori)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_kategori_info, parent, false)
        return MyHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged", "UseCompatLoadingForDrawables")
    override fun onBindViewHolder(holder: MyHolder, @SuppressLint("RecyclerView") position: Int) {
        val listData = dataList[position]
        holder.namaKategori.text = listData.nama_kategori
        holder.namaKategori.setOnClickListener {
            rowIndex = position
            notifyDataSetChanged()
            infoRepo.getInfoAll(
                listData.id,
                token
            )
        }

        if(rowIndex!=position){
            holder.layoutKategori.background = context.getDrawable(R.drawable.badge_item_not_selected)
        }else{
            holder.layoutKategori.background = context.getDrawable(R.drawable.badge_item_selected)
            infoRepo.getInfoAll(
                listData.id,
                token
            )
        }

    }

    override fun getItemCount(): Int {
        return dataList.size
    }

}