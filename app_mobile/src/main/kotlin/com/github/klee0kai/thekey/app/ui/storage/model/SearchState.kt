package com.github.klee0kai.thekey.app.ui.storage.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SearchState(
    val isActive: Boolean = false,
    val searchText: String = "",
) : Parcelable
