
include(${CMAKE_CURRENT_LIST_DIR}/FindGTestLibs.cmake)

add_executable(tkey_test
        helpers.cpp
        salt_testing.cpp
        salt2_schemas.cpp
        salt2_testing.cpp
        gen2_passw_testing.cpp
        crypt2_tests.cpp
)

target_include_directories(tkey_test PUBLIC
        ${GTEST_INCLUDE_DIR}
)

target_link_libraries(tkey_test
        key_find_static
        key_storage1_static
        key_storage2_static

        gtest_main
)

set_target_properties(tkey_test PROPERTIES COMPILE_FLAGS -DDEBUG_TEST)
gtest_discover_tests(tkey_test)