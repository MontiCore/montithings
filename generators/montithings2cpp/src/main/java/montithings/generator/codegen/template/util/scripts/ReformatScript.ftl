<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh

# Check if clang-format is installed
which clang-format > /dev/null
if [ "$?" -eq 1 ]
then
echo Could not find clang-format. Aborting.
exit 1
fi

# Reformat files
find . -type f \( -iname "*.cpp" -or -iname "*.h" \) ! -path "./header/*" ! -path "./montithings-RTE/*" ! -path "./build/*" | xargs clang-format -i --style=file


