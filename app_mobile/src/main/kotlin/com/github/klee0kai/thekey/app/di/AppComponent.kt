package com.github.klee0kai.thekey.app.di

import com.github.klee0kai.stone.KotlinWrappersStone
import com.github.klee0kai.stone.Stone
import com.github.klee0kai.stone.annotations.component.Component
import com.github.klee0kai.stone.annotations.component.ExtendOf
import com.github.klee0kai.thekey.app.BuildConfig
import com.github.klee0kai.thekey.app.di.debug.DebugDI
import com.github.klee0kai.thekey.app.di.debug.DebugDI.initDummyModules
import com.github.klee0kai.thekey.app.di.dependencies.AppComponentProviders
import com.github.klee0kai.thekey.app.features.allFeatures
import com.github.klee0kai.thekey.app.features.model.findApi
import com.github.klee0kai.thekey.core.di.CoreComponent
import com.github.klee0kai.thekey.core.di.CoreDI
import com.github.klee0kai.thekey.core.di.hardResetToPreview
import com.github.klee0kai.thekey.core.di.identifiers.NoteGroupIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.NoteIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.PluginIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.di.wrap.AppWrappersStone
import com.github.klee0kai.thekey.core.domain.model.AppConfig
import com.github.klee0kai.thekey.core.feature.model.DynamicFeature
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly

var DI: AppComponent = initAppComponent()
    private set

@Component(
    identifiers = [
        StorageIdentifier::class,
        NoteIdentifier::class,
        NoteGroupIdentifier::class,
        PluginIdentifier::class,
        DynamicFeature::class,
    ],
    wrapperProviders = [
        KotlinWrappersStone::class,
        AppWrappersStone::class,
    ],
)
interface AppComponent : CoreComponent, AppComponentModules, AppComponentProviders {

    @ExtendOf
    fun ext(component: CoreComponent)

}

@DebugOnly
fun AppComponent.hardResetToPreview() {
    val ctx = ctx()
    val config = config()

    CoreDI.hardResetToPreview()
    DI = initAppComponent()

    DI.ctx(ctx)
    DI.config(config)
    DI.initDummyModules()
}

fun AppComponent.updateComponentsSoft() {
    gcWeak()
    if (BuildConfig.DEBUG) {
        with(DebugDI) { initDI() }
    }

    DynamicFeature
        .allFeatures()
        .forEach { feature ->
            with(feature.findApi() ?: return@forEach) {
                initDI()
            }
        }
}

private fun initAppComponent() = Stone.createComponent(AppComponent::class.java).apply {
    ext(CoreDI)
    config(AppConfig(isDebug = BuildConfig.DEBUG))
    updateComponentsSoft()
}
