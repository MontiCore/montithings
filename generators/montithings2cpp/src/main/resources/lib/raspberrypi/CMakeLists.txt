cmake_minimum_required(VERSION 3.1)
project(RaspberryPiLib)
set(CMAKE_CXX_STANDARD 11)
include_directories(.)
include_directories("../../montithings-RTE")

#set target for building executables and libraries
set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)

file(GLOB_RECURSE LIB_SOURCES "./*.cpp" "./*.h")


add_library(RaspberryPiLib ${LIB_SOURCES})

target_link_libraries(RaspberryPiLib wiringPi)
target_link_libraries(RaspberryPiLib pthread)
set_target_properties(RaspberryPiLib PROPERTIES LINKER_LANGUAGE CXX)
install(TARGETS RaspberryPiLib DESTINATION ${PROJECT_SOURCE_DIR}/lib)