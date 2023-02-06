<!-- (c) https://github.com/MontiCore/monticore -->
# DSL - end-user development with MontiCore

This application shows with a basic example how end user development can be accomplished in MontiThings.

# Concept

Projects that use the end-user development feature of MontiThings allow users to upload MontiCore Models, that modify the behavior of specific components.
For this a seperate Server is generated, which can be executed as a docker image and allows to upload models and also handles the generation of python files based on these models.
The genrated python files is then send to the corresponding component which executes it.

# Example project
## **Structure**

## **Execution**
### **Step 1**
Generate the code for this project.
```bash
mvn clean install
```

### **Step 2**
Building all the docker images.
```bash
cd target/generated-sources
./dockerBuild.sh
cd target/generated-sources/generator-server
./dockerBuild.sh
```

### **Step 3**
Run the example.
```bash
mosquitto
cd target/generated-sources
./dockerRun.sh
cd target/generated-sources/generator-server
./dockerRun.sh
```

### **Step 3**
Now listen to the logs of the docker container `calculationMachine.Machine.numLog`.
```bash
docker logs -f calculationMachine.Machine.numLog
```
Access the web page using `127.0.0.1:8080` and a model. Example models can be found in the directory `dsl/exampleModels`.

If everything is working correctly you should see a output similar to the following one:
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
+3                                                                                                                                                                        = 14703 if Y = 147
INFO: 2023-02-06 14:56:29,737 Variable: 148
INFO: 2023-02-06 14:56:29,787 Resulting text:
Y 
*100
+3                                                                                                                                                                        = 14803 if Y = 148 
```

# Getting started building you own DSL-project 