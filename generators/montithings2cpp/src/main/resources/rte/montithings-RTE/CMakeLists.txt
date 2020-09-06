cmake_minimum_required(VERSION 3.8)
project("MontiThingsRTE")
set(CMAKE_CXX_STANDARD 11)

# Find all subdirectories with .h files
# Adapted from https://stackoverflow.com/a/31004567
MACRO(HEADER_DIRECTORIES input return_list)
    FILE(GLOB_RECURSE new_list ${input}/*.h)
    SET(dir_list "")
    FOREACH(file_path ${new_list})
        GET_FILENAME_COMPONENT(dir_path ${file_path} PATH)
        SET(dir_list ${dir_list} ${dir_path})
    ENDFOREACH()
    LIST(REMOVE_DUPLICATES dir_list)
    SET(${return_list} ${dir_list})
ENDMACRO()
SET(dir_list "")


find_package(nng 1.1.1 CONFIG REQUIRED)

# for MSVC
if (MSVC)
    set(variables
            CMAKE_CXX_FLAGS_DEBUG
            CMAKE_CXX_FLAGS_RELEASE
            CMAKE_CXX_FLAGS_RELWITHDEBINFO
            CMAKE_CXX_FLAGS_MINSIZEREL
            )
    foreach (variable ${variables})
        if (${variable} MATCHES "/MD")
            string(REGEX REPLACE "/MD" "/MT" ${variable} "${${variable}}")
        endif ()
    endforeach ()
endif ()

#set target for building executables and libraries
set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)

file(GLOB_RECURSE RTE_SOURCES "./*.cpp" "./*.h")
include_directories(".")

add_library(MontiThingsRTE ${RTE_SOURCES})
target_link_libraries(MontiThingsRTE nng::nng)
set_target_properties(MontiThingsRTE PROPERTIES LINKER_LANGUAGE CXX)
install(TARGETS MontiThingsRTE DESTINATION ${PROJECT_SOURCE_DIR}/lib)















