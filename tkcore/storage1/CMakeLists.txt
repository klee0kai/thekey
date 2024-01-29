
set(KEY1_SRC
        # storage 1
        ${CMAKE_CURRENT_LIST_DIR}/salt/salt1.cpp
        ${CMAKE_CURRENT_LIST_DIR}/salt/pass_spliter_v1.cpp
        ${CMAKE_CURRENT_LIST_DIR}/key1.cpp
)

add_library(key_storage1_static ${KEY1_SRC})

set_target_properties(key_storage1_static PROPERTIES INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_CURRENT_LIST_DIR})

include_directories(key_storage1_static ${CMAKE_CURRENT_LIST_DIR})

target_link_libraries(key_storage1_static key_core)


#  ------------- SHARED -----------------
add_library(key_storage1 SHARED ${KEY1_SRC})
include_directories(key_storage1 ${CMAKE_CURRENT_LIST_DIR})
target_link_libraries(key_storage1  key_core)