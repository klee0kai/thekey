add_executable(tkey
        utils/term_utils.cpp
        utils/interactive.cpp
        termk1.cpp
        termk2.cpp
        main.cpp
)


target_link_libraries(tkey
        key_find_static
        key_storage1_static
        key_storage2_static

        openssl
        openssl_crypto
        dl
        pthread
)

install(TARGETS tkey DESTINATION ${PROJECT_SOURCE_DIR}/build/${CMAKE_SYSTEM_NAME}/${CMAKE_SYSTEM_PROCESSOR})