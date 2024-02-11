
add_library(otpauth
        # Core
        ${CMAKE_CURRENT_LIST_DIR}/tools/uri.cpp
        ${CMAKE_CURRENT_LIST_DIR}/tools/base64.cpp
        ${CMAKE_CURRENT_LIST_DIR}/tools/base32.cpp
        ${CMAKE_CURRENT_LIST_DIR}/otpauthuri.cpp
        ${CMAKE_CURRENT_LIST_DIR}/otp.cpp
)

set_target_properties(otpauth PROPERTIES INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_CURRENT_LIST_DIR})

include_directories(otpauth ${CMAKE_CURRENT_LIST_DIR})

target_link_libraries(otpauth PUBLIC
        key_core
)