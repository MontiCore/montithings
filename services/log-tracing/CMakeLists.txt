project(LogTracer CXX)
cmake_minimum_required(VERSION 3.8.2)

find_package(OpenDDS REQUIRED)
find_package(Boost REQUIRED system)

set(CMAKE_CXX_COMPILER ${OPENDDS_COMPILER})
set(CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -gdwarf-3")
set(CMAKE_C_FLAGS_DEBUG "${CMAKE_C_FLAGS_DEBUG} -gdwarf-3")
set (CMAKE_CXX_FLAGS_DEBUG "${CMAKE_CXX_FLAGS_DEBUG} -fno-omit-frame-pointer -fsanitize=address")
set (CMAKE_LINKER_FLAGS_DEBUG "${CMAKE_LINKER_FLAGS_DEBUG} -fno-omit-frame-pointer -fsanitize=address")

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

add_executable(logtracer_middleware
  ${SOURCES}
)

include_directories(${Boost_INCLUDE_DIRS})
target_link_libraries(logtracer_middleware ${Boost_LIBRARIES})
target_link_libraries(logtracer_middleware ${opendds_libs})
target_link_libraries(logtracer_middleware ${rte_lib})
