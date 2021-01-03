package com.apdef.tandurapps.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DataSell (
    var userName: String? ="",
    var userKecamatan: String? ="",
    var userKelurahan: String? = "",
    var userKodepos: String? ="",
    var userAdditionalAddress: String? ="",
    var productTotal: String = "",
    var productName: String = "",
    var productInfo: String ="",
    var price: String = "0",
    var time: String ="",
    var image: String = ""
):Parcelable
