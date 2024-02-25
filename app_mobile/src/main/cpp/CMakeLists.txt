cmake_minimum_required(VERSION 3.10)

project("thekey")

set(CMAKE_CXX_STANDARD 17)

SET(CMAKE_CXX_FLAGS "-g -Wall  -std=c++17 -pthread -fPIC")
SET(CMAKE_C_FLAGS "-g -Wall  -pthread -fPIC")

get_filename_component(BROOKLYN_FILE ../../../build/generated/sources/${BROOKLYN_FOLDER}/brooklyn/FindBrooklynBridge.cmake ABSOLUTE)
message(BROOKLYN_FILE = ${BROOKLYN_FILE})
include(${BROOKLYN_FILE})


include(../../../../tkcore/FindOpensslLib.cmake)
include(../../../../tkcore/FindTheKeyLib.cmake)
find_library(log-lib log)

add_library(crypt-storage-lib SHARED
        ${BROOKLYN_SRC}
        find-storages.cpp
        edit_storage.cpp
        cryp_storage.cpp
        )

target_include_directories(crypt-storage-lib
        PUBLIC
        ${BROOKLYN_INCLUDE_DIRS}
        )

target_link_libraries(crypt-storage-lib
        ${log-lib}

        key_find_static
        key_storage1_static
        key_storage2_static

        )


# for https://github.com/android/ndk/issues/517
set(CMAKE_FIND_ROOT_PATH_MODE_PROGRAM BOTH)
set(CMAKE_FIND_ROOT_PATH_MODE_LIBRARY BOTH)
set(CMAKE_FIND_ROOT_PATH_MODE_INCLUDE BOTH)
set(CMAKE_FIND_ROOT_PATH_MODE_PACKAGE BOTH)
