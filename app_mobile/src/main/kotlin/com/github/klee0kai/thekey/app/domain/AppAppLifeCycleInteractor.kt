package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.core.domain.AppLifeCycleInteractor

class AppAppLifeCycleInteractor(
    val origin: AppLifeCycleInteractor,
) : AppLifeCycleInteractor by origin