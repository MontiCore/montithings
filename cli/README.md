<!-- (c) https://github.com/MontiCore/monticore -->
# MontiThings CLI

The MontiThings CLI can be used to validate, pretty-print, 
and generate code from models.
It can be used without any build scripts.
For projects with a longer lifespan, we recommend considering 
the build process with Maven or Gradle.

```
usage: MTCLI
 -b,--messageBroker <arg>           Set the message broker to be used by the
                                    architecture. Possible arguments are:
                                    -sp off to use a proprietary one,
                                    -sp mqtt to use Message Queuing Telemetry
                                    Transport (Mosquitto MQTT),
                                    -sp dds to Data Distribution Service
                                    (OpenDDS)
 -c,--coco                          Checks the CoCos for the input.
 -d,--dev                           Specifies whether developer level logging
                                    should be used (default is false)
 -h,--help                          Prints this help dialog
 -hwc,--handcodedPath <directory>   Sets the path containing the handwritten
                                    code. Defaults to the current folder +
                                    'src/main/resources/hwc'.
 -i,--input <files>                 Processes the list of MontiThings input
                                    artifacts. Argument list is space separated.
                                    CoCos are not checked automatically (see
                                    -c).
 -main,--mainComp <directory>       Specifies the fully qualified name of the
                                    main, i.e., outermost, component.
 -mp,--modelpath <directory>        Sets the model path for the project.
                                    Directory will be searched recursively for
                                    files with the ending ".*mt". Defaults to
                                    the current folder +
                                    'src/main/resources/models'.
 -pf,--platform <arg>               Set the platform for which to generate code.
                                    Possible arguments are:
                                    -pf generic to generate for generic Linux /
                                    Windows / Mac systems,
                                    -pf dsa to generate for DSA VCG,
                                    -pf raspi to generate for Raspberry Pi.
 -pp,--prettyprint <files>          Prints the OCL model to stdout or the
                                    specified file(s) (optional). Multiple files
                                    should be separated by spaces and will be
                                    used in the same order in which the input
                                    files (-i option) are provided.
 -sp,--splitting <arg>              Set the splitting mode of the generator.
                                    Possible arguments are:
                                    -sp off to generate a single binary
                                    containing all components,
                                    -sp local to generate one binary per
                                    component (for execution on the same
                                    device),
                                    -sp distributed to generate one binary per
                                    component (for execution on multiple
                                    devices)
 -t,--target <directory>            Set the directory in which to place the
                                    generated code. Defaults to the current
                                    folder + 'target/generated-sources'.
 -tp,--testPath <directory>         Sets the path containing the test case code.
                                    Defaults to the current folder +
                                    'src/test/resources/gtests'.
```