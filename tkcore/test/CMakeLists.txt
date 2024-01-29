
include(${CMAKE_CURRENT_LIST_DIR}/ktest_core/CMakeLists.txt)

add_subdirectory(${CMAKE_CURRENT_LIST_DIR}/testk1)

add_executable(tkey_test
        salt2_schemas.cpp
        salt2_testing.cpp
        gen2_passw_testing.cpp
        crypt2_tests.cpp
)

target_link_libraries(tkey_test
        key_find_static
        key_storage1_static
        key_storage2_static


        ktest_core
)

set_target_properties(tkey_test PROPERTIES COMPILE_FLAGS -DDEBUG_TEST)
gtest_discover_tests(tkey_test)