# TheKey 

Менеджер паролей

## Структура проекта

    - Android проект (корневая папка, gradle сборка)
    - tkcore (терминальная версия - cmake сборка)


## Сборка

Перед сборкой в проекте должен быть файл local.properties к примеру:

    sdk.dir=/home/<user>/Android/Sdk
    android.ndkVersion=21.1.6352462 
    android.useAndroidX=true
    android.enableJetifier=true

Сборка проекта упрощена системами сборки cmake и gradle. Для настройки окружение используйте скрипт
env_build.sh

        . env_build.sh
        download_openssl
        build_openssl_cur_os
        build_openssl_android_all

        build_term_app # терминальная утилита 
        build_apk # сборка apk 



## License
```
Copyright (c) 2022 Andrey Kuzubov
```
