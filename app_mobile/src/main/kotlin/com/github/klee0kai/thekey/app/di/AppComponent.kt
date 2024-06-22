package com.github.klee0kai.thekey.app.di

import com.github.klee0kai.stone.KotlinWrappersStone
import com.github.klee0kai.stone.Stone
import com.github.klee0kai.stone.annotations.component.Component
import com.github.klee0kai.stone.annotations.component.ExtendOf
import com.github.klee0kai.thekey.app.BuildConfig
import com.github.klee0kai.thekey.app.di.debug.DebugDI
import com.github.klee0kai.thekey.app.di.debug.DebugDI.initDummyModules
import com.github.klee0kai.thekey.app.di.dependencies.AppComponentProviders
import com.github.klee0kai.thekey.app.di.modules.CoreAndroidHelpersModuleImpl
import com.github.klee0kai.thekey.app.features.allFeatures
import com.github.klee0kai.thekey.app.features.model.findApi
import com.github.klee0kai.thekey.app.ui.navigation.deeplink.configMainDeeplinks
import com.github.klee0kai.thekey.core.di.CoreComponent
import com.github.klee0kai.thekey.core.di.CoreDI
import com.github.klee0kai.thekey.core.di.hardResetToPreview
import com.github.klee0kai.thekey.core.di.identifiers.ActivityIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.NoteGroupIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.NoteIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.PluginIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.StorageGroupIdentifier
import com.github.klee0kai.thekey.core.di.identifiers.StorageIdentifier
import com.github.klee0kai.thekey.core.di.initDummyModule
import com.github.klee0kai.thekey.core.di.wrap.AppWrappersStone
import com.github.klee0kai.thekey.core.domain.model.AppConfig
import com.github.klee0kai.thekey.core.domain.model.feature.model.DynamicFeature
import com.github.klee0kai.thekey.core.utils.annotations.DebugOnly

var DI: AppComponent = initAppComponent()
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
interface AppComponent : AppComponentProviders, AppComponentModules, CoreComponent {

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

    CoreDI.initDummyModule()
    DI.initDummyModules()
}

fun AppComponent.updateComponentsSoft() {
    gcWeak()
    if (BuildConfig.DEBUG) {
        with(DebugDI) { initDI() }
    }

    val availableFeatures = DynamicFeature
        .allFeatures()
        .filter { it.isCommunity || billingInteractor().isAvailable(it) }
        .mapNotNull { it.findApi() }

    availableFeatures
        .forEach { feature -> with(feature) { initDI() } }
}

fun AppComponent.configRouting(
    activityIdentifier: ActivityIdentifier? = null,
) {
    val availableFeatures = DynamicFeature
        .allFeatures()
        .filter { it.isCommunity || billingInteractor().isAvailable(it) }
        .mapNotNull { it.findApi() }

    // init new deeplinks routing
    router(activityIdentifier).configDeeplinks {
        availableFeatures
            .forEach { feature -> with(feature) { configDeeplinks() } }

        configMainDeeplinks()
    }
}

private fun initAppComponent() = Stone.createComponent(AppComponent::class.java).apply {
    ext(CoreDI)
    with(CommercialDIInit) { initDI() }

    initCoreAndroidHelpersModule(
        CoreAndroidHelpersModuleImpl(
            origin = coreAndroidHelpersModuleFactory()
        )
    )
    config(AppConfig(isDebug = BuildConfig.DEBUG))
    updateComponentsSoft()
}
