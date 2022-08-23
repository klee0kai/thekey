 add_library(crypt-storage-lib
         SHARED
         ${SRC_FILES}
         cpp_lib/mapping/jmapping.cpp
         cpp_lib/native-crypt-storage.cpp
         cpp_lib/native-find-storage.cpp
         )

 find_library(log-lib log)
 target_link_libraries(crypt-storage-lib ${log-lib})

 # for https://github.com/android/ndk/issues/517
 set(CMAKE_FIND_ROOT_PATH_MODE_PROGRAM BOTH)
 set(CMAKE_FIND_ROOT_PATH_MODE_LIBRARY BOTH)
 set(CMAKE_FIND_ROOT_PATH_MODE_INCLUDE BOTH)
 set(CMAKE_FIND_ROOT_PATH_MODE_PACKAGE BOTH)


 target_include_directories(crypt-storage-lib PUBLIC
         ${OPENSSL_LIB_PATH}/include
         cpp_lib)

 message(opennssllib ${OPENSSL_LIB_PATH}/android-${ANDROID_SYSROOT_ABI}/lib/libssl.a)

 target_link_libraries(crypt-storage-lib
         ${OPENSSL_LIB_PATH}/android-${ANDROID_SYSROOT_ABI}/lib/libssl.a
         ${OPENSSL_LIB_PATH}/android-${ANDROID_SYSROOT_ABI}/lib/libcrypto.a
         )