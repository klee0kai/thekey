
set(KEY2_SRC
        # storage 2
        ${CMAKE_CURRENT_LIST_DIR}/salt_text/salt2.cpp
        ${CMAKE_CURRENT_LIST_DIR}/salt_text/salt2_schema.cpp
        ${CMAKE_CURRENT_LIST_DIR}/split_password.cpp
        ${CMAKE_CURRENT_LIST_DIR}/storage_structure.cpp
        ${CMAKE_CURRENT_LIST_DIR}/key2.cpp
)

add_library(key_storage2_static ${KEY2_SRC})

set_target_properties(key_storage2_static PROPERTIES INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_CURRENT_LIST_DIR})

include_directories(key_storage2_static ${CMAKE_CURRENT_LIST_DIR})

target_link_libraries(key_storage2_static openssl_interface thekey_core_static)

#  ------------- SHARED -----------------
add_library(key_storage2 SHARED ${KEY2_SRC})
include_directories(key_storage2 ${CMAKE_CURRENT_LIST_DIR})
target_link_libraries(key_storage2 openssl_interface thekey_core_static)