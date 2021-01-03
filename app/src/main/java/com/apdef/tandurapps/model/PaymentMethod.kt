package com.apdef.tandurapps.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentMethod(
    var code : String ="",
    var name : String =""
):Parcelable