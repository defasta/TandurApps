package com.apdef.tandurapps.ui.manage.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apdef.tandurapps.R
import com.apdef.tandurapps.model.Transaction
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_list_my_package.view.*

class MyPackageAdapter(val plantingMyPackage: ArrayList<Transaction>):RecyclerView.Adapter<MyPackageAdapter.PlantingMyPackageVH>() {
    private var mContext: Context? = null
    private var itemClick: ItemClick? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlantingMyPackageVH {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.item_list_my_package, parent, false)
        mContext = parent.context
        return PlantingMyPackageVH(inflater)
    }

    override fun onBindViewHolder(
        holder: PlantingMyPackageVH,
        position: Int
    ) {
        val item :Transaction = plantingMyPackage[position]
        holder.bind(item)
    }

    inner class PlantingMyPackageVH(itemView: View):RecyclerView.ViewHolder(itemView){
        fun bind(data: Transaction){
            itemView.tv_title.text = data.name
//            itemView.tv_keterangan.text = data.status
            if (data.status == "0"){
                itemView.tv_keterangan.text = "Belum dibayar"
            }
            Glide.with(itemView).load(data.images).into(itemView.img_stuff)
            itemView.setOnClickListener {
                itemClick?.itemOnClicked(data)
            }
        }
    }

    override fun getItemCount(): Int = plantingMyPackage.size

    fun setOnClickItem(itemClick: ItemClick){
        this.itemClick = itemClick
    }

    interface ItemClick{
        fun itemOnClicked(transaction: Transaction)
    }
}