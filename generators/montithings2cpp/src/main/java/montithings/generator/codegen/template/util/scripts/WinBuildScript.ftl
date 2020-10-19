@echo off

for /f "usebackq tokens=*" %%i in (`"%ProgramFiles(x86)%\Microsoft Visual Studio\Installer\vswhere.exe" -latest -products * -requires Microsoft.VisualStudio.Component.VC.Tools.x86.x64 -property installationPath`) do (
set InstallDir=%%i
)

if exist "%InstallDir%\VC\Auxiliary\Build\vcvars32.bat" (
call "%InstallDir%\VC\Auxiliary\Build\vcvars32.bat"
mkdir build
cd build
(cmake -G Ninja ..) || exit 1
ninja || exit 1
cd bin
for /r "." %%a in (*.exe) do "%%~fa"
) else (
ECHO Could not find VisualStudio. Is it installed?
ECHO Does "%ProgramFiles(x86)%\Microsoft Visual Studio\Installer\vswhere.exe" exist?
exit 1
)