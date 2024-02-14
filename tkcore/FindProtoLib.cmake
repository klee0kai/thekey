
find_package(Protobuf REQUIRED)

message(Protobuf_INCLUDE_DIRS = ${Protobuf_INCLUDE_DIRS})

add_library(protobuf INTERFACE)
set_target_properties(protobuf PROPERTIES
        INTERFACE_INCLUDE_DIRECTORIES ${Protobuf_INCLUDE_DIRS}
)
