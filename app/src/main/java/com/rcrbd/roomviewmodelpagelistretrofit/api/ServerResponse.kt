package com.rcrbd.roomviewmodelpagelistretrofit.api

import com.google.gson.annotations.SerializedName
import com.rcrbd.roomviewmodelpagelistretrofit.model.Repo

data class ServerResponse (
    @SerializedName("total_count") val total: Int = 0,
    @SerializedName("items") val items: List<Repo> = emptyList(),
    val nextPage: Int? = null
)
