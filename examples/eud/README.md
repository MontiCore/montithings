<!-- (c) https://github.com/MontiCore/monticore -->
# EUD - end-user development with MontiCore

This application shows with a basic example how end user development can be accomplished in MontiThings.

# Concept

Projects that use the end-user development feature of MontiThings allow users to upload MontiCore Models, that modify the behavior of specific components.
For this a seperate Server is generated, which can be executed as a docker image and allows to upload models and also handles the generation of python files based on these models.
The genrated python file is then send to the corresponding component which executes it.

# Example project
## **Structure**
This example is very basic calculator, that  consists of following components:
<img src="../../docs/DSLExampleOverview.png" alt="Overview of structure" width="1053px"/>

### **Machine**
This component is just the parent component of everything else.

### **NumberGenerator**
This component outputs a number every second and sends it to the `Calculator`. This number starts at 1 and is raised by one everytime.
Furthermore this component receives the result of the entire calculation from the `Printer` and logs it.

### **Calculator**
This component receives a number from the `NumberGenerator` and performs a calculation on it. The result is send to the `Printer`.
What calculation is performed depends on the entered model.

### **Printer**
This component receives the calculation, the initial value and the result of the calculation. It then creates a string, that represends thsi calculation depending on the inserted model. The string is send to the `NumberGenerator`.

### **Generator-Server**
This not a component in the classical MontiThings way, but it is an important part of a project that requires end-user development. The `Generator-Server` hosts the website at which the language models can be uploaded. Furthermore it generates the python scripts that make up the behavior of the EUD-components based on the uploaded models.


## **Execution**
### **Step 1**
Build all of the languages (Calculator and Printer) in the folder `src/main/resources/languages/calculationMachine` with following command:
```bash
./gradle build
```
### **Step 2**
Generate the code for this project.
```bash
mvn clean install
```

### **Step 3**
Building all the docker images.
```bash
cd target/generated-sources
./dockerBuild.sh
```

### **Step 4**
Run the example.
```bash
mosquitto
cd target/generated-sources
./dockerRun.sh
```

### **Step 5**
Now listen to the logs of the docker container `calculationMachine.Machine.numLog`.
```bash
docker logs -f calculationMachine.Machine.numLog
```
Access the web page using `127.0.0.1:8080` and upload a model. Example models can be found in the directory `eud/exampleModels`.

If everything is working correctly you should see an output similar to the following one:
```bash
INFO: 2023-02-06 14:55:39,711 Variable: 140
INFO: 2023-02-06 14:55:38,757 Resulting text:f(X) = 97 if X = 140
INFO: 2023-02-06 14:55:39,711 Variable: 141
INFO: 2023-02-06 14:55:39,757 Resulting text:f(X) = 98 if X = 141
INFO: 2023-02-06 14:55:40,712 Variable: 142
INFO: 2023-02-06 14:55:41,712 Variable: 143
DEBUG: 2023-02-06 14:55:42,658 Connected to MQTT topic /ports/calculationMachine/Machine/print/text
INFO: 2023-02-06 14:56:11,727 Variable: 144
INFO: 2023-02-06 14:56:10,777 Resulting text:f(X) = 14403 if X = 144
INFO: 2023-02-06 14:56:11,727 Variable: 145
INFO: 2023-02-06 14:56:12,727 Variable: 146
DEBUG: 2023-02-06 14:56:13,218 Connected to MQTT topic /ports/calculationMachine/Machine/print/text
INFO: 2023-02-06 14:56:28,736 Variable: 147
INFO: 2023-02-06 14:56:28,787 Resulting text:
Y
*100
+3
= 14703 if Y = 147
INFO: 2023-02-06 14:56:29,737 Variable: 148
INFO: 2023-02-06 14:56:29,787 Resulting text:
Y 
*100
+3
= 14803 if Y = 148 
```

# Getting started building you own EUD-project

## **Languages**
All MontiCore languages that are supposed to be used for a component need to follow a certain structure.
First of all all languages must be inside of one folder, in this example this is the folder `src/main/resources/languages`, but the folder can also be placed somewhere else. The only important part is, that the location is given in the `pom.xml` or `build.gradle` configuration:
```bash
<languagePath>${basedir}/src/main/resources/languages</languagePath>
```
```bash
languagePath(file("$projectDir/src/main/resources/languages")
```

Inside of this folder you can place all your MontiCore languages. These languages need to follow a naming scheme.
For a component `ComponentType` the language needs to be the folder `<packageName>/<ComponentType>`.
The language it self can be any MontiCore language with a few restrictions:
1. It needs to store all of its templates under the folder `<ComponentType>/src/main/resources/templates`.
2. There must be a java file called `Generator.java`, that defines the generation process. This file needs to be in the folder `<ComponentType>/src/main/java/<langaugename>/generator` and must have following structure:
```java
public class Generator {
    public static String generate(String json) throws Exception {
        ...
    }
    ...
}
```
Where all references to a template need to be made with following path `<packageName>/<ComponentType>/<TemplateName>.ftl>`.
3. Furthermore the language can only accept one model file and must generate exactly one python file.
4. (Optional) If you want to explain your language to the end-user you can put a `EXPLAIN.html` in the folder `<packageName>/<ComponentType>`. Furthermore you can do the samething for the whole project ad how all of your languages work together by adding an `EXPLAIN.html` in your languages folder. These html-files will later be displayed on the website.


## **Defining a EUD-project or EUD-component**
A project is defined as a EUD-project, if there is a `languages` folder, whichs position is defined in the build configuration.
Furthermore a component is defiend as a EUD-component, iff there exists a language in the `languages` folder matching its type.

## **Python script structure**
All EUD-components use a python script that is generated at runtime by the language, that needs to follow a specific format. This format is the same for the python script that needs to be defined under `src/main/resources/hwc/<ComponentType>Impl.py` for each ComponentType as a standart behavior for the component.

```py
# (c) https://github.com/MontiCore/monticore

from <ComponentType>ImplTOP import <ComponentType>ImplTOP

class <ComponentType>Impl(<ComponentType>ImplTOP):

    def __init__(self):
        super().__init__(
            client_id="<clientID>",
            reconnect_on_failure=True
        )

    def getInitialValues(self) -> None:
        #write standart value for all outputs
        self._result.ports["<outPortName>"].<valueName> = ...
        ...
    def compute(self, port) -> None:
        print(f"New value on port {port}: {self._input.ports['result'].var}, {self._input.ports['result'].val}, {self._input.ports['result'].calc}")
        #Read values with:
        self._input.ports['<inPortName>'].<valueName>
        
        ...

        #write values for all outputs, that are needed and send them away
        self._result.ports["<outPortName>"].<valueName> = ...
        self.send_port_<outPortName>()
```

## **Model defintion**
The models under `src/main/resources/models` don't need to be adapted, except for the fact, that all EUD-components need their inputs and outputs as protobuf values.
An indepth explaination on using protobuf for port values can be found in the `face-id-door-opener` example application.
