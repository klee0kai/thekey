#cmake --help-module FindProtobuf
find_package(Protobuf)

if (Protobuf_FOUND)
    protobuf_generate_cpp(PROTO_SRCS PROTO_HDRS ${CMAKE_CURRENT_LIST_DIR}/proto/OtpMigration.proto)
endif ()

add_library(otpauth
        ${PROTO_SRCS}
        ${CMAKE_CURRENT_LIST_DIR}/tools/uri.cpp
        ${CMAKE_CURRENT_LIST_DIR}/tools/base64.cpp
        ${CMAKE_CURRENT_LIST_DIR}/tools/base32.cpp
        ${CMAKE_CURRENT_LIST_DIR}/otpauthuri.cpp
        ${CMAKE_CURRENT_LIST_DIR}/gauthmigration.cpp
        ${CMAKE_CURRENT_LIST_DIR}/otp.cpp
)

set_target_properties(otpauth PROPERTIES INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_CURRENT_LIST_DIR})
if (Protobuf_FOUND)
    set_target_properties(otpauth PROPERTIES COMPILE_FLAGS "-DProtobuf_FOUND=true ")

    target_link_libraries(otpauth PUBLIC
            protobuf
            )
endif ()

target_include_directories(otpauth PUBLIC
        ${CMAKE_CURRENT_LIST_DIR}
        ${Protobuf_INCLUDE_DIRS}
        ${CMAKE_CURRENT_BINARY_DIR}
)

target_link_libraries(otpauth PUBLIC
        key_core
)