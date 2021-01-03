package com.apdef.tandurapps.ui.manage.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.apdef.tandurapps.R
import com.apdef.tandurapps.model.DataSell
import kotlinx.android.synthetic.main.activity_history_detail.*
import kotlinx.android.synthetic.main.activity_sell_confirm.*
import kotlinx.android.synthetic.main.activity_sell_confirm.tv_add
import kotlinx.android.synthetic.main.activity_sell_confirm.tv_banyak_produk
import kotlinx.android.synthetic.main.activity_sell_confirm.tv_kecamatan
import kotlinx.android.synthetic.main.activity_sell_confirm.tv_kelurahan
import kotlinx.android.synthetic.main.activity_sell_confirm.tv_kodepos
import kotlinx.android.synthetic.main.activity_sell_confirm.tv_name_product
import kotlinx.android.synthetic.main.activity_sell_confirm.tv_product_info

class HistoryDetailActivity : AppCompatActivity() {
    companion object{
        const val EXTRA_HISTORY = "extra_history"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_detail)
        val dataSell = intent.getParcelableExtra(EXTRA_HISTORY) as DataSell

        if (dataSell.price == "0"){
            tv_info.visibility = View.VISIBLE
        }else{
            tv_price_title_history.visibility = View.VISIBLE
            tv_price_history.visibility = View.VISIBLE
            tv_price_history.text = dataSell.price
        }

        tv_name_product.text = dataSell.productName
        tv_banyak_produk.text = dataSell.productTotal
        tv_product_info.text = dataSell.productInfo
        tv_kelurahan.text = dataSell.userKelurahan
        tv_kecamatan.text = dataSell.userKecamatan
        tv_kodepos.text = dataSell.userKodepos
        tv_add.text = dataSell.userAdditionalAddress
    }
}