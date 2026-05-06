package com.icc.eserviceshelper.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val id: String = "",
    val title: String = "",
    val icon_url: String = "",
    val items: Map<String, ServiceItem>? = null
) : Parcelable
