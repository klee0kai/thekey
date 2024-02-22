
add_executable(testk2
        schemas/find_schemas_tests.cpp
        schemas/encoding_schemas_tests.cpp
        ${CMAKE_CURRENT_LIST_DIR}/gen_passw_tests.cpp
        ${CMAKE_CURRENT_LIST_DIR}/crypt_tests.cpp
        otp/uri_tests.cpp
        otp/otp_gen_tests.cpp
        otp/otp_uri_tests.cpp
        otp/auth_migration_test.cpp
)

target_link_libraries(testk2
        key_storage2_static

        ktest_core
)

set_target_properties(testk2 PROPERTIES COMPILE_FLAGS -DDEBUG_TEST)
gtest_discover_tests(testk2)