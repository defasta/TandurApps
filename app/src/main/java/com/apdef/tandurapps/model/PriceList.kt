package com.apdef.tandurapps.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PriceList (
    var name: String = "",
    var image: String = "",
    var price: Int = 0,
): Parcelable