add_executable(tkey
        cmd_processing.cpp
        term_utils.cpp
        main.cpp
        )


target_link_libraries(tkey
        lib_tkcore
        openssl
        openssl_crypto
        dl
        pthread
        )

install(TARGETS tkey DESTINATION ${PROJECT_SOURCE_DIR}/build/${CMAKE_SYSTEM_NAME}/${CMAKE_SYSTEM_PROCESSOR})