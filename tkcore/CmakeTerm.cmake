
add_executable(tkey
        ${SRC_FILES}
        term/cmd_processing.cpp
        term/term_utils.cpp
        main.cpp
        )

message("include dirs  ${OPENSSL_LIB_PATH}/include")

target_include_directories(tkey PUBLIC
        ${OPENSSL_LIB_PATH}/include
        cpp_lib
        term)


target_link_libraries(tkey
        ${OPENSSL_LIB_PATH}/current-os/lib/libssl.a
        ${OPENSSL_LIB_PATH}/current-os/lib/libcrypto.a
        dl
        pthread
        )

install(TARGETS tkey DESTINATION ${PROJECT_SOURCE_DIR}/build/${CMAKE_SYSTEM_NAME}/${CMAKE_SYSTEM_PROCESSOR})


