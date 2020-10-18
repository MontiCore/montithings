call "C:\Program Files (x86)\Microsoft Visual Studio\2019\Community\VC\Auxiliary\Build\vcvars32.bat"
mkdir build
cd build
cmake -G Ninja ..
ninja || EXIT /B 1
cd bin
for /r "." %%a in (*.exe) do "%%~fa"
