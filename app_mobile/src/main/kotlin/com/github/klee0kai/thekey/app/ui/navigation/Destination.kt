package com.github.klee0kai.thekey.app.ui.navigation

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

sealed interface Destination : Parcelable {

    @Parcelize
    data object LoginScreen : Destination

    @Parcelize
    data object StoragesScreen : Destination

}