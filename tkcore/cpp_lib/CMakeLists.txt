

add_library(lib_tkcore
        # Core
        ${CMAKE_CURRENT_LIST_DIR}/core/common.cpp
        ${CMAKE_CURRENT_LIST_DIR}/core/key_signatures.cpp
        # find
        ${CMAKE_CURRENT_LIST_DIR}/find/key_storage_v1.cpp
        ${CMAKE_CURRENT_LIST_DIR}/find/key_storage_v2.cpp
        ${CMAKE_CURRENT_LIST_DIR}/find/find.cpp

        # storage 1
        ${CMAKE_CURRENT_LIST_DIR}/storage1/pass_spliter_v1.cpp
        ${CMAKE_CURRENT_LIST_DIR}/storage1/salt_test1.cpp
        ${CMAKE_CURRENT_LIST_DIR}/storage1/key1.cpp

        # storage 2

        ${CMAKE_CURRENT_LIST_DIR}/storage2/salt_text/salt2.cpp
        ${CMAKE_CURRENT_LIST_DIR}/storage2/salt_text/salt2_schema.cpp
        ${CMAKE_CURRENT_LIST_DIR}/storage2/salt_text/salt_base.cpp
        ${CMAKE_CURRENT_LIST_DIR}/storage2/split_password.cpp
        ${CMAKE_CURRENT_LIST_DIR}/storage2/storage_structure.cpp
        ${CMAKE_CURRENT_LIST_DIR}/storage2/key2.cpp
)

set_target_properties(lib_tkcore PROPERTIES INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_CURRENT_LIST_DIR})

include_directories(lib_tkcore ${CMAKE_CURRENT_LIST_DIR})

target_link_libraries(lib_tkcore openssl_interface)