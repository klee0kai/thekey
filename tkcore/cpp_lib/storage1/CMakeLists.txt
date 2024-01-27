

add_library(thekey_storage_v1
        # storage 1
        ${CMAKE_CURRENT_LIST_DIR}/pass_spliter_v1.cpp
        ${CMAKE_CURRENT_LIST_DIR}/salt_test1.cpp
        ${CMAKE_CURRENT_LIST_DIR}/key1.cpp


)

set_target_properties(thekey_storage_v1 PROPERTIES INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_CURRENT_LIST_DIR})

include_directories(thekey_storage_v1 ${CMAKE_CURRENT_LIST_DIR})

target_link_libraries(thekey_storage_v1 openssl_interface thekey_core)