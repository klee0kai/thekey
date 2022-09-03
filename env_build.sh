#!/bin/bash

function read_properties_file() {
  file=$1
  if [ -f "$file" ]; then
    echo "$file found."

    while IFS='=' read -r key value; do
      # игнорируем закомментированные строки
      if [[ $key == "#"* ]]; then
        continue
      fi

      key=$(echo $key | tr '.' '_')
      eval ${key}=\${value}
      echo "property ${key} = ${value}"
    done <"$file"

  else
    echo "$file not found."
  fi
}

function create_local_properties_ifneed() {
  if [[ -f "./local.properties" ]]; then
    return
  fi

  echo "sdk.dir=$ANDROID_SDK" >>"./local.properties"
  echo "android.ndkVersion=$(basename $NDK_ROOT)" >>"./local.properties"
}

function download_openssl() {
  #glone by tag name
  git clone --depth 1 --branch OpenSSL_1_1_1-stable https://github.com/openssl/openssl.git -o origin ${TR_LIBS}/openssl

}

function build_openssl_cur_os() {
  cd ${TR_LIBS}/openssl

  ./config --strict-warnings no-filenames no-afalgeng threads --prefix="${TR_LIBS_BUILD}/openssl/${CUR_OS_UNAME}"
  make clean
  make -j8
  make install -j8

  cd "$WORKSPACE"
}

function build_openssl_android_all() {
  cd ${TR_LIBS}/openssl

  arc_names=("android-arm64" "android-arm" "android-x86" "android-x86_64")
  arch_prebuildes=("aarch64-linux-android-4.9" "arm-linux-androideabi-4.9" "x86-4.9" "x86_64-4.9")

  for i in {0..3}; do
    arch=${arc_names[${i}]}
    arch_prebuild=${arch_prebuildes[${i}]}

    BUILD_PATH="${TR_LIBS_BUILD}/openssl/${arch}"
    PATH="${ANDROID_NDK_ROOT}/toolchains/llvm/prebuilt/linux-x86_64/bin:
          ${ANDROID_NDK_ROOT}/toolchains/${arch_prebuild}/prebuilt/linux-x86_64/bin:$GLOBAL_PATH"
    mkdir -p $BUILD_PATH

    echo "build ${arch} - ${arch_prebuild} to $BUILD_PATH"

    ./Configure $arch no-filenames no-afalgeng no-asm threads -D__ANDROID_API__=${ANDROID_API} --prefix=$BUILD_PATH
    make clean
    make -j8
    make install -j8
  done

  cd "$WORKSPACE"
}

function build_apk() {
  bash ./gradlew assembleRelease
  mkdir -p builds
  cp app/build/outputs/apk/release/app-release.apk builds/app-release.apk

   cd "$WORKSPACE"
}

function build_term_app() {
  mkdir -p builds/${CUR_OS_UNAME}

   cd tkcore
   rm -rf build
   mkdir -p build
   cd build

   cmake ..
   make

   cd "$WORKSPACE"

   cp tkcore/build/tkey_test builds/${CUR_OS_UNAME}/tkey_test
   cp tkcore/build/tkey builds/${CUR_OS_UNAME}/tkey

   #run tests
   "./builds/${CUR_OS_UNAME}/tkey_test"

   cd "$WORKSPACE"
}


read_properties_file ./local.properties

if [ -z "${ANDROID_SDK}" ]; then
  ANDROID_SDK="${ANDROID_HOME}"
fi
if [ -z "${ANDROID_SDK}" ]; then
  ANDROID_SDK="${sdk_dir}"
fi
if [ -z "${ANDROID_SDK}" ]; then
  echo "setup please local.properties sdk.dir=<path to android sdk>"
fi

if [ -z "${ANDROID_NDK_VERSION}" ]; then
  ANDROID_NDK_VERSION=${android_ndkVersion}
fi

if [ -z "${NDK_ROOT}" ]; then
  NDK_ROOT="${ANDROID_SDK}/ndk/${ANDROID_NDK_VERSION}"
fi

if [ -z "${NDK_ROOT}" ]; then
  echo "setup please local.properties ndk.dir=<path to ndk>"
fi
if [ -z "${GLOBAL_PATH}" ]; then
  export GLOBAL_PATH="$PATH"
fi



export TR_LIBS=$(realpath ./third_party_libraries)
export TR_LIBS_BUILD=${TR_LIBS}/build
export ANDROID_SDK_ROOT=${ANDROID_SDK}
export ANDROID_NDK="${NDK_ROOT}"
export ANDROID_NDK_ROOT="${NDK_ROOT}"
export ANDROID_NDK_HOME="${NDK_ROOT}"
export ANDROID_NDK_VERSION=$(realpath $NDK_ROOT)
export ANDROID_API="21"
export PROTOBUF_VERSION="3.9.0"
export WORKSPACE=$(pwd)
export CUR_OS_UNAME=$(uname -sm | sed 's/ /_/g')


mkdir -p ${TR_LIBS}
mkdir -p ${TR_LIBS_BUILD}

# стартуем если сразу указан метод
$1