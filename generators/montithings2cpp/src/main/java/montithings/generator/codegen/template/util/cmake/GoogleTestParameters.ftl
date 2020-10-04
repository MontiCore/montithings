# (c) https://github.com/MontiCore/monticore
project(gtests)

enable_testing()

add_subdirectory(lib)
include_directories(${gtest_SOURCE_DIR}/include ${gtest_SOURCE_DIR})

macro(package_add_test TESTNAME)
add_executable(${TESTNAME} ${ARGN})
target_link_libraries(${TESTNAME} gtest gmock gtest_main)
target_link_libraries(${TESTNAME} ${comp.getFullName().replaceAll("\\.","_")}Lib)
add_test(NAME ${TESTNAME} COMMAND ${TESTNAME})
set_target_properties(${TESTNAME} PROPERTIES FOLDER tests)
endmacro()

package_add_test(${comp.getFullName().replaceAll("\\.","_")}TestSuite ${comp.getFullName().replaceAll("\\.","_")}Test.cpp)
include_directories("/usr/local/include")
