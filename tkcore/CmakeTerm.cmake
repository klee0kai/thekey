
add_executable(tkey
        ${SRC_FILES}
        term/cmd_processing.cpp
        term/term_utils.cpp
        main.cpp
        )

message("include dirs ${OPENSSL_LIB_PATH}/include")

target_include_directories(tkey PUBLIC cpp_lib term)


target_link_libraries(tkey
        openssl
        openssl_crypto
        dl
        pthread
        )

install(TARGETS tkey DESTINATION ${PROJECT_SOURCE_DIR}/build/${CMAKE_SYSTEM_NAME}/${CMAKE_SYSTEM_PROCESSOR})


