
get_filename_component(OPENSSL_LIB_PATH ${CMAKE_CURRENT_LIST_DIR}/../prebuild/openssl ABSOLUTE)
get_filename_component(openssl_include ${OPENSSL_LIB_PATH}/include ABSOLUTE)

message("OPENSSL_LIB_PATH = ${OPENSSL_LIB_PATH}")

add_library(openssl_interface INTERFACE)
set_target_properties(openssl_interface PROPERTIES
        INTERFACE_INCLUDE_DIRECTORIES ${openssl_include}
        )

if (${ANDROID})
    add_library(openssl STATIC IMPORTED)
    set_target_properties(openssl PROPERTIES
            IMPORTED_LOCATION ${OPENSSL_LIB_PATH}/android-${ANDROID_SYSROOT_ABI}/lib/libssl.a
            INTERFACE_INCLUDE_DIRECTORIES ${openssl_include}
            )

    add_library(openssl_crypto STATIC IMPORTED)
    set_target_properties(openssl_crypto PROPERTIES
            IMPORTED_LOCATION ${OPENSSL_LIB_PATH}/android-${ANDROID_SYSROOT_ABI}/lib/libcrypto.a
            INTERFACE_INCLUDE_DIRECTORIES ${openssl_include}
            )
else ()
    add_library(openssl STATIC IMPORTED)
    set_target_properties(openssl PROPERTIES
            IMPORTED_LOCATION ${OPENSSL_LIB_PATH}/${SYSTEM_INFO_TYPE}/lib/libssl.a
            INTERFACE_INCLUDE_DIRECTORIES ${openssl_include}
            )

    add_library(openssl_crypto STATIC IMPORTED)
    set_target_properties(openssl_crypto PROPERTIES
            IMPORTED_LOCATION ${OPENSSL_LIB_PATH}/${SYSTEM_INFO_TYPE}/lib/libcrypto.a
            INTERFACE_INCLUDE_DIRECTORIES ${openssl_include}
            )

endif ()