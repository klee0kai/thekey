
add_executable(testk1
        ${CMAKE_CURRENT_LIST_DIR}/salt_tests.cpp
        ${CMAKE_CURRENT_LIST_DIR}/storage/create_storage_tests.cpp
        ${CMAKE_CURRENT_LIST_DIR}/storage/read_storage_tests.cpp
)

target_link_libraries(testk1
        key_storage1_static

        ktest_core
)

set_target_properties(testk1 PROPERTIES COMPILE_FLAGS -DDEBUG_TEST)

gtest_discover_tests(testk1)