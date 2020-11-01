# MontiThings Core Project

© https://github.com/MontiCore/monticore; Contact: @christian.kirchhof

The MontiThings Core repository contains everything related to the common basis of the MontiThings architecture description, 
a [MontiArc][montiarc]-based architecture description language for rapid prototyping of Internet of Things applications.

<img src="docs/MontiThingsOverview.png" alt="drawing" height="400px"/>

In MontiArc, architectures are described as component and connector systems in which autonomously acting components perform 
computations. Communication between components is regulated by connectors between the components’ interfaces, which are stable 
and build up by typed, directed ports. Components are either atomic or composed of connected subcomponents. Atomic components 
yield behavior descriptions in the form of embedded time-synchronous port automata, or via integration of handcrafted code. 
For composed components the behavior emerges from the behavior of their subcomponents. 

While MontiArc generates code for simulations, MontiThings generates code to be executed on real devices.

# Getting Started (native installation)

## Is this the right installation type for you?
The native installation takes more time to set up, but it runs considerably faster than Docker. 
Docker takes about 3-4 times longer to execute. 
If you will use this project for a full semester (e.g. for a thesis or a practical course) 
you'll most likely want the native installation - it will save you time in the long run. 

## Prerequisites 
- Git (for checking out the project)
- Maven (for building the project); Gradle is still under development, use Maven!
- Java 8 or 11 or 14 (other versions are not checked by the CI pipeline)
- [NNG (for networking)][nng] (Please use [version 1.3.0][nng-1.3])
- GCC and CMake (For compiling the generated C++ code)
- [Visual Studio Community][visualstudio] (only necessary for Windows!)
- A settings.xml file from the SE chair (for accessing the dependencies of this project)

## Installation

Place the `settings.xml` file in a folder called `.m2` within your home folder; 
for example this folder might look like this `/home/kirchhof/.m2` (on Linux), `/Users/kirchhof/.m2` (on macOS), 
`C:\Users\Kirchhof\.m2` (on Windows).

```
git clone git@git.rwth-aachen.de:monticore/montithings/core.git
cd core
mvn clean install
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

# Quick Start (using Docker)

## Is this the right installation type for you?
The Docker-based execution is slower to execute, but has almost no requirements. 
Docker execution takes about 3-4 times longer than the native installation. 
If you just want to try this project, but haven't decided if you will use it for 
an extended period of time, you will most likely want this Docker-based installation. 
If you later decide to use this project for a longer period of time, you can still do 
the native installation.


## Prerequisites
- [Docker][docker] (for running the compilers that build this project)
- A `settings.xml` file from the SE chair (for accessing the dependencies of this project)

## Installation

Place the `settings.xml` file in a folder called `.m2` within your home folder; 
for example this folder might look like this `/home/kirchhof/.m2` (on Linux), `/Users/kirchhof/.m2` (on macOS), 
`C:\Users\Kirchhof\.m2` (on Windows).

Log in to this GitLab's docker registry using your credentials you use to log in this GitLab:
```
docker login registry.git.rwth-aachen.de
```

Now you can build the project using this folder:

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
docker run -it --rm -v $PWD:$PWD -w $PWD registry.git.rwth-aachen.de/monticore/montithings/core/mtcmake
```

Windows:
```
docker run -it --rm -v %CD%:/root/generated-sources -w /root/generated-sources registry.git.rwth-aachen.de/monticore/montithings/core/mtcmake
```

This command will bring you into a new shell where you can build the project. 
There you can build the project (for example called `hierarchy`) using 
```
./build.sh hierarchy
```
After building the code, you try to run it by going into the folder with the binaries (`cd build/bin`) and 
then starting the application (`./hierarchy` in this example case). 

Leave the Docker container by pressing `Ctrl+D` or by typing `exit`.

# FAQs

**Q:** "CMake cant find my compiler. Whats wrong?"<br>
**A:** "Most likely your environment variables are wrong. On Windows start the terminal window using
Visual Studio's variable script under `C:\Program Files (x86)\Microsoft Visual Studio\2019\Community\VC\Auxilliary\Build\vcvarsall.bat x64`
(your path might be a little different depending on you installation location and Visual Studio version).

**Q:** "Docker says something like 'denied: access forbidden'"<br>
**A:** You forgot to log in first. Call `docker login registry.git.rwth-aachen.de` and the credentials you
use to log into this GitLab.

**Q:** "I don't know my credentials. I always log in through the RWTH single-sign on"<br>
**A:** "You can find your username by clicking on your icon in the top right corner. The dropdown should tell 
you your username (something like `@christian.kirchhof`). If you haven't set a differnet password for GitLab
your password is most likely the password you use everywhere else to login with you TIM id (TIM id has the 
form `xy123456`). 

**Q:** "I cant execute the binary. It says something like 'cannot execute binary file' (or something similar)"<br>
**A:** You most likely compiled the binary using Docker and are now trying to execute it outside of the container. 
As the different operating systems use different formats for their binaries, this doesn't work. If you have some
time to waste, you can read more about the different file formats on Wikipedia: 
[ELF][elf] (Linux), [Mach-O][mach-o] (macOS), [Portable Executable][portable-executable] (Windows).

# License

© https://github.com/MontiCore/monticore

This repository is currently non-public. 

[se-rwth]: http://www.se-rwth.de
[montiarc]: https://git.rwth-aachen.de/monticore/montiarc/core
[nng]: https://github.com/nanomsg/nng#quick-start 
[nng-1.3]: https://github.com/nanomsg/nng/archive/v1.3.0.zip
[docker]: https://www.docker.com/products/docker-desktop
[visualstudio]: https://visualstudio.microsoft.com/vs/community/
[elf]: https://en.wikipedia.org/wiki/Executable_and_Linkable_Format
[mach-o]: https://en.wikipedia.org/wiki/Mach-O
[portable-executable]: https://en.wikipedia.org/wiki/Portable_Executable