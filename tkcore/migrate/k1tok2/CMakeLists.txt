

set(K1TOK2
        # k1tok2
        ${CMAKE_CURRENT_LIST_DIR}/k1tok2.cpp
)


add_library(k1tok2_static
        ${K1TOK2}
)

target_include_directories(k1tok2_static PUBLIC ${CMAKE_CURRENT_LIST_DIR})
target_link_libraries(k1tok2_static PUBLIC
        key_storage1
        key_storage2
)

set_target_properties(k1tok2_static PROPERTIES INTERFACE_INCLUDE_DIRECTORIES ${CMAKE_CURRENT_LIST_DIR})


#  ------------- SHARED -----------------
add_library(k1tok2 SHARED ${K1TOK2})
target_include_directories(k1tok2 PUBLIC ${CMAKE_CURRENT_LIST_DIR})
target_link_libraries(k1tok2 PUBLIC key_storage1 key_storage2)