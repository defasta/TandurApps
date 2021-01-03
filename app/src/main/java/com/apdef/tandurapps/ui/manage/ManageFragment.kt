package com.apdef.tandurapps.ui.manage

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.apdef.tandurapps.R
import com.apdef.tandurapps.ui.manage.history.HistoryActivity
import com.apdef.tandurapps.ui.manage.mypackage.MyPackageActivity
import com.apdef.tandurapps.ui.manage.price.PriceActivity
import com.apdef.tandurapps.ui.manage.price.PriceAdapter
import com.apdef.tandurapps.ui.manage.sell.SellProductDetailActivity
import kotlinx.android.synthetic.main.fragment_manage.*

class ManageFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_manage, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        cv_my_package.setOnClickListener {
            startActivity(Intent(context, MyPackageActivity::class.java))
        }

        cv_sell.setOnClickListener {
            startActivity(Intent(context, SellProductDetailActivity::class.java))
        }

        cv_history.setOnClickListener {
            startActivity(Intent(context, HistoryActivity::class.java))
        }

        cv_market_price.setOnClickListener {
            startActivity(Intent(context, PriceActivity::class.java))
        }
    }

}