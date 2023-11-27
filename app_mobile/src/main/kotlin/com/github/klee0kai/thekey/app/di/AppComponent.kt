package com.github.klee0kai.thekey.app.di

import com.github.klee0kai.stone.Stone
import com.github.klee0kai.stone.annotations.component.Component
import com.github.klee0kai.stone.annotations.module.BindInstance
import com.github.klee0kai.thekey.app.App

val DI: AppComponent = Stone.createComponent(AppComponent::class.java)

@Component
interface AppComponent {

    open fun coroutineModule(): CoroutineModule

    @BindInstance(cache = BindInstance.CacheType.Weak)
    fun app(app: App? = null): App

}