package com.apdef.tandurapps.ui.manage.mypackage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.apdef.tandurapps.R
import com.apdef.tandurapps.model.Transaction
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_my_package_detail.*

class MyPackageDetailActivity : AppCompatActivity() {
    companion object{
        const val EXTRA_PLANTING_PACKAGE = "extra_planting_package"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_package_detail)

        val transaction = intent.getParcelableExtra(EXTRA_PLANTING_PACKAGE)  as Transaction
        showPlantingMyPackageDetail(transaction)
    }

    private fun showPlantingMyPackageDetail(transaction: Transaction){
        Glide.with(this).load(transaction.images).into(iv_product)
        tv_highlights_product.text = transaction.education
    }
}