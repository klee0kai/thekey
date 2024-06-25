package com.github.klee0kai.thekey.app.domain

import com.github.klee0kai.thekey.core.domain.StartupInteractor

class AppStartupInteractor(
    val origin: StartupInteractor,
) : StartupInteractor by origin