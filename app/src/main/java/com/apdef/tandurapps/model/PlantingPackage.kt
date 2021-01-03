package com.apdef.tandurapps.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PlantingPackage(
    var name: String = "",
    var detail: String = "",
    var images: String = "",
    var price: Int = 0,
    var info: String = "",
    var education: String = ""
):Parcelable