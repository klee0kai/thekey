add_executable(tkey
        utils/term_utils.cpp
        utils/interactive.cpp
        cmd_processing.cpp
        interactive.cpp
        storage_v1.cpp
        storage_v2.cpp
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