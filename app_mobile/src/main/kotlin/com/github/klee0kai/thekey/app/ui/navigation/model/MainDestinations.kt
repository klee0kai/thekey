package com.github.klee0kai.thekey.app.ui.navigation.model

import com.github.klee0kai.thekey.app.engine.model.DecryptedNote
import com.github.klee0kai.thekey.app.engine.model.DecryptedOtpNote
import com.github.klee0kai.thekey.app.features.autofill
import com.github.klee0kai.thekey.app.features.gdrive
import com.github.klee0kai.thekey.app.features.qrcodeScanner
import com.github.klee0kai.thekey.app.features.smpassw
import com.github.klee0kai.thekey.app.ui.noteedit.model.EditTabs
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.domain.model.feature.model.DynamicFeature
import com.github.klee0kai.thekey.core.ui.navigation.model.Destination
import com.github.klee0kai.thekey.core.ui.navigation.model.DialogDestination
import com.github.klee0kai.thekey.core.ui.navigation.model.DynamicDestination
import kotlinx.parcelize.Parcelize


object MainDestinations {
    val InitDest = LoginDestination()
}

@Parcelize
data object EmptyDestination : Destination

@Parcelize
data class LoginDestination(
    val identifier: StorageIdentifier = StorageIdentifier(),
    val prefilledPassw: String? = null,
    val forceAllowStorageSelect: Boolean = false,
) : Destination

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
data class ChangeStoragePasswordDestination(
    /**
     * storage path
     */
    val path: String = ""
) : Destination

@Parcelize
data class EditStorageGroupDestination(
    /**
     * group id
     */
    val groupId: Long? = null,
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
data class NoteDestination(
    /**
     * storage path
     */
    val path: String = "",
    /**
     * Storage version
     */
    val storageVersion: Int = 0,

    /**
     * note id
     */
    val notePtr: Long? = null,

    /**
     * otp note id
     */
    val otpNotePtr: Long? = null,
) : DialogDestination

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

    /**
     * Prevent delete note
     */
    val isIgnoreRemove: Boolean = false,
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
data object SubscriptionsDestination : Destination

@Parcelize
data class PluginDestination(
    val feature: DynamicFeature,
) : Destination

@Parcelize
data object QRCodeScanDestination : DynamicDestination(DynamicFeature.qrcodeScanner())

@Parcelize
data object AutoFillSettingsDestination : DynamicDestination(DynamicFeature.autofill())

@Parcelize
data object BackupSettings : DynamicDestination(DynamicFeature.gdrive())

@Parcelize
data class BackupStorageDestination(
    val storageIdentifier: StorageIdentifier = StorageIdentifier(),
) : DynamicDestination(DynamicFeature.gdrive())


@Parcelize
data class PasswordTwinsDestination(
    val storageIdentifier: StorageIdentifier = StorageIdentifier(),
) : DynamicDestination(DynamicFeature.smpassw())

