package com.apdef.tandurapps.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UserData(
    val username: String? ="",
    val email: String? = "",
    val token: String? ="",
    val kecamatan: String? ="",
    val kelurahan: String? = "",
    val kodepos: String? ="",
    val additionalAddress: String? =""
):Parcelable