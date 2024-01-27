

add_library(lib_tkcore
        ${CMAKE_CURRENT_LIST_DIR}/utils/pass_spliter_v1.cpp
        ${CMAKE_CURRENT_LIST_DIR}/utils/common.cpp
        ${CMAKE_CURRENT_LIST_DIR}/salt_text/salt2.cpp
        ${CMAKE_CURRENT_LIST_DIR}/salt_text/salt2_schema.cpp
        ${CMAKE_CURRENT_LIST_DIR}/salt_text/salt_test1.cpp
        ${CMAKE_CURRENT_LIST_DIR}/salt_text/salt_base.cpp
        ${CMAKE_CURRENT_LIST_DIR}/main/key_storage.cpp
        ${CMAKE_CURRENT_LIST_DIR}/main/storage1/key_storage_v1.cpp
        ${CMAKE_CURRENT_LIST_DIR}/main/storage2/split_password.cpp
        ${CMAKE_CURRENT_LIST_DIR}/main/storage2/storage.cpp
        ${CMAKE_CURRENT_LIST_DIR}/main/storage2/storage_structure.cpp
)

set_target_properties(lib_tkcore PROPERTIES INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_CURRENT_LIST_DIR})

include_directories(lib_tkcore ${CMAKE_CURRENT_LIST_DIR})

target_link_libraries(lib_tkcore openssl_interface)