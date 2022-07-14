# (c) https://github.com/MontiCore/monticore

# Stop on first error
$ErrorActionPreference = "Stop"

#
# Set "$env:SKIP_MVN = 1" to skip the maven build at the end of this script
#

<#
 # checks using "Get-Command" if a specific program is installed on the system
 # the try-catch Block requires $ErrorActionPreference = "Stop"
 #
 # @param $ProgramName name of the program that should be checked
 # @return $true if $ProgramName is already installed on the system, $false otherwise
 #>
function Get-IsInstalled {
    param(
        [Parameter(Mandatory)][string]$ProgramName
    )

    # Reload Path Environment Variable
    $env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

    try{
        $CheckCommand = Get-Command $ProgramName
        Write-Output "$ProgramName is already installed"
        return $true
    }
    catch {
        return $false
    }
}

##########################################
# Install WinGet CLI
##########################################
if(-not (Get-IsInstalled winget)){
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
}
##########################################
# Install software available via WinGet
##########################################
if(-not (Get-IsInstalled git)){
    winget install -e Git.Git
}
if(-not (Get-IsInstalled java) -or (-not ([string](java --version)).Contains("11"))){
    winget install -e Microsoft.OpenJDK.11
}
if(-not (Get-IsInstalled cmake)){
    winget install -e Kitware.CMake
}
if(-not (Get-IsInstalled docker)){
    winget install -e Docker.DockerDesktop
}
if(-not (Get-IsInstalled mosquitto)){
    winget install -e EclipseFoundation.Mosquitto

    # Add Mosquitto to PATH
    $oldpath = (Get-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name PATH).path
    $newpath="$oldpath;C:\Program Files\Mosquitto\;C:\Program Files\CMake\bin"
    Set-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name PATH -Value $newPath
}
# Reload Path Environment Variable
$env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")

# Start Mosquitto MQTT Broker
Start-Service -Name Mosquitto

if(-not (Get-IsInstalled conan)){
    winget install -e JFrog.Conan

    # Reload Path Environment Variable
    $env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
}
##########################################
# Install Chocolatery Package Manager
##########################################
if(-not (Get-IsInstalled choco)){
    Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
}

##########################################
# Install Maven
##########################################
if(-not (Get-IsInstalled mvn)){
    # Download
    choco install maven

    # Add Maven to PATH
    $oldpath = (Get-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name PATH).path
    $newpath="$oldpath;C:\Program Files\apache-maven-3.8.4\bin\"
    Set-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name PATH -Value $newPath

    # Reload Path Environment Variable
    $env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
}

##########################################
# Install Ninja
##########################################
if(-not (Get-IsInstalled ninja)){
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
}
##########################################
# Install MinGW
##########################################
if(-not (Get-IsInstalled gcc)){
    choco install -y mingw
}

##########################################
# Install NNG 1.3.0
##########################################
Invoke-Webrequest -UseBasicParsing -OutFile nng.zip -Uri https://github.com/nanomsg/nng/archive/v1.3.0.zip
Expand-Archive -DestinationPath "$PWD" nng.zip
rm .\nng.zip
cd .\nng-1.3.0\
mkdir build
cd .\build\
cmake -G Ninja ..
ninja
ninja test
ninja install
cd ..
cd ..

##########################################
# Install MontiThings
##########################################
if ( $null -eq $env:SKIP_MVN -or $env:SKIP_MVN -ne 1) {
  mvn clean install "-Dmaven.test.skip=true" "-Dexec.skip"
}
else {
  "###################################"
  "MontiThings installed successfully!"
  "###################################"
}
