package com.apdef.tandurapps.ui.manage.price

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apdef.tandurapps.R
import com.apdef.tandurapps.model.PriceList
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_list_price.view.*

class PriceAdapter(val priceList: ArrayList<PriceList>):RecyclerView.Adapter<PriceAdapter.PriceListVH>() {
    private var mContext: Context? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PriceAdapter.PriceListVH {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.item_list_price, parent, false)
        mContext = parent.context
        return PriceListVH(inflater)
    }

    override fun onBindViewHolder(holder: PriceAdapter.PriceListVH, position: Int) {
        val item : PriceList = priceList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = priceList.size

    inner class PriceListVH(itemView: View):RecyclerView.ViewHolder(itemView){
        fun bind(data: PriceList){
            itemView.tv_title_product.text = data.name
            itemView.tv_price_product.text = data.price.toString()
            Glide.with(itemView).load(data.image).into(itemView.iv_product)
        }
    }

}