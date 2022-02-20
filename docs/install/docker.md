## Installation (using Docker)

### Is this the right installation type for you?
The Docker-based execution is slower to execute, but has almost no requirements.
Docker execution takes about 3-4 times longer than the native installation.
If you just want to try this project, but haven't decided if you will use it for
an extended period of time, you will most likely want this Docker-based installation.
If you later decide to use this project for a longer period of time, you can still do
the native installation.


### Prerequisites
- [Docker][docker] (for running the compilers that build this project)

### Installation

You can build the project using this folder:

Linux/macOS:
```
docker run --rm -v $PWD:$PWD -v /Users/kirchhof/.m2:/root/.m2 -w $PWD maven:3-jdk-11 mvn clean install
                                ^--------------^
                       replace this with your own home folder
```

Windows:
```
docker run --rm -v %CD%:/root/montithings -v C:\Users\Kirchhof\.m2:/root/.m2 -w /root/montithings maven:3-jdk-11 mvn clean install
                                             ^----------------^
                                      replace this with your own home folder
```

Now all the application folders should contain folders called `target/generated-sources` that contain the
generated C++ code an some scripts.
Within the `target/generated-sources` folder you can try out the generated code by using this command:

Linux/macOS:
```
docker run -it --rm -v $PWD:$PWD -w $PWD montithings/mtcmake
```

Windows:
```
docker run -it --rm -v %CD%:/root/generated-sources -w /root/generated-sources montithings/mtcmake
```

This command will bring you into a new shell where you can build the project.
There you can build the project (for example called `hierarchy`) using
```
./build.sh hierarchy
```
After building the code, you try to run it by going into the folder with the binaries (`cd build/bin`) and
then starting the application (`./hierarchy` in this example case).

Leave the Docker container by pressing `Ctrl+D` or by typing `exit`.


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