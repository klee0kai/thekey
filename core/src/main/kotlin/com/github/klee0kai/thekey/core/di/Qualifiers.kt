package com.github.klee0kai.thekey.core.di

import javax.inject.Qualifier

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class MainDispatcher

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class IODispatcher

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher

@Qualifier
@MustBeDocumented
@Retention(AnnotationRetention.BINARY)
annotation class JniDispatcher

