package com.github.klee0kai.thekey.app.ui.changepassw.presenter

import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.ui.changepassw.model.ChangePasswordStorageState
import com.github.klee0kai.thekey.app.ui.storage.model.StorageItem
import com.github.klee0kai.thekey.app.ui.storage.model.storageItem
import com.github.klee0kai.thekey.core.domain.model.ColorGroup
import com.github.klee0kai.thekey.core.domain.model.ColoredNote
import com.github.klee0kai.thekey.core.domain.model.ColoredOtpNote
import com.github.klee0kai.thekey.core.ui.devkit.color.KeyColor
import com.github.klee0kai.thekey.core.utils.common.Dummy
import com.github.klee0kai.thekey.core.utils.common.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

open class ChangeStoragePasswordPresenterDummy(
    state: ChangePasswordStorageState = ChangePasswordStorageState(),
    val notesCount: Int = 1,
) : ChangeStoragePasswordPresenter {

    private val scope = DI.defaultThreadScope()

    override val state = MutableStateFlow(state)

    override val filteredItems = MutableStateFlow(
        if (notesCount < 0) {
            null
        } else {
            buildList<StorageItem> {
                repeat(notesCount) {
                    add(
                        if (Random.nextBoolean()) {
                            ColoredNote(
                                ptnote = Dummy.dummyId,
                                site = "some${it}.site",
                                login = "login${it}",
                                desc = "description${it}",
                                group = ColorGroup(
                                    id = Dummy.dummyId,
                                    keyColor = KeyColor.colors.random()
                                ),
                                isLoaded = true,
                            ).storageItem()
                        } else {
                            ColoredOtpNote(
                                ptnote = Dummy.dummyId,
                                issuer = "issuer${it}",
                                name = "otp_name${it}",
                                isLoaded = true,
                            ).storageItem()
                        }
                    )
                }
            }
        }
    )

    override fun input(
        block: ChangePasswordStorageState.() -> ChangePasswordStorageState,
    ) = scope.launch {
        state.update { block(it) }
    }

}