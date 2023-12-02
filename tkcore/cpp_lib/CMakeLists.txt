

add_library(lib_tkcore
        ${CMAKE_CURRENT_LIST_DIR}/libs/pass_spliter.cpp
        ${CMAKE_CURRENT_LIST_DIR}/libs/utils.cpp
        ${CMAKE_CURRENT_LIST_DIR}/salt_text/s_text.cpp
        ${CMAKE_CURRENT_LIST_DIR}/dll_interface/key_manager_ctx.cpp
        ${CMAKE_CURRENT_LIST_DIR}/dll_interface/key_finder.cpp
)

set_target_properties(lib_tkcore PROPERTIES INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_CURRENT_LIST_DIR})

include_directories(lib_tkcore ${CMAKE_CURRENT_LIST_DIR})

target_link_libraries(lib_tkcore openssl_interface)