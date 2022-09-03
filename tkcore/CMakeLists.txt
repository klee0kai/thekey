cmake_minimum_required(VERSION 3.4.1)
project(TheKey)

set(CMAKE_CXX_STANDARD 17)

SET(CMAKE_CXX_FLAGS "-g -Wall  -std=c++11 -pthread ")

## prepate ASan (AddressSanitizer)
#set (CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -fno-omit-frame-pointer -fsanitize=address")
#set (CMAKE_LINKER_FLAGS_DEBUG "${CMAKE_LINKER_FLAGS_DEBUG} -fno-omit-frame-pointer -fsanitize=address")


set(OPENSSL_LIB_PATH ${PROJECT_SOURCE_DIR}/../prebuild/openssl)
set(SYSTEM_INFO_TYPE ${CMAKE_HOST_SYSTEM_NAME}_${CMAKE_SYSTEM_PROCESSOR})
message(SYSTEM_INFO_TYPE = ${SYSTEM_INFO_TYPE})
message(OPENSSL_LIB_PATH = ${OPENSSL_LIB_PATH})

include(FindOpensslLib.cmake)


add_subdirectory(cpp_lib)

if (${ANDROID})
    add_subdirectory(android)

else ()
    add_subdirectory(term)
    add_subdirectory(test)

endif ()


