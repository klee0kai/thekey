
find_package(Protobuf REQUIRED)
protobuf_generate_cpp(PROTO_SRCS PROTO_HDRS ${CMAKE_CURRENT_LIST_DIR}/proto/OtpMigration.proto)

add_library(otpauth
        # Core
        ${PROTO_SRCS}
        ${CMAKE_CURRENT_LIST_DIR}/tools/uri.cpp
        ${CMAKE_CURRENT_LIST_DIR}/tools/base64.cpp
        ${CMAKE_CURRENT_LIST_DIR}/tools/base32.cpp
        ${CMAKE_CURRENT_LIST_DIR}/otpauthuri.cpp
        ${CMAKE_CURRENT_LIST_DIR}/gauthmigration.cpp
        ${CMAKE_CURRENT_LIST_DIR}/otp.cpp
)

set_target_properties(otpauth PROPERTIES INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_CURRENT_LIST_DIR})

target_include_directories(otpauth PUBLIC
        ${CMAKE_CURRENT_LIST_DIR}
        ${Protobuf_INCLUDE_DIRS}
        ${CMAKE_CURRENT_BINARY_DIR}
)

target_link_libraries(otpauth PUBLIC
        key_core
        protobuf
)