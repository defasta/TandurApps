package com.apdef.tandurapps.ui.home.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apdef.tandurapps.R
import com.apdef.tandurapps.model.PlantingPackage
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_list_product.view.*

class PlantingPackageAdapter (val plantingPackage: ArrayList<PlantingPackage>):RecyclerView.Adapter<PlantingPackageAdapter.PlantingPackageVH>(){
    private var mContext: Context? = null
    private var itemClick: ItemClick? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PlantingPackageAdapter.PlantingPackageVH {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.item_list_product, parent, false)
        mContext = parent.context
        return PlantingPackageVH(inflater)
    }

    override fun onBindViewHolder(holder: PlantingPackageAdapter.PlantingPackageVH, position: Int) {
        val item : PlantingPackage = plantingPackage[position]
        holder.bind(item)
    }

    inner class PlantingPackageVH(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(data: PlantingPackage){
            itemView.tv_title_product.text = data.name.toString()
            itemView.tv_price_product.text = "Rp. " + data.price.toString()
            itemView.tv_info_product.text = data.detail
            Glide.with(itemView).load(data.images).into(itemView.iv_product)
            itemView.setOnClickListener{
                itemClick?.itemOnClicked(data)
            }
        }
    }

    override fun getItemCount(): Int = plantingPackage.size

    fun setOnClickItem(itemClick: ItemClick){
        this.itemClick = itemClick
    }
    interface ItemClick{
        fun itemOnClicked(plantingPackage: PlantingPackage)
    }
}