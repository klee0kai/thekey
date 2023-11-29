cmake_minimum_required(VERSION 3.10)


include(../../../../tkcore/FindOpensslLib.cmake)
include(../../../../tkcore/FindTheKeyLib.cmake)

add_library(crypt-storage-lib
        SHARED
        mapping/jmapping.cpp
        native-crypt-storage.cpp
        native-find-storage.cpp
        )

find_library(log-lib log)
target_link_libraries(crypt-storage-lib
        ${log-lib}
        lib_tkcore
        openssl_android
        openssl_android_crypto)

# for https://github.com/android/ndk/issues/517
set(CMAKE_FIND_ROOT_PATH_MODE_PROGRAM BOTH)
set(CMAKE_FIND_ROOT_PATH_MODE_LIBRARY BOTH)
set(CMAKE_FIND_ROOT_PATH_MODE_INCLUDE BOTH)
set(CMAKE_FIND_ROOT_PATH_MODE_PACKAGE BOTH)


