<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("config","existsHWC")}
@echo off

mkdir build
cd build
cmake .. -G "MinGW Makefiles"
mingw32-make
