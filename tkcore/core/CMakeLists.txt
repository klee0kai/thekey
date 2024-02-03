



add_library(key_core
        # Core
        ${CMAKE_CURRENT_LIST_DIR}/common.cpp
        ${CMAKE_CURRENT_LIST_DIR}/key_signatures.cpp
        ${CMAKE_CURRENT_LIST_DIR}/key_errors.cpp
        ${CMAKE_CURRENT_LIST_DIR}/salt/salt_base.cpp
)

set_target_properties(key_core PROPERTIES INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_CURRENT_LIST_DIR})

include_directories(key_core ${CMAKE_CURRENT_LIST_DIR})

target_link_libraries(key_core PUBLIC
        openssl_interface
        openssl
        openssl_crypto
)