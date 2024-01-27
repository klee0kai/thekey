

add_library(thekey_core
        # Core
        ${CMAKE_CURRENT_LIST_DIR}/common.cpp
        ${CMAKE_CURRENT_LIST_DIR}/key_signatures.cpp
)

set_target_properties(thekey_core PROPERTIES INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_CURRENT_LIST_DIR})

include_directories(thekey_core ${CMAKE_CURRENT_LIST_DIR})