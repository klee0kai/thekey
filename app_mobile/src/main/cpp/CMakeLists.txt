cmake_minimum_required(VERSION 3.18.1)

project("thekey")

add_library(thekey SHARED native-lib.cpp)

find_library(log-lib log)

target_link_libraries(thekey ${log-lib})