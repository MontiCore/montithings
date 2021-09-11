<!-- (c) https://github.com/MontiCore/monticore -->
<img src="docs/Banner.png" width="700px"/>

# MontiThings Core Project

![Ubuntu workflow](https://github.com/monticore/montithings/actions/workflows/maven-ubuntu.yml/badge.svg)
![Windows workflow](https://github.com/monticore/montithings/actions/workflows/maven-windows.yml/badge.svg)
![macOS workflow](https://github.com/monticore/montithings/actions/workflows/maven-mac.yml/badge.svg)

© https://github.com/MontiCore/monticore; Contact: [Christian Kirchhof](https://se-rwth.de/staff/kirchhof)

The MontiThings Core repository contains everything related to the common basis of the MontiThings architecture description, 
a [MontiArc][montiarc]-based architecture description language for rapid prototyping of Internet of Things applications.

<img src="docs/MontiThingsOverview.png" width="700px"/>

In MontiArc, architectures are described as component and connector systems in which autonomously acting components perform 
computations. Communication between components is regulated by connectors between the components’ interfaces, which are stable 
and build up by typed, directed ports. Components are either atomic or composed of connected subcomponents. Atomic components 
yield behavior descriptions in the form of embedded time-synchronous port automata, or via integration of handcrafted code. 
For composed components the behavior emerges from the behavior of their subcomponents. 

While MontiArc generates code for simulations, MontiThings generates code to be executed on real devices.

<img src="docs/Process.png" alt="drawing" width="700px"/>

MontiThings takes models and handwritten code from its users together with a control script.
MontiThings uses these elements to generate a C++ project including various scripts, e.g., for building the project, or packaging it in Docker images.


# Installation

This section describes some of the many possible ways to use MontiThings.
For the purpose of this tutorial, you can choose between the following options:
1. a native installation on your machine
2. an installation in a virtual machine of the Microsoft Azure Cloud
3. using MontiThings' Docker containers to avoid an installation
4. using an online IDE by clicking this button (you will need to sign in with your GitHub account to Gitpod): \
[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/monticore/montithings)


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
- [Mosquitto][mosquitto] (only for MQTT message broker)
- [OpenDDS][opendds] (only for DDS communication)

On Ubuntu 20.04, you can use our script for installing everything except OpenDDS:
```
git clone git@git.rwth-aachen.de:monticore/montithings/core.git
cd core
./installLinux.sh
```

### Installation

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

## Microsoft Azure

### Is this the right installation type for you?
In case you do not want to install MontiThings on your own machine, you can try
MontiThings in a virtual machine provided by Microsoft Azure. 
**Note: Costs may be incurred in the process! Use at your own responsibility!**
This guide is based on the guide from the 
[Microsoft Azure Docs][azure-terraform-docs], but adapted for MontiThings.

### Prerequisites
* [Microsoft Azure CLI][azure-cli] 
* [Terraform][terraform-cli] 
* An SSH key (by default it is expected at `~/.ssh/id_rsa.pub`)

### Installation
First you need to log into Azure and initialize Terraform to make sure 
everything is correctly installed and setup:
```
az login
terraform init
```

Then you can plan your deployment, i.e. dry-run it and get a preview of what 
Terraform will actually do. 
Terraform will also ask you for your GitLab login credentials.
These will be used by the virtual machine to download MontiThings and log into
MontiThings' Docker registry. 
```
terraform plan -out terraform_azure.tfplan
```

If your SSH key is not at `~/.ssh/id_rsa`, please provide its location as an 
argument.
```
terraform plan -out terraform_azure.tfplan -var 'rsa_key_location=/path/to/id_rsa'
```

Here, make sure that you're happy with all the services Terraform will install. 
If you want to know more about the individual services, refer to the excellent 
documentation from the [Microsoft Azure Docs][azure-terraform-docs].

If you're happy, deploy the virtual machine by calling: 
```
terraform apply terraform_azure.tfplan
```

You will see how Terraform first instantiates the virtual machine and then 
installs MontiThings on this machine.
At the end, the script shows you the virtual machine's IP.
In case you forget it, you can find out the virtual machine's IP address by calling:
```
az vm show --resource-group montithingsResourceGroup --name montithings -d --query [publicIps] -o tsv
```

To connect to the machine, call:
```
ssh azureuser@20.30.40.50
```

After the installation you can use MontiThings as if it was installed using 
a native installation.
For example, you can follow the "Building and Running Your First Application" 
tutorial below.

When you are done, you can instruct Terraform to destroy all resources so that 
no further costs are incurred:
```
terraform destroy
```
Terraform will ask again for your GitLab credentials, although they are not 
needed. 
You can just hit enter and leave the prompts empty.
Double check that everything was correctly deleted in your Azure account just to
make sure no further costs are incurred.

## Quick Start (using Docker)

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


# Building and Running Your First Application

This sections guides you through building and executing your first application.
We will use the example under `applications/basic-input-output`.
It consists of only three components, with the main purpose of showcasing the 
MontiThings build process.
The `Example` component contains two subcomponents. The `Source` component produces
values, the `Sink` component consumes these values and displays them on the
terminal.

<img src="docs/BasicInputOutput.png" alt="drawing" height="200px"/>

We support three ways of building an application:
1. Using the command line
1. Using the [CLion][clion] IDE
1. Using [Docker](docker)

The following will guide you through each of these possibilities. 
Choose your favorite method—they're all equivalent for the purpose of 
this introduction.

### Building and Running an Application using the Command Line

*Note: If you're using Windows, please use PowerShell, not the default command line*

After building the project with `mvn clean install` and thus generating 
the C++ code, go to the `target/generated-sources` folder. 
There you can execute the build script:
```bash
./build.sh hierarchy
```

`hierarchy` refers to the folder name in which the generated C++ classes 
can be found.
The folder name is the package name of the outermost component 
(if the generator is set to splitting mode `OFF`) or the 
fully qualified name of the outermost component (if the 
generator is set to splitting mode  that is not `OFF`).

After building the project you can go to the new folder `build/bin`.
There you will find the generated binaries.
Execute them like this:
```bash
./hierarchy.Example -n hierarchy.Example
```

The `-n hierarchy.Example` defines the instance name of the component you'll instantiate by executing the application.
You can stop the application by pressing CTRL+C.

Every generated MontiThings component also comes with a generated command line interface that can tell you more about the available parameters, if you use the `-h` option.
In this case, it looks like this:
```
$ ./hierarchy.Example -h

USAGE: 

   hierarchy.Example  [-h] [--version] -n <string>


Where: 

   -n <string>,  --name <string>
     (required) Fully qualified instance name of the component

   --,  --ignore_rest
     Ignores the rest of the labeled arguments following this flag.

   --version
     Displays version information and exits.

   -h,  --help
     Displays usage information and exits.

   Example MontiThings component
```

Depending on the generator configuration, other options will be available.
For example, if you're using MQTT as message broker, you'll also get options like `--brokerHostname` to set the IP address or hostname of the broker.

In case you've configured the generator with a splitting mode other than `OFF`, you will find multiple binaries in this folder.
In this case, you will also find two scripts `run.sh` and `kill.sh` in the `build/bin` folder which will start and stop all of the generated components so that you dont have to open a huge number of terminal windows.
You can inspect their outputs by looking at the log files, that have the components instance name followed by the file extension `.log` (e.g. `hierarchy.Example.log`).

## Building and Running an Application using CLion

It's also possible to option generated MontiThings Projects in IDEs.
Here, we'll show the process for CLion (which is the Intellij equivalent for C++).
First open the `target/generated-sources` folder as the root folder of a new project.
After CLion is done configuring, your window should look like this:

<img src="docs/Clion1.png" alt="Clion Screenshot" width="700px" />

Now, please open the `Edit configurations...` popup:

<img src="docs/Clion2.png" alt="Clion Screenshot" width="700px" />

In the popup, set the instance name of the component instance that will be instantiated by the application:

<img src="docs/Clion3.png" alt="Clion Screenshot" width="700px" />

Now, you just need to press the green play button and a window will show up that first compiles the code and then shows you the application's output:

<img src="docs/Clion4.png" alt="Clion Screenshot" width="700px" />

## Building and Running an Application using Docker

In order to run an application using Docker, make sure that Docker is installed and running first.

Next, open a terminal in the *generated-sources* folder and use the following command in order to build the application:

```bash
./dockerBuild.sh
```

After, run the application using:

```bash
./dockerRun.sh
```

The application is now running inside of a docker container with the name `hierarchy.example:latest`. In order to verify that the container is instantiated correctly, you can run:

```bash
docker ps
```

In the resulting list, there should be a container with the correct name.

You can also look at the current logs of any instance. For our example, this would be done using the following command:

```bash
docker logs -f hierarchy.example
```

In this case, `hierarchy.example` is the fully qualified instance name. If your application is split (by using the `Splitting = LOCAL` mode in the `pom.xml` of the application), the fully qualified instance names would be `hierarchy.Example.sink`, for example.

After running the command above, the output should look similar to this:

<img src="docs/DockerRunScreenshot.png" alt="drawing" width="700px" />

As you can see, the Source component is sending a new value every second, which is then received by the Sink component.

If you want to stop the application you can do the following:

```bash
./dockerStop.sh
```

# FAQs

**Q:** "CMake cant find my compiler. Whats wrong?"<br>
**A:** "Most likely your environment variables are wrong. On Windows start the terminal window using
Visual Studio's variable script under `C:\Program Files (x86)\Microsoft Visual Studio\2019\Community\VC\Auxilliary\Build\vcvarsall.bat x64`
(your path might be a little different depending on you installation location and Visual Studio version).

**Q:** "Docker says something like 'denied: access forbidden'"<br>
**A:** You need to log in first. Call `docker login registry.git.rwth-aachen.de` and the credentials you
use to log into this GitLab.

**Q:** "I don't know my credentials. I always log in through the RWTH single-sign on"<br>
**A:** "You can find your username by clicking on your icon in the top right corner. The dropdown should tell 
you your username (something like `@christian.kirchhof`). If you haven't set a differnet password for GitLab
your password is most likely the password you use everywhere else to login with you TIM id (TIM id has the 
form `xy123456`). In case you have never logged in using a manually set password, you maybe need to first
[set a password][password].

**Q:** "I cant execute the binary. It says something like 'cannot execute binary file' (or something similar)"<br>
**A:** You most likely compiled the binary using Docker and are now trying to execute it outside of the container. 
As the different operating systems use different formats for their binaries, this doesn't work. If you have some
time to waste, you can read more about the different file formats on Wikipedia: 
[ELF][elf] (Linux), [Mach-O][mach-o] (macOS), [Portable Executable][portable-executable] (Windows).

**Q:** "`mvn clean install` fails with error `The forked VM terminated without properly saying goodbye. VM crash or System.exit called?`"<br>
**A:** Most likely your terminal couldn't handle that much output. Try to either build MontiThings using Intellij or redirect the output to a file: `mvn clean install > output.log 2>&1`

**Q:** "My terminal says 'Killed' when running `mvn clean install`. Why?"
**A:** Probably you don't have enough memory. Check it using `dmesg -T| grep -E -i -B100 'killed process'`. 

# License

© https://github.com/MontiCore/monticore

For details on the MontiCore 3-Level License model, visit
https://github.com/MontiCore/monticore/blob/dev/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md

# Further Information

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [**List of languages**](https://github.com/MontiCore/monticore/blob/dev/docs/Languages.md)
* [**MontiCore Core Grammar Library**](https://github.com/MontiCore/monticore/blob/dev/monticore-grammar/src/main/grammars/de/monticore/Grammars.md)
* [CD4Analysis Project](https://github.com/MontiCore/cd4analysis)
* [Best Practices](https://github.com/MontiCore/monticore/blob/dev/docs/BestPractices.md)
* [Publications about MBSE and MontiCore](https://www.se-rwth.de/publications/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

[se-rwth]: http://www.se-rwth.de
[montiarc]: https://git.rwth-aachen.de/monticore/montiarc/core
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
