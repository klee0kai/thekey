package com.kee0kai.thekey.di;

import com.github.klee0kai.stone.annotations.Component;
import com.github.klee0kai.stone.interfaces.IComponent;

@Component
public interface AppComponent extends IComponent {

    AppModule app();

    DomainModule domain();

    EngineModule engine();

}
