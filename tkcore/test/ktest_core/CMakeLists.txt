
enable_testing()
include(FetchContent)
FetchContent_Declare(
        googletest
        URL https://github.com/google/googletest/archive/609281088cfefc76f9d0ce82e1ff6c30cc3591e5.zip
)
# For Windows: Prevent overriding the parent project's compiler/linker settings
set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
FetchContent_MakeAvailable(googletest)

include(GoogleTest)


add_library(ktest_core
        # Core
        ${CMAKE_CURRENT_LIST_DIR}/helpers.cpp
)

set_target_properties(ktest_core PROPERTIES INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_CURRENT_LIST_DIR} ${GTEST_INCLUDE_DIR})

include_directories(ktest_core ${CMAKE_CURRENT_LIST_DIR})

target_link_libraries(ktest_core
        gtest_main
)