add_library(lib_tkcore
        libs/pass_spliter.cpp
        libs/utils.cpp
        salt_text/s_text.cpp
        dll_interface/key_manager_ctx.cpp
        dll_interface/key_finder.cpp
        )

set_target_properties(lib_tkcore PROPERTIES
        INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_CURRENT_SOURCE_DIR}
        )

include_directories(lib_tkcore
        ${CMAKE_CURRENT_SOURCE_DIR}
        )


target_link_libraries(
        lib_tkcore
        openssl_interface
)