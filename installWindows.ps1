# Stop on first error
$ErrorActionPreference = "Stop" 

##########################################
# Install WinGet CLI
##########################################
# Install RTE
Invoke-Webrequest -UseBasicParsing -OutFile VCLibs.appx https://aka.ms/Microsoft.VCLibs.x64.14.00.Desktop.appx
Add-AppxPackage -Path "$PWD\VCLibs.appx"
rm "$PWD\VCLibs.appx"

# Find current release
$data = Invoke-Webrequest -UseBasicParsing https://api.github.com/repos/microsoft/winget-cli/releases/latest
$data = $data.Content | ConvertFrom-Json

# Get URL of installer
foreach($asset in $data[0].assets) 
{ 
  if ($asset.name.endswith("msixbundle")) 
  { 
    $wingetUrl=$asset.browser_download_url 
  } 
}
# Download and install
Invoke-Webrequest -UseBasicParsing -OutFile WinGet.msixbundle -Uri $wingetUrl
Add-AppPackage -path ".\WinGet.msixbundle"
rm ".\WinGet.msixbundle"

# Reload Path Environment Variable
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User") 

##########################################
# Install software available via WinGet
##########################################
winget install -e Git.Git
winget install -e Microsoft.OpenJDK.11
winget install -e Microsoft.VisualStudio.2019.Community --override "--passive --wait --config $PWD\.vsconfig"
winget install -e Kitware.CMake
winget install -e Docker.DockerDesktop 
winget install -e EclipseFoundation.Mosquitto
winget install -e JFrog.Conan

# Add Mosquitto to PATH
$oldpath = (Get-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name PATH).path
$newpath="$oldpath;C:\Program Files\Mosquitto\;C:\Program Files\CMake\bin"
Set-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name PATH -Value $newPath

# Reload Path Environment Variable
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User") 

# Start Mosquitto MQTT Broker
Start-Service -Name Mosquitto

##########################################
# Install Maven
##########################################
# Download 
Invoke-Webrequest -UseBasicParsing -OutFile Maven.zip -Uri "https://dlcdn.apache.org/maven/maven-3/3.8.3/binaries/apache-maven-3.8.3-bin.zip"
Expand-Archive -DestinationPath 'C:\Program Files\' Maven.zip
rm .\Maven.zip

# Add Maven to PATH
$oldpath = (Get-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name PATH).path
$newpath="$oldpath;C:\Program Files\apache-maven-3.8.3\bin\"
Set-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name PATH -Value $newPath

# Reload Path Environment Variable
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User") 

##########################################
# Install Ninja
##########################################
# Download
Invoke-Webrequest -UseBasicParsing -OutFile Ninja.zip -Uri https://github.com/ninja-build/ninja/releases/download/v1.10.2/ninja-win.zip
Expand-Archive -DestinationPath 'C:\Program Files\Ninja' Ninja.zip
rm .\Ninja.zip 

# Add Ninja to PATH
$oldpath = (Get-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name PATH).path
$newpath="$oldpath;C:\Program Files\Ninja\"
Set-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name PATH -Value $newPath

# Reload Path Environment Variable
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User") 

##########################################
# Install MinGW
##########################################
# Install Chocolatery
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))

choco install -y mingw


##########################################
# Install NNG 1.3.0
##########################################
Invoke-Webrequest -UseBasicParsing -OutFile nng.zip -Uri https://github.com/nanomsg/nng/archive/v1.3.0.zip
Expand-Archive -DestinationPath "$PWD" nng.zip
rm .\nng.zip 
cd .\nng-1.3.0\
exit 1
cd ..
rm .\nng-1.3.0\


##########################################
# Install MontiThings
##########################################
mvn clean install "-Dmaven.test.skip=true" "-Dexec.skip" 