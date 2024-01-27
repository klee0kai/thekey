add_executable(tkey
        utils/term_utils.cpp
        utils/interactive.cpp
        termk1.cpp
        termk2.cpp
        main.cpp
)


target_link_libraries(tkey
        thekey_find
        thekey_storage_v1
        thekey_storage_v2

        openssl
        openssl_crypto
        dl
        pthread
)

install(TARGETS tkey DESTINATION ${PROJECT_SOURCE_DIR}/build/${CMAKE_SYSTEM_NAME}/${CMAKE_SYSTEM_PROCESSOR})