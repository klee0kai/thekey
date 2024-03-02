
add_executable(testk1tok2
        ${CMAKE_CURRENT_LIST_DIR}/k1tok2_tests.cpp
)

target_link_libraries(testk1tok2
        k1tok2

        ktest_core
)

set_target_properties(testk1tok2 PROPERTIES COMPILE_FLAGS -DDEBUG_TEST)
gtest_discover_tests(testk1tok2)