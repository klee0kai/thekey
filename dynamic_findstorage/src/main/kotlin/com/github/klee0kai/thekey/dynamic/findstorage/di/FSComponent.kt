package com.github.klee0kai.thekey.dynamic.findstorage.di

import com.github.klee0kai.stone.KotlinWrappersStone
import com.github.klee0kai.stone.Stone
import com.github.klee0kai.stone.annotations.component.Component
import com.github.klee0kai.thekey.app.di.DI
import com.github.klee0kai.thekey.app.di.dependencies.AppComponentProviders
import com.github.klee0kai.thekey.core.di.CoreDI
import com.github.klee0kai.thekey.core.di.dependecies.CoreDependencyProvider
import com.github.klee0kai.thekey.core.di.hardResetToPreview
import com.github.klee0kai.thekey.core.di.identifiers.ActivityIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.NoteGroupIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.NoteIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.PluginIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.StorageGroupIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.di.initDummyModule
import com.github.klee0kai.thekey.core.di.wrap.AppWrappersStone
import com.github.klee0kai.thekey.core.domain.model.feature.model.DynamicFeature
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly
import com.github.klee0kai.thekey.dynamic.findstorage.di.deps.FSProviders

var FSDI: FindStorageComponent = initFindStorageComponent()
    private set

@Component(
    identifiers = [
        ActivityIdentifier::class,
        StorageIdentifier::class,
        NoteIdentifier::class,
        NoteGroupIdentifier::class,
        StorageGroupIdentifier::class,
        PluginIdentifier::class,
        DynamicFeature::class,
    ],
    wrapperProviders = [
        KotlinWrappersStone::class,
        AppWrappersStone::class,
    ],
)
interface FindStorageComponent : FSProviders, FSModules, AppComponentProviders, CoreDependencyProvider {

}

@DebugOnly
fun FindStorageComponent.hardResetToPreview() {
    DI.hardResetToPreview()
    FSDI = initFindStorageComponent()

    CoreDI.initDummyModule()
}

private fun initFindStorageComponent() = Stone.createComponent(FindStorageComponent::class.java).apply {
    initCoreDeps(CoreDI)
    initAppDeps(DI)
}