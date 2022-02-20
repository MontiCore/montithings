## Native installation

### Is this the right installation type for you?
The native installation takes more time to set up, but it runs considerably faster than Docker.
Docker takes about 3-4 times longer to execute.
If you will use this project for a full semester (e.g. for a thesis or a practical course)
you'll most likely want the native installation - it will save you time in the long run.

### Prerequisites
- Git (for checking out the project)
- Maven (for building the project); Alternatively, you can also use Gradle.
- Java 8 or 11 or 14 (other versions are not checked by the CI pipeline)
- [NNG (for networking)][nng] (Please use [version 1.3.0][nng-1.3])
- GCC and CMake (For compiling the generated C++ code)
- [Visual Studio Community][visualstudio] (only necessary for Windows!)
- [Docker][docker] (for executing generator tests)
- [Mosquitto][mosquitto] and [mosquitto_clients][mosquitto] (only for MQTT message broker)
- [OpenDDS][opendds] (only for DDS communication)
- [Python 3][python], [pip][pip] and [paho-mqtt][paho-mqtt] (only for Mqtt message broker)


<details>
<summary>Ubuntu Installation Instructions</summary>

On Ubuntu 20.04, you can use our script for installing everything except OpenDDS:
```
git clone <link to this Git repository> montithings
cd montithings
./installLinux.sh
```

</details>

<details>
<summary>macOS Installation Instructions</summary>

On macOS, you can also use our script for installing everything except OpenDDS.
It will also install Java, Maven, and Gradle using SDKMAN.
If you already have Java 8, 11, or 14 installed, you might want to remove these lines
of the script before executing it.
You will be asked several times for your password in the process.
If you do not already have the XCode developer tools, they will be also
installed; in this case there will be a popup from Apple right after starting the script.
```
git clone <link to this Git repository> montithings
cd montithings
./installMac.sh
```

</details>

<details>
<summary>Windows 10/11 Installation Instructions</summary>

On Windows, you can use our Powershell installer. 
In case you have never run Powershell scripts before, you first need to allow executing
scripts:
```
Set-ExecutionPolicy remotesigned
```
When prompted whether you really want to change the policy, please answer with `a`. 
You can reset the policy after running the installer using:
```
Set-ExecutionPolicy undefined
```

To install MontiThings, run the following:
```
git clone <link to this Git repository> montithings
cd montithings
.\installWindows.ps1
```

During the installation, you will be asked if you accept Microsoft's terms and conditions. 
Accepting their terms is required for the installer.

</details>


<!--
### Installation

```
git clone <link to this Git repository> montithings
cd montithings
mvn clean install -Dexec.skip
```

Now the project should start building. This can take a while (10-15 minutes are normal).

Once the project is built, you can look at the generated source code.
The `application` folder contains some example applications.
Each of them should now contain a `target/generated-sources` subdirectory.
If you want, you can reformat the generated sources for better readability using the
`reformatCode.sh` script (requires clang-format). Within that directory you can find
the generated source. Within one of these folders, you can compile them by running
```
mkdir build; cd build
cmake -G Ninja ..; ninja
```
You should then be able to find the binaries in the `bin` folder. 
-->

[se-rwth]: http://www.se-rwth.de
[montiarc]: https://www.se-rwth.de/topics/Software-Architecture.php
[nng]: https://github.com/nanomsg/nng#quick-start
[nng-1.3]: https://github.com/nanomsg/nng/archive/v1.3.0.zip
[docker]: https://www.docker.com/products/docker-desktop
[visualstudio]: https://visualstudio.microsoft.com/vs/community/
[mosquitto]: https://mosquitto.org/download/
[opendds]: https://opendds.org/downloads.html
[elf]: https://en.wikipedia.org/wiki/Executable_and_Linkable_Format
[mach-o]: https://en.wikipedia.org/wiki/Mach-O
[portable-executable]: https://en.wikipedia.org/wiki/Portable_Executable
[password]: https://git.rwth-aachen.de/profile/password/edit
[clion]: https://www.jetbrains.com/clion
[azure-cli]: https://docs.microsoft.com/en-us/cli/azure/install-azure-cli
[terraform-cli]: https://www.terraform.io/downloads.html
[azure-terraform-docs]: https://docs.microsoft.com/en-us/azure/developer/terraform/create-linux-virtual-machine-with-infrastructure
[python]: https://www.python.org/
[pip]: https://pypi.org/project/pip/
[paho-mqtt]: https://pypi.org/project/paho-mqtt/