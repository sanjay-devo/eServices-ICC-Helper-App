package com.icc.eserviceshelper.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ServiceItem(
    val id: String = "",
    val title: String = "",
    val pdf_url: String = "",
    val keywords: List<String>? = null
) : Parcelable
