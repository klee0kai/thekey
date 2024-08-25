package com.github.klee0kai.thekey.app.ui.navigationboard.presenter

import com.github.klee0kai.thekey.core.domain.model.ColoredStorage
import com.thedeanda.lorem.LoremIpsum
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow

class NavigationBoardPresenterDummy(
    val hasCurrentStorage: Boolean = false,
    val favoriteCount: Int = 3,
    val opennedCount: Int = 3,
) : NavigationBoardPresenter {

    override val currentStorage = when {
        hasCurrentStorage -> MutableStateFlow(
            ColoredStorage(
                path = "/phoneStorage/Documents/pet.ckey",
                name = "petprojects",
                description = LoremIpsum().getWords(4),
            )
        )

        else -> emptyFlow()
    }

    override val openedStoragesFlow = MutableStateFlow(
        buildList {
            repeat(opennedCount) {
                add(
                    ColoredStorage(
                        path = "/phoneStorage/Documents/business-${it}.ckey",
                        name = "business",
                        description = LoremIpsum().getWords(4),
                    ),
                )
            }
        }
    )


    override val favoritesStorages = MutableStateFlow(
        buildList {
            repeat(favoriteCount) {
                add(
                    ColoredStorage(
                        path = "/phoneStorage/Documents/business-${it}.ckey",
                        name = "business",
                        description = LoremIpsum().getWords(4),
                    ),
                )
            }
        }
    )

}