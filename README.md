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
1. a native installation on your machine [(*install instructions*)](./docs/install/native.md)
2. an installation in a virtual machine of the Microsoft Azure Cloud [(*install instructions*)](./docs/install/azure.md)
3. using MontiThings' Docker containers to avoid an installation [(*install instructions*)](./docs/install/docker.md)
4. using an online IDE by clicking this button (you will need to sign in with your GitHub account to Gitpod): \
   [![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/monticore/montithings)


# Building and Running Your First Application

This sections guides you through building and executing your first application.
We will use the example under `applications/basic-input-output`.
It consists of only three components, with the main purpose of showcasing the
MontiThings build process.
The `Example` component contains two subcomponents. The `Source` component produces
values, the `Sink` component consumes these values and displays them on the
terminal.

<img src="docs/BasicInputOutputPackaged.png" alt="drawing" height="200px"/>

We support four ways of building an application:
1. Using the command line [(*instructions*)](./docs/building-apps/cmd.md)
1. Using the [CLion][clion] IDE [(*instructions*)](./docs/building-apps/clion.md)
1. Using [Docker](docker) [(*instructions*)](./docs/building-apps/docker.md)
1. Using MontiThings' command line tool [(*instructions*)](./cli/README.md)

<img src="docs/ScreenRecording.gif" width="700px"/>

# FAQs

[Here](./docs/faq.md) you can find answers to the most frequently asked questions building and using MontiThings.

# Reference

Please cite MontiThings using it's publication in the Journal of Systems and Software  [(Free Preprint Link)][jss-preprint].
> Jörg Christian Kirchhof, Bernhard Rumpe, David Schmalzing, Andreas Wortmann,
MontiThings: Model-Driven Development and Deployment of Reliable IoT Applications, In: W.K. Chan, editor, Journal of Systems and Software (JSS), Volume 183, January 2022, 111087, Elsevier, https://doi.org/10.1016/j.jss.2021.111087.

```
@article{KRS+22,
  key       = {KRS+22},
  title     = {{MontiThings: Model-driven Development and Deployment of Reliable IoT Applications}},
  author    = {Kirchhof, J\"{o}rg Christian and Rumpe, Bernhard and Schmalzing, David and Wortmann, Andreas},
  editor    = {Chan, Wing-Kwong},
  year      = 2022,
  month     = {January},
  journal   = {{Journal of Systems and Software}},
  publisher = {Elsevier},
  volume    = 183,
  pages     = 111087,
  doi       = {https://doi.org/10.1016/j.jss.2021.111087},
  issn      = {0164-1212},
  url       = {http://www.se-rwth.de/publications/MontiThings-Model-driven-Development-and-Deployment-of-Reliable-IoT-Applications.pdf},
  keywords  = {Internet of Things, Model-driven engineering, Architecture modeling, Code generation, Deployment}
}
```


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
[jss-preprint]: https://www.se-rwth.de/publications/MontiThings-Model-driven-Development-and-Deployment-of-Reliable-IoT-Applications.pdf
[jss-preprint]: https://www.se-rwth.de/publications/MontiThings-Model-driven-Development-and-Deployment-of-Reliable-IoT-Applications.pdf
