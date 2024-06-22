package com.github.klee0kai.thekey.core.di

import android.content.Context
import androidx.activity.ComponentActivity
import com.github.klee0kai.stone.KotlinWrappersStone
import com.github.klee0kai.stone.Stone
import com.github.klee0kai.stone.annotations.component.Component
import com.github.klee0kai.stone.annotations.component.GcWeakScope
import com.github.klee0kai.stone.annotations.component.RunGc
import com.github.klee0kai.stone.annotations.module.BindInstance
import com.github.klee0kai.thekey.core.di.debug.DummyCoreAndroidHelpersModule
import com.github.klee0kai.thekey.core.di.dependecies.CoreDependencyProvider
import com.github.klee0kai.thekey.core.di.identifiers.ActivityIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.NoteGroupIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.NoteIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.PluginIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.StorageGroupIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.di.wrap.AppWrappersStone
import com.github.klee0kai.thekey.core.domain.model.AppConfig
import com.github.klee0kai.thekey.core.domain.model.feature.model.DynamicFeature
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly


var CoreDI: CoreComponent = initCoreComponent()
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
interface CoreComponent : CoreDependencyProvider, CoreComponentModules {

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
fun CoreComponent.hardResetToPreview() {
    val ctx = ctx()
    val config = config()

    CoreDI = initCoreComponent()

    CoreDI.ctx(ctx)
    CoreDI.config(config)

    initDummyModule()
}

@DebugOnly
fun CoreComponent.initDummyModule() {
    initCoreAndroidHelpersModule(
        DummyCoreAndroidHelpersModule(origin = coreAndroidHelpersModuleFactory())
    )
}

private fun initCoreComponent() = Stone.createComponent(CoreComponent::class.java).apply {
    config(AppConfig())
}