

add_library(thekey_storage_v2
       
        # storage 2
        ${CMAKE_CURRENT_LIST_DIR}/salt_text/salt2.cpp
        ${CMAKE_CURRENT_LIST_DIR}/salt_text/salt2_schema.cpp
        ${CMAKE_CURRENT_LIST_DIR}/salt_text/salt_base.cpp
        ${CMAKE_CURRENT_LIST_DIR}/split_password.cpp
        ${CMAKE_CURRENT_LIST_DIR}/storage_structure.cpp
        ${CMAKE_CURRENT_LIST_DIR}/key2.cpp
)

set_target_properties(thekey_storage_v2 PROPERTIES INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_CURRENT_LIST_DIR})

include_directories(thekey_storage_v2 ${CMAKE_CURRENT_LIST_DIR})

target_link_libraries(thekey_storage_v2 openssl_interface thekey_core)