

add_library(thekey_find
        # find
        ${CMAKE_CURRENT_LIST_DIR}/key_storage_v1.cpp
        ${CMAKE_CURRENT_LIST_DIR}/key_storage_v2.cpp
        ${CMAKE_CURRENT_LIST_DIR}/find.cpp

)

set_target_properties(thekey_find PROPERTIES INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_CURRENT_LIST_DIR})

include_directories(thekey_find ${CMAKE_CURRENT_LIST_DIR})

target_link_libraries(thekey_find openssl_interface thekey_core)