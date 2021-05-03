project(Recorder CXX)
cmake_minimum_required(VERSION 3.8.2)

find_package(OpenDDS REQUIRED)

set(CMAKE_CXX_COMPILER ${OPENDDS_COMPILER})
set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -gdwarf-3")
set(CMAKE_C_FLAGS_DEBUG "${CMAKE_C_FLAGS_DEBUG} -gdwarf-3")

set(opendds_libs
  OpenDDS::Dcps # Core OpenDDS Library
  OpenDDS::InfoRepoDiscovery
  OpenDDS::Tcp
  OpenDDS::Rtps
  OpenDDS::Udp
  OpenDDS::Rtps_Udp
  OpenDDS::Multicast
  OpenDDS::Shmem
)

set(CMAKE_ARCHIVE_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/bin)

file(GLOB SOURCES
  *.h
  *.cpp
  lib/*.h
  lib/*.cpp
)

# Include RTE source
include_directories("../../generators/montithings2cpp/src/main/resources/rte/montithings-RTE")

# Find prebuild library (run ./build.sh)
find_library(rte_lib MontiThingsRTE "${CMAKE_SOURCE_DIR}/build/rte/lib")

add_executable(recorder
  ${SOURCES}
)

target_link_libraries(recorder ${opendds_libs})
target_link_libraries(recorder ${rte_lib})
