
set(KEYFIND_SRC
        # find
        ${CMAKE_CURRENT_LIST_DIR}/key_storage_v1.cpp
        ${CMAKE_CURRENT_LIST_DIR}/key_storage_v2.cpp
        ${CMAKE_CURRENT_LIST_DIR}/key_find.cpp
)


add_library(key_find_static ${KEYFIND_SRC})

set_target_properties(key_find_static PROPERTIES INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_CURRENT_LIST_DIR})

include_directories(key_find_static ${CMAKE_CURRENT_LIST_DIR})

target_link_libraries(key_find_static openssl_interface thekey_core_static)


# -----  SHARED -----
add_library(key_find SHARED ${KEYFIND_SRC})
include_directories(key_find ${CMAKE_CURRENT_LIST_DIR})
target_link_libraries(key_find openssl_interface thekey_core_static)