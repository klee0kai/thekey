
add_executable(testk2
        ${CMAKE_CURRENT_LIST_DIR}/gen_passw_tests.cpp
        ${CMAKE_CURRENT_LIST_DIR}/crypt_tests.cpp
        ${CMAKE_CURRENT_LIST_DIR}/schemas/find_schemas_tests.cpp
        ${CMAKE_CURRENT_LIST_DIR}/schemas/encoding_schemas_tests.cpp
        ${CMAKE_CURRENT_LIST_DIR}/schemas/unic_schemas_tests.cpp
        ${CMAKE_CURRENT_LIST_DIR}/otp/uri_parse_tests.cpp
        ${CMAKE_CURRENT_LIST_DIR}/otp/uri_export_tests.cpp
        ${CMAKE_CURRENT_LIST_DIR}/otp/otp_gen_tests.cpp
        ${CMAKE_CURRENT_LIST_DIR}/otp/yaotp_gen_aegis_tests.cpp
        ${CMAKE_CURRENT_LIST_DIR}/otp/otp_uri_tests.cpp
        ${CMAKE_CURRENT_LIST_DIR}/otp/auth_migration_test.cpp
        ${CMAKE_CURRENT_LIST_DIR}/storage/read_issue44_tests.cpp
        ${CMAKE_CURRENT_LIST_DIR}/storage/issue44_tests.cpp
        ${CMAKE_CURRENT_LIST_DIR}/storage/change_passw_storage_tests.cpp
        ${CMAKE_CURRENT_LIST_DIR}/storage/create_storage2_tests.cpp
        ${CMAKE_CURRENT_LIST_DIR}/storage/read_storage2_tests.cpp
)

target_link_libraries(testk2
        key_storage2

        ktest_core
)

set_target_properties(testk2 PROPERTIES COMPILE_FLAGS -DDEBUG_TEST)
gtest_discover_tests(testk2)