enable_testing()
include(FetchContent)
FetchContent_Declare(
        googletest
        URL https://github.com/google/googletest/archive/609281088cfefc76f9d0ce82e1ff6c30cc3591e5.zip
)
# For Windows: Prevent overriding the parent project's compiler/linker settings
set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
FetchContent_MakeAvailable(googletest)
include_directories(${GTEST_INCLUDE_DIR})


add_executable(tkey_test
        ${SRC_FILES}
        test/salt_testing.cpp
        )


target_include_directories(tkey_test PUBLIC
        ${GTEST_INCLUDE_DIR}
        cpp_lib
        term)

target_link_libraries(tkey_test
        gtest_main
        openssl
        openssl_crypto
        dl
        pthread
        )

set_target_properties(tkey_test PROPERTIES COMPILE_FLAGS -DDEBUG_TEST)


include(GoogleTest)
gtest_discover_tests(tkey_test)