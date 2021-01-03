package com.apdef.tandurapps.model.response

import com.google.gson.annotations.SerializedName

data class ResponseTime (

    @field:SerializedName("status")
    val status: String? = null,

    @field:SerializedName("message")
    val message: String? = null,

    @field:SerializedName("countryCode")
    val countryCode: String? = null,

    @field:SerializedName("countryName")
    val countryName: String? = null,

    @field:SerializedName("regionName")
    val regionName: String? = null,

    @field:SerializedName("cityName")
    val cityName: String? = null,

    @field:SerializedName("zoneName")
    val zoneName: String? = null,

    @field:SerializedName("abbreviation")
    val abbreviation: String? = null,

    @field:SerializedName("gmtOffset")
    val gmtOffset: Int? = null,

    @field:SerializedName("dst")
    val dst: String? = null,

    @field:SerializedName("zoneStart")
    val zoneStart: Int? = null,

    @field:SerializedName("zoneEnd")
    val zoneEnd: Int? = null,

    @field:SerializedName("nextAbbreviation")
    val nextAbbreviation: String? = null,

    @field:SerializedName("timestamp")
    val timestamp: Int? = null,

    @field:SerializedName("formatted")
    val formatted: String? = null
)

