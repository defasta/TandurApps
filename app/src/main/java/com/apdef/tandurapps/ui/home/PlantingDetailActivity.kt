package com.apdef.tandurapps.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.apdef.tandurapps.R
import com.apdef.tandurapps.model.PlantingPackage
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_planting_detail.*

class PlantingDetailActivity : AppCompatActivity() {

    companion object{
        const val EXTRA_PLANTING_PACKAGE = "extra_planting_package"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planting_detail)
        val plantingData = intent.getParcelableExtra(EXTRA_PLANTING_PACKAGE) as PlantingPackage

        showPlantingDetail(plantingData)
        btn_buy.setOnClickListener {
            startActivity(Intent(this, PlantingPaymentActivity::class.java).putExtra(PlantingPaymentActivity.EXTRA_PLANTING_PACKAGE, plantingData))
        }
    }

    private fun showPlantingDetail(plantingData : PlantingPackage){
        Glide.with(this).load(plantingData.images).into(iv_product)
        tv_title_product.text = plantingData.name
        tv_price_product.text = "Rp. "+plantingData.price.toString()
        tv_highlights_product.text = plantingData.detail
        tv_penjelasan_produk.text = plantingData.info
    }
}