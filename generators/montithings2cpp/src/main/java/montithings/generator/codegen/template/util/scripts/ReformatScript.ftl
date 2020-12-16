<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh

# Check if clang-format is installed
which clang-format > /dev/null
if [ "$?" -eq 1 ] 
then
echo Could not find clang-format. Trying Docker instead.
else
# Reformat files
find . -type f \( -iname "*.cpp" -or -iname "*.h" \) ! -path "./header/*" ! -path "./montithings-RTE/*" ! -path "./build/*" ! -path "./test/gtests/lib/*" | xargs -n 1 -P 8 clang-format -i --style=file
exit 0
fi

which docker > /dev/null
if [ "$?" -eq 1 ] 
then
echo Could not find Docker. Aborting.
exit 1
else
echo Entering container.
docker run --rm -v $PWD:$PWD -w $PWD --entrypoint /bin/sh unibeautify/clang-format ./reformatCode.sh
echo Leaving container.
fi
