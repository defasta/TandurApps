package com.apdef.tandurapps.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Transaction (
    var addressAdditional: String="",
    var addressKecamatan: String="",
    var addressKelurahan: String = "",
    var addressKodepos: String = "",
    var name: String = "",
    var detail: String = "",
    var images: String = "",
    var price: String = "",
    var info: String = "",
    var education: String = "",
    var status: String = "",
    var paymentMethod: String = "",
    var paymentMethodCode: String = ""
): Parcelable