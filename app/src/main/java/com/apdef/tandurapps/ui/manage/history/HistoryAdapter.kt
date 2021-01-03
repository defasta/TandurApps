package com.apdef.tandurapps.ui.manage.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.apdef.tandurapps.R
import com.apdef.tandurapps.model.DataSell
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_list_history.view.*

class HistoryAdapter (val dataSell: ArrayList<DataSell>):
    RecyclerView.Adapter<HistoryAdapter.HistoryVH>(){
    private var mContext: Context? = null
    private var itemClick: ItemClick? = null
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryAdapter.HistoryVH {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.item_list_history, parent, false)
        mContext = parent.context
        return HistoryVH(inflater)
    }

    override fun onBindViewHolder(holder: HistoryAdapter.HistoryVH, position: Int) {
        val item : DataSell = dataSell[position]
        holder.bind(item)
    }

    inner class HistoryVH(itemView: View): RecyclerView.ViewHolder(itemView){
        fun bind(data: DataSell){
            itemView.tv_title.text = data.productName
            itemView.tv_date.text = data.time
            if (data.price == "0"){
                itemView.tv_keterangan.text = "Sedang Diproses"
            }else{
                itemView.tv_keterangan.text = "Rp. " + data.price
            }
            itemView.setOnClickListener{
                itemClick?.itemOnClicked(data)
            }
        }
    }

    override fun getItemCount(): Int = dataSell.size

    fun setOnClickItem(itemClick: ItemClick){
        this.itemClick = itemClick
    }
    interface ItemClick{
        fun itemOnClicked(data: DataSell)
    }
}