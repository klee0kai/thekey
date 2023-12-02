cmake_minimum_required(VERSION 3.10)

project("thekey")

get_filename_component(BROOKLYN_FILE ../../../build/generated/sources/${BROOKLYN_FOLDER}/brooklyn/FindBrooklynBridge.cmake ABSOLUTE)
message(BROOKLYN_FILE = ${BROOKLYN_FILE})
if (EXISTS ${BROOKLYN_FILE})
    include(${BROOKLYN_FILE})
endif ()

find_library(log-lib log)

add_library(crypt-storage-lib SHARED
        ${BROOKLYN_SRC}
        find-storages.cpp
        )

target_include_directories(crypt-storage-lib
        PUBLIC
        ${BROOKLYN_INCLUDE_DIRS}
        )

target_link_libraries(crypt-storage-lib ${log-lib})