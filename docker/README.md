<!-- (c) https://github.com/MontiCore/monticore -->

# Base Docker Images

Besides the Docker images that can be created from the generated code,
MontiThings also provides a number of base Docker images. 
These base images can be found in this folder.
The `services` folder includes further Dockerfiles in case a service 
is packaged as a Docker image.

## mtcmake

Contains the tools for building appplications generated with MontiThings.
This image shall be used for applications using MQTT or NNG as communication 
technologies (_not_ DDS).

You can pull the image at:
```
docker pull montithings/mtcmake
```

## mtcmakedds

Contains the tools for building appplications generated with MontiThings that 
use DDS as message broker.
This image is considerably larger than the mtcmake image. 
Use it only if you really are using DDS.
Otherwise use the mtcmake image.

## openddsdcpsinforepo

If DDS is not able to use broadcasts within a network to find other devices, 
it needs the "DCPS Info Repo". 
This can be provided by this image.
You can read more about the DCPS Info Repo in the [OpenDDS developer guide](https://download.objectcomputing.com/OpenDDS/OpenDDS-latest.pdf).
