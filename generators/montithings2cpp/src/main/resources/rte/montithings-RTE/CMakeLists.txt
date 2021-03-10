cmake_minimum_required(VERSION 3.8.2)
project("MontiThingsRTE")
set(CMAKE_CXX_STANDARD 11)

# Find all subdirectories with .h files
# Adapted from https://stackoverflow.com/a/31004567
MACRO(HEADER_DIRECTORIES input return_list)
    FILE(GLOB_RECURSE new_list ${input}/*.h)
    SET(dir_list "")
    FOREACH (file_path ${new_list})
        GET_FILENAME_COMPONENT(dir_path ${file_path} PATH)
        SET(dir_list ${dir_list} ${dir_path})
    ENDFOREACH ()
    LIST(REMOVE_DUPLICATES dir_list)
    SET(${return_list} ${dir_list})
ENDMACRO()
SET(dir_list "")

if (NOT EXCLUDE_DDS)
    find_package(OpenDDS REQUIRED)

    set(CMAKE_CXX_COMPILER "${OPENDDS_COMPILER}")
    set(opendds_libs
        OpenDDS::Dcps # Core OpenDDS Library
        OpenDDS::InfoRepoDiscovery
        OpenDDS::Tcp
        OpenDDS::Rtps #RTPS Discovery
        OpenDDS::Udp
        OpenDDS::Rtps_Udp
    )
endif ()

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

# Exclude DDS files
if (EXCLUDE_DDS)
    list(FILTER RTE_SOURCES EXCLUDE REGEX "DDS.*.h")
    list(FILTER RTE_SOURCES EXCLUDE REGEX "DDS.*.cpp")
    list(FILTER RTE_SOURCES EXCLUDE REGEX "DDSMessage.idl")
endif ()


# exclude MQTT related part of the RTE to not require Mosquitto for compiling
if (EXCLUDE_MQTT)
    list(FILTER RTE_SOURCES EXCLUDE REGEX "Mqtt.*.h")
    list(FILTER RTE_SOURCES EXCLUDE REGEX "Mqtt.*.cpp")
else ()
    target_link_libraries(MontiThingsRTE mosquitto)
endif ()

if (EXCLUDE_COMM_MANAGER)
    # Exclude management communication because splitting is disabled
    list(FILTER RTE_SOURCES EXCLUDE REGEX "ManagementCommunication.h")
    list(FILTER RTE_SOURCES EXCLUDE REGEX "ManagementCommunication.cpp")
endif ()

if (NOT EXCLUDE_DDS)
    # Generates type support code
    OPENDDS_TARGET_SOURCES(MontiThingsRTE "../montithings-RTE/DDSMessage.idl")
    target_link_libraries(MontiThingsRTE ${opendds_libs})
endif ()

target_link_libraries(MontiThingsRTE nng::nng)
set_target_properties(MontiThingsRTE PROPERTIES LINKER_LANGUAGE CXX)
install(TARGETS MontiThingsRTE DESTINATION ${PROJECT_SOURCE_DIR}/lib)
