
set(OPENSSL_LIB_PATH ${PROJECT_SOURCE_DIR}/../prebuild/openssl)
set(openssl_include ${OPENSSL_LIB_PATH}/include)

add_library(openssl_interface INTERFACE)
set_target_properties(openssl_interface PROPERTIES
        INTERFACE_INCLUDE_DIRECTORIES ${openssl_include}
        )

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




add_library(openssl_android STATIC IMPORTED)
set_target_properties(openssl_android PROPERTIES
        IMPORTED_LOCATION  ${OPENSSL_LIB_PATH}/android-${ANDROID_SYSROOT_ABI}/lib/libssl.a
        INTERFACE_INCLUDE_DIRECTORIES ${openssl_include}
        )


add_library(openssl_android_crypto STATIC IMPORTED)
set_target_properties(openssl_android_crypto PROPERTIES
        IMPORTED_LOCATION ${OPENSSL_LIB_PATH}/android-${ANDROID_SYSROOT_ABI}/lib/libcrypto.a
        INTERFACE_INCLUDE_DIRECTORIES ${openssl_include}
        )