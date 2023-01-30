# (c) https://github.com/MontiCore/monticore

# first, make sure powershell is running with admin priviliges

#### START ELEVATE TO ADMIN #####
param(
    [Parameter(Mandatory=$false)]
    [switch]$shouldAssumeToBeElevated,

    [Parameter(Mandatory=$false)]
    [String]$workingDirOverride
)

# If parameter is not set, we are propably in non-admin execution. We set it to the current working directory so that
# the working directory of the elevated execution of this script is the current working directory
if(-not($PSBoundParameters.ContainsKey('workingDirOverride'))) {
    $workingDirOverride = (Get-Location).Path
}

function Test-Admin {
    $currentUser = New-Object Security.Principal.WindowsPrincipal $([Security.Principal.WindowsIdentity]::GetCurrent())
    $currentUser.IsInRole([Security.Principal.WindowsBuiltinRole]::Administrator)
}

if ((Test-Admin) -eq $false)  {
    if ($shouldAssumeToBeElevated) {
        Write-Output "Elevating did not work :("
    } else {
        Start-Process powershell.exe -Verb RunAs -ArgumentList ('-noprofile -noexit -file "{0}" -shouldAssumeToBeElevated -workingDirOverride "{1}"' -f ($myinvocation.MyCommand.Definition, "$workingDirOverride"))
    }
    exit
}

Set-Location "$workingDirOverride"
##### END ELEVATE TO ADMIN #####

# the shell now runs in admin mode


<#
 # reloads the PATH environment Variable
 #>
function Reload-Path {
    $env:Path = [System.Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [System.Environment]::GetEnvironmentVariable("Path","User")
}

#
# Set "$env:SKIP_MVN = 1" to skip the maven build at the end of this script
#

<#
 # checks using "Get-Command" if a specific program is installed on the system
 #
 # @param $ProgramName name of the program that should be checked
 # @return $true if $ProgramName is already installed on the system, $false otherwise
 #>
function Get-IsInstalled {
    param(
        [Parameter(Mandatory)][string]$ProgramName
    )
    Reload-Path

    try{
        $ErrorActionPreference = "Stop"
        $CheckCommand = Get-Command $ProgramName
        $ErrorActionPreference = "Continue"
        return $true
    }
    catch {
        $ErrorActionPreference = "Continue"
        return $false
    }
}

<#
 # adds a path to the PATH environment variable
 # @param $PathToAdd path to be added to PATH
 #>
function AddToPath {
    param(
        [Parameter(Mandatory)][string]$PathToAdd
    )
    # modify PATH
    $oldpath = (Get-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name PATH).path
    $newpath="$oldpath;$PathToAdd"
    Set-ItemProperty -Path 'Registry::HKEY_LOCAL_MACHINE\System\CurrentControlSet\Control\Session Manager\Environment' -Name PATH -Value $newPath

    Reload-Path
}

<#
 # checks whether the current java version is 11
 #>
function Get-JavaVersionIs11 {
    $result = $false
    $result = (java -version 2>&1 | Out-String).Contains('11.0.16.1')
    return $result
}

##########################################
# Install WinGet CLI
##########################################
if(-not (Get-IsInstalled winget)){
    # Install RTE
    Invoke-Webrequest -UseBasicParsing -OutFile VCLibs.appx https://aka.ms/Microsoft.VCLibs.x64.14.00.Desktop.appx
    Add-AppxPackage -Path "$PWD\VCLibs.appx"
    rm "$PWD\VCLibs.appx"

    # Get architecture
    $arch = "x64" # default value
    Switch ($env:PROCESSOR_ARCHITECTURE) {
        "AMD64" {$arch = "x64"}
        "ARM64" {$arch = "arm64"}
        "X86"   {$arch = "x86"}
    }

    # Install Windows UI Library (workaround for this issue: https://github.com/microsoft/winget-cli/issues/1861)
    Invoke-Webrequest -UseBasicParsing -OutFile microsoft.ui.xaml.2.7.zip https://www.nuget.org/api/v2/package/Microsoft.UI.Xaml/2.7.0
    Expand-Archive -DestinationPath .\microsoft.ui.xaml.2.7 .\microsoft.ui.xaml.2.7.zip
    Add-AppxPackage -path ".\microsoft.ui.xaml.2.7\tools\AppX\${arch}\Release\Microsoft.UI.Xaml.2.7.appx"

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

    # Download and install winget
    Invoke-Webrequest -UseBasicParsing -OutFile WinGet.msixbundle -Uri $wingetUrl
    Add-AppxPackage -path ".\WinGet.msixbundle"
    rm ".\WinGet.msixbundle"
    Reload-Path
}

##########################################
# Install software available via WinGet
##########################################
if(-not (Get-IsInstalled git)){
    winget install -e Git.Git
}
if(-not (Get-IsInstalled java) -or -not (Get-JavaVersionIs11)){
    winget install -e Microsoft.OpenJDK.11
    Reload-Path
    if(-not (Get-JavaVersionIs11)){
        Write-Output "WARNING: Java 11 was installed but is not your default Java version. Please make sure to use Java 11 with montithings"
    }
}
if(-not (Get-IsInstalled cmake)){
    winget install -e Kitware.CMake
}
if(-not (Get-IsInstalled docker)){
    winget install -e Docker.DockerDesktop
}
if(-not (Get-IsInstalled mosquitto)){
    winget install -e EclipseFoundation.Mosquitto

    AddToPath("C:\Program Files\Mosquitto")
    AddToPath("C:\Program Files\CMake\bin")
}
Reload-Path
# Start Mosquitto MQTT Broker
Start-Service -Name Mosquitto

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
    choco install maven
    Reload-Path
}

##########################################
# Install Ninja
##########################################
if(-not (Get-IsInstalled ninja)){
    # Download
    Invoke-Webrequest -UseBasicParsing -OutFile Ninja.zip -Uri https://github.com/ninja-build/ninja/releases/download/v1.10.2/ninja-win.zip
    Expand-Archive -DestinationPath 'C:\Program Files\Ninja' Ninja.zip
    rm .\Ninja.zip

    AddToPath("C:\Program Files\Ninja\")
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
if(-not((Test-Path -Path 'C:\nng-1.3.0') -or (Test-Path -Path 'C:\Program Files (x86)\nng'))){
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
}

##########################################
# Install Python
##########################################

# since winget adds an app-execution alias to the ms store for python our
# Get-IsInstalled function would see python as installed. To fix this we must check for pip here instead
if(-not (Get-IsInstalled pip)) {
    winget install python
    Reload-Path
}

##########################################
# Install Conan
##########################################

if(-not (Get-IsInstalled conan)){
    pip install conan
    Reload-Path
}

##########################################
# Install MontiThings
##########################################
if ( $null -eq $env:SKIP_MVN -or $env:SKIP_MVN -ne 1) {
  mvn clean install "-Dmaven.test.skip=true" "-Dexec.skip"
}
"
Installed successfully!

  _____  ___                __  _   ___________    _
 /__   |/  /  ___________  / /_(_) / ___  __/ /_  (_)___  ____   __
   / /|_/ / / __ \__/ __ \/ __/ / (_)  / / / __ \/ / __ \/ __ `//_ \
  / /  / /_/ /_/ / / / / / /_/ /_   __/ / / / / / / / / / /_/ /___) )_
 /_/  /____\____/ /_/ /_/\__/___/  /___/ /_/ /_/_/_/ /_/\__, /(______/
                                                       /____/

"
