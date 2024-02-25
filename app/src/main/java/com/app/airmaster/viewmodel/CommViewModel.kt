package com.app.airmaster.viewmodel

import androidx.lifecycle.ViewModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull

open class CommViewModel : ViewModel() {

    val JSON = "application/json; charset=utf-8".toMediaTypeOrNull()
}