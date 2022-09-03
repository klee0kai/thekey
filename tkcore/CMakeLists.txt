cmake_minimum_required(VERSION 3.4.1)
project(TheKey)

set(CMAKE_CXX_STANDARD 17)

SET(CMAKE_CXX_FLAGS "-g -Wall  -std=c++11 -pthread ")

## prepate ASan (AddressSanitizer)
#set (CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -fno-omit-frame-pointer -fsanitize=address")
#set (CMAKE_LINKER_FLAGS_DEBUG "${CMAKE_LINKER_FLAGS_DEBUG} -fno-omit-frame-pointer -fsanitize=address")


set(SRC_FILES
        cpp_lib/libs/pass_spliter.cpp
        cpp_lib/libs/utils.cpp
        cpp_lib/salt_text/s_text.cpp
        cpp_lib/dll_interface/key_manager_ctx.cpp
        cpp_lib/dll_interface/key_finder.cpp
        )

set(OPENSSL_LIB_PATH ${PROJECT_SOURCE_DIR}/../prebuild/openssl)
set(SYSTEM_INFO_TYPE ${CMAKE_HOST_SYSTEM_NAME}_${CMAKE_SYSTEM_PROCESSOR})

include(FindOpensslLib.cmake)

message(SYSTEM_INFO_TYPE = ${SYSTEM_INFO_TYPE})
message(OPENSSL_LIB_PATH = ${OPENSSL_LIB_PATH})

if (${ANDROID})
    include(CmakeAndroid.cmake)

else ()
    include(CmakeTerm.cmake)
    include(CmakeTests.cmake)
endif ()


