package com.github.klee0kai.thekey.app.ui.navigation.model

import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedOtpNote
import com.github.klee0kai.thekey.app.features.autofill
import com.github.klee0kai.thekey.app.features.qrcodeScanner
import com.github.klee0kai.thekey.app.ui.note.model.EditTabs
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.feature.model.DynamicFeature
import com.github.klee0kai.thekey.core.ui.navigation.model.Destination
import com.github.klee0kai.thekey.core.ui.navigation.model.DialogDestination
import com.github.klee0kai.thekey.core.ui.navigation.model.DynamicDestination
import kotlinx.parcelize.Parcelize


object MainDestinations {
    val InitDest = AutoFillSettingsDestination
}

@Parcelize
data object EmptyDestination : Destination

@Parcelize
data object LoginDestination : Destination

@Parcelize
data object StoragesDestination : Destination

@Parcelize
data object SelectStorageDialogDestination : DialogDestination

@Parcelize
data class EditStorageDestination(
    /**
     * storage path
     */
    val path: String = ""
) : Destination

@Parcelize
data object DesignDestination : Destination

@Parcelize
data class StorageDestination(
    /**
     * storage path
     */
    val path: String = "",

    /**
     * Storage version
     */
    val version: Int = 0,

    /**
     * selected page
     */
    val selectedPage: Int = 0,
) : Destination

@Parcelize
data class EditNoteDestination(
    /**
     * storage path
     */
    val path: String = "",
    /**
     * Storage version
     */
    val storageVersion: Int = 0,

    /**
     * prefilled note
     */
    val note: DecryptedNote? = null,

    /**
     * prefilled note
     */
    val otpNote: DecryptedOtpNote? = null,

    /**
     * opened tab
     */
    val tab: EditTabs = EditTabs.Account,
) : Destination


@Parcelize
data class GenHistDestination(
    /**
     * storage path
     */
    val path: String = "",
    /**
     * Storage version
     */
    val storageVersion: Int = 0,
) : Destination


@Parcelize
data class EditNoteGroupDestination(
    /**
     * storage identifier
     */
    val storageIdentifier: StorageIdentifier = StorageIdentifier(),
    /**
     * group id
     */
    val groupId: Long? = null,
) : Destination

@Parcelize
data object AboutDestination : Destination

@Parcelize
data object SettingsDestination : Destination

@Parcelize
data object PluginsDestination : Destination

@Parcelize
data class PluginDestination(
    val feature: DynamicFeature,
) : Destination

@Parcelize
data object QRCodeScanDestination : DynamicDestination(DynamicFeature.qrcodeScanner())

@Parcelize
data object AutoFillSettingsDestination : DynamicDestination(DynamicFeature.autofill())

