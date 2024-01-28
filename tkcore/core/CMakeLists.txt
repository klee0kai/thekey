



add_library(thekey_core_static
        # Core
        ${CMAKE_CURRENT_LIST_DIR}/common.cpp
        ${CMAKE_CURRENT_LIST_DIR}/key_signatures.cpp
        ${CMAKE_CURRENT_LIST_DIR}/salt/salt_base.cpp
)

set_target_properties(thekey_core_static PROPERTIES INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_CURRENT_LIST_DIR})

include_directories(thekey_core_static ${CMAKE_CURRENT_LIST_DIR})