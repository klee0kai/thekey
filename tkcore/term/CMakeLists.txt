add_executable(tkey
        utils/term_utils.cpp
        utils/interactive.cpp
        k1/termk1.cpp
        k2/termk2.cpp
        k2/termk2_common.cpp
        k2/termk2_select_note.cpp
        k2/termk2_interactive_edit.cpp
        otp/termotp.cpp
        main.cpp
)


target_link_libraries(tkey
        key_find_static
        key_storage1_static
        key_storage2_static
)

install(TARGETS tkey DESTINATION ${PROJECT_SOURCE_DIR}/build/${CMAKE_SYSTEM_NAME}/${CMAKE_SYSTEM_PROCESSOR})