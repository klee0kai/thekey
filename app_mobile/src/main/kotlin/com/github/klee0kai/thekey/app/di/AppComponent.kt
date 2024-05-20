package com.github.klee0kai.thekey.app.di

import android.content.Context
import androidx.activity.ComponentActivity
import com.github.klee0kai.stone.KotlinWrappersStone
import com.github.klee0kai.stone.Stone
import com.github.klee0kai.stone.annotations.component.Component
import com.github.klee0kai.stone.annotations.component.GcWeakScope
import com.github.klee0kai.stone.annotations.component.RunGc
import com.github.klee0kai.stone.annotations.module.BindInstance
import com.github.klee0kai.thekey.app.BuildConfig
import com.github.klee0kai.thekey.app.di.debug.DebugDI
import com.github.klee0kai.thekey.app.di.debug.DebugDI.initDummyModules
import com.github.klee0kai.thekey.app.di.dependencies.AppComponentProviders
import com.github.klee0kai.thekey.app.di.identifier.NoteGroupIdentifier
import com.github.klee0kai.thekey.app.di.identifier.NoteIdentifier
import com.github.klee0kai.thekey.app.di.identifier.PluginIdentifier
import com.github.klee0kai.thekey.app.di.identifier.StorageIdentifier
import com.github.klee0kai.thekey.app.di.wrap.AppWrappersStone
import com.github.klee0kai.thekey.app.domain.model.AppConfig
import com.github.klee0kai.thekey.app.features.allFeatures
import com.github.klee0kai.thekey.app.features.model.DynamicFeature
import com.github.klee0kai.thekey.app.features.model.findApi
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
interface AppComponent : AppComponentModules, AppComponentProviders {

    @BindInstance(cache = BindInstance.CacheType.Weak)
    fun ctx(ctx: Context? = null): Context

    @BindInstance(cache = BindInstance.CacheType.Weak)
    fun activity(app: ComponentActivity? = null): ComponentActivity?

    @BindInstance(cache = BindInstance.CacheType.Strong)
    fun config(snackbarHostState: AppConfig? = null): AppConfig

    @RunGc
    @GcWeakScope
    fun gcWeak()

}

@DebugOnly
fun AppComponent.hardResetToPreview() {
    val ctx = DI.ctx()

    DI = initAppComponent()
    DI.ctx(ctx)
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
    config(AppConfig())
    updateComponentsSoft()
}
