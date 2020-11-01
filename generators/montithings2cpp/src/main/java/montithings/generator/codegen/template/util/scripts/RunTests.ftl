<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
${tc.signature("config")}
set -e
for dir in ./*/        
do
  if [ -d "${"$"}{dir}generated-test-sources/" ]; then
    cd ${"$"}{dir}generated-test-sources/
    mkdir -p build
    cd build
    cmake -G Ninja ..
    ninja
    cd bin
    for testCase in ./*
    do
      ${"$"}{testCase}
    done
    cd ../../../..
  fi
done
