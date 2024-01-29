
add_executable(testk2
        ${CMAKE_CURRENT_LIST_DIR}/salt2_schemas.cpp
        ${CMAKE_CURRENT_LIST_DIR}/salt2_testing.cpp
        ${CMAKE_CURRENT_LIST_DIR}/gen2_passw_testing.cpp
        ${CMAKE_CURRENT_LIST_DIR}/crypt2_tests.cpp
)

target_link_libraries(testk2
        key_find_static
        key_storage1_static
        key_storage2_static

        ktest_core
)

set_target_properties(testk2 PROPERTIES COMPILE_FLAGS -DDEBUG_TEST)
gtest_discover_tests(testk2)