# SD4ComponentTesting

This project implements a language with a generator for writing tests of Internet of Things applications generated using MontiThings.
The language for a SD4C model is derived from the [sd-language](https://git.rwth-aachen.de/monticore/statechart/sd-language) with added support of [OCL](https://git.rwth-aachen.de/monticore/languages/OCL) expressions.

SLE 2021 Project

#### SD-language

SD language is a tool to model and specify sequence diagrams (SD). We used this language as a starting point to define test case diagrams. Test case diagrams (TD) are used to specify a test case which currently needs to be given as an input to generate the C++ tests with our tool.

#### OCL-Expressions

The Object Constraint Language (OCL) defines a set of various practical expressions. Examples are number expressions, equalities, boolean Expressions and more which can be read about in the OCL-documentation or,because it includes MontiCore's CommonExpressions, in the Handbook.


# An Example Model

Suppose the project for which tests are to be generated has the following structure, as shown in this diagram.

![Diagram of Main Component with Sum as Subcomponent](docs/Diagram.svg)

The connections from the diagram would be written down components as shown in the following two code sections.

```
package example;

component Main {
  port in int inPort;
  port out int outPort;

  Sum sumCom;

  inPort -> sumCom.first; 
  inPort -> sumCom.second;
  sumCom.outPort -> outPort;
}
```

```
package example;

component Sum {
  port in int first;
  port in int second;

  port out int outPort;
}
```

Having the application specified, we come up with an idea of a test case for the application. These test cases normaly describing how the components of an application handle a predefined situation with inputs, while keeping checked that the results are within expectations.

An exemplary test case would be to check if the application returns 24 for an input of 12. Representing this test case in a graphical syntax would result in something like the following test diagram, which is a variation of the sequence diagram from sd-language.

![Diagram of 12+12=24 Test Case](docs/TestCaseSD.svg)

Transforming this into textual syntax for generating tests would result in such a SD4C test diagram that we would save as `MainTest.sd4c`.

```
package example;

testdiagram MainTest for Main {
  -> inPort : 12;
  inPort -> sumCom.first, sumCom.second : 12;
  sumCom.outPort -> outPort : 24;
  outPort -> : 24;
}
```

This test diagram will then be used to generate the C++ files for testing the IoT application.

For more information about the SD4C language please check out the [grammar](src/main/grammars/de/monticore/lang/Readme.md) documentation.

If the test diagram for some reason does not meet the application architecture (e. g. message exchange between components that are not connected in the architecture) the Context Condition checks will fail and throw an error before generating the test files. Check out the [CoCo documentation](src/main/java/de/monticore/lang/sd4componenttesting/_cocos/README.md) for more information about Context Conditions.

# Command Line Interface (CLI)

This section describes the CLI tool of the SD4ComponentTesting Generator.
The CLI tool provides typical functionality used when
processing models. To this effect, it provides funcionality
for

- parsing,
- coco-checking,
- pretty-printing,
- generating.

The requirements for building and using the SD4C CLI tool are that JDK 8 (or JDK 11 or JDK 14), Git, and Maven are installed and available for use in Bash.
This document describes how to build the CLI tool from the source files.
Afterwards, this document contains a tutorial for using the CLI tool.

## Building the CLI Tool from the Sources

It is possible to build an executable JAR of the CLI tool from the source files located in GitLab.
The following describes the process for building the CLI tool from the source files using Bash.
For building an executable Jar of the CLI with Bash from the source files available in GitLab, execute the following commands.

First, clone the repository:

```
git clone https://git.rwth-aachen.de/monticore/montithings/sd4componenttesting.git
```

Change the directory to the root directory of the cloned sources:

```
cd sd4componenttesting
```

Then build the project by running (Info: you need to have Maven installed for this):

```
maven clean install
```

Congratulations! You can now find the executable JAR file `sd4componenttesting-7.0.0-SNAPSHOT-cli.jar` in the directory `target/` (accessible via `cd target/`).

## Tutorial: Getting Started Using the SD4ComponentTesting CLI Tool

The previous sections describe how to obtain an executable JAR file (SD4ComponentTesting CLI tool). This section provides a tutorial for using the SD4ComponentTesting CLI tool. You should create a new folder for this tutorial, you may create this new folder anywhere you want, and copy the CLI tool into the newly created folder. The following examples assume that you locally named the SD4ComponentTesting tool `sd4componenttesting-cli.jar`.
If you build the CLI tool from the sources, you may consider renaming the built JAR.
Please do not use the CLI tool from inside the target/ folder for this Tutorial. The target folder contains models which can lead to some (for the purpose of this tutorial unwanted) sideeffects.

### First Steps

Executing the Jar file without any options prints usage information of the CLI tool to the console:

```
$ java -jar sd4componenttesting-cli.jar 
usage: SD4ComponentTestingCLI
 -c,--coco                  Checks all CoCos for the input.
 -g,--generate <file>       Generate c++ tests for each SD4CT and save them in
                            specified files (optional) or based on the sd4c file
                            name.
 -h,--help                  Prints this help informations.
 -i,--input <arg>           Processes the list of SD4CT input artifacts.
                            Argument list is space separated. CoCos are not
                            checked automatically (see -c).
 -path <arg>                Sets the artifact path for imported symbols, space
                            separated.
 -pp,--prettyprint <file>   Prints the input SD4CTs to stdout or to the
                            specified file (optional).
```

To work properly, the CLI tool needs the mandatory argument `-i,--input <file>`, which takes the file paths of at least one input file containing a SD4C model.
If no other arguments are specified, the CLI tool solely parses the model(s). For trying this out, if you have not done this yet, create a new folder anywhere you want and copy the `sd4componenttesting-cli.jar` into that directory.
Afterwards, create a text file containing the following simple SD4C model:

```
package example;

testdiagram ExampleTest for Example {
}
```

Save the text file as `Example.sd4c` in the directory where `sd4componenttesting-cli.jar` is located.
Now execute the following command:

```
java -jar sd4componenttesting-cli.jar -i Example.sd4c
```

You may notice that the CLI tool prints no output to the console.
This means that the tool has parsed the file `Example.sd4c` successfully.

For more Information about the structure of the SD4C testdiagram from above please have a look at the [Grammar](src/main/grammars/de/monticore/lang/Readme.md#diagram) documentation.

### Step 2: Pretty-Printing

The CLI tool provides a pretty-printer for the SD4C language.
A pretty-printer can be used, e.g., to fix the formatting of files containing a SD4C testdiagram.
To execute the pretty-printer, the `-pp,--prettyprint` option can be used.
Using the option without any arguments pretty-prints the models contained in the input files to the console.

Execute the following command for trying this out:

```
java -jar sd4componenttesting-cli.jar -i Example.sd4c -pp
```

The command prints the pretty-printed model contained in the input file to the console:

```
package example;

testdiagram ExampleTest for Example {
}
```

It is possible to pretty-print the models contained in the input files to output files.
For this task, it is possible to provide the names of output files as arguments to the `-pp,--prettyprint` option.
If arguments for output files are provided, then the number of output files must be equal to the number of input files.
The i-th input file is pretty-printed into the i-th output file.

Execute the following command for trying this out:

```
java -jar sd4componenttesting-cli.jar -i Example.sd4c -pp PPExample.sd4c
```

The command prints the pretty-printed model contained in the input file into the file `PPExample.sd4c`.

### Step 3: Checking Context Conditions
For checking context conditions, the `-c,--coco` option can be used. 
Using this option checks whether the SD4C model satisfies all context conditions. 

Execute the following command for trying out a simple example:
```
java -jar sd4componenttesting-cli.jar -i Example.sd4c -c
```
You may notice that the CLI prints an Error to the console when executing this command.

```
[ERROR] 0xSD4CPT1010: Main Component Instance 'Example' has no model file!
```

This means that the SD4C model does not satisfies all context condtions. It is missing the model file for the Main component.

Create a new folder called `models` in the directory where `sd4componenttesting-cli.jar` is located.
Inside this new folder create an additional new folder and call it `example`.
Now create a text file containing the following simple component called Example:

```
package example;

component Example {
}
```

Save the text file as `Example.arc` in the newly created directory `models/example/`.
When you now execute the command to check the context conditions again you may notice that the CLI prints only `CoCos: All Checked!` to the console while previously executing the command was showing the error.
This means that the CLI now finds the model file for the Main component and satisfies all other context condtions. 

Before explaing in [Step 4](#step-4-using-the-model-path-to-resolve-symbols), how the CLI will find the model file, why the model files are `.arc` files and how you would get `.arc` files for your MontiThings project when you have `.mt` files, let us first consider a more complex example.

Recall the testdiagram `MainTest` from the [An Example Model](#an-example-model) section above.
For continuing, copy the textual representation of the SD4C `MainTest` and save it in a file `MainTest.sd4c` in the directory where the file `sd4componenttesting-cli.jar` is located.
Additionally we need the model files for the Main and Sum component. You can find them in the [An Example Model](#an-example-model) section above aswell. Save them as `Main.arc` and `Sum.arc` in the same directory where `Example.arc` is located, in `models/example`.

Now you can check the context conditions, using the `-c,--coco` option:
```
java -jar sd4componenttesting-cli.jar -i MainTest.sd4c -c
```
After executing this command, you should not experience any errors. If you do encounter errors, there could be dublicate models in any subdirectory defining the same component symbol making it impossible for the CLI to resolving the correct model. In that case go ahead anyway and continue with the next step, which will address this error.

### Step 4: Using the Model Path to Resolve Symbols

In the previous steps, we did not explicitly specify the model path. 
However, by default, if the `-path` option is not specified, the directory from which the CLI is called, this is the current working directory, will be used as the model path.

You can manually specify the model path by using the `-path` option:
```
java -jar sd4componenttesting-cli.jar -i MainTest.sd4c -path ./models/example/ -c
```
After executing this command, you should not experience any errors. The CLI prints only `CoCos: All Checked!` to the console.
This again means that all context condtions are satisfied.

At the moment the SD4ComponentTesting-CLI works only with MontiArc model files, thus requires the components to be stored as `.arc` files. When using the SD4ComponentTesting-CLI in a MontiThings Project the idea is that you can use the integrated pretty-printer to convert MontiThings models to MontiArc models and then use the converted MontiArc models to generate Tests with the CLI. For more Information about that please have a look [here](https://git.rwth-aachen.de/monticore/montithings/core/-/blob/develop/languages/montithings/src/main/java/montithings/_visitor/MontiThingsToMontiArcFullPrettyPrinter.java) and [here](https://git.rwth-aachen.de/monticore/montithings/core/-/blob/develop/languages/montithings/src/test/java/montithings/_visitor/MontiThingsPrettyPrinterDelegatorTest.java#L43).

The pretty-printer can be used like this:

```java
montithings._parser.MontiThingsParser mtParser = new montithings._parser.MontiThingsParser();
final java.util.Optional<montiarc._ast.ASTMACompilationUnit> ast = mtParser.parse("src/test/resources/examples/generatorTest/models/Source.mt");
final montithings._visitor.MontiThingsToMontiArcFullPrettyPrinter printer = new montithings._visitor.MontiThingsToMontiArcFullPrettyPrinter();
String arc = printer.prettyprint(ast.get());
```

_Unknown if this actually works, did not work for us - keywords and numbers missing in output_

### Step 5: Generate C++ Tests

As the final and concluding step of this CLI Tutorial the C++ tests are generated.
For generating the C++ tests, the `-g,--generate <file>` option can be used. 
Using this option generates the C++ tests for each SD4C testdiagram from the `-i,--input` argument and save them in the specified files (optional) or based on the input SD4C file name.

Execute the following command for trying this out:

```
java -jar sd4componenttesting-cli.jar -i MainTest.sd4c -path models/example -g
```

The command generates the `MainTest.cpp` file in the directory where the CLI tool `sd4componenttesting-cli.jar` is located.

It is possible to provide the names of output files as arguments to the `-g,--generate` option.
If arguments for output files are provided, then the number of output files must be equal to the number of input files.
The i-th input file is generated into the i-th output file.

For example the following command will generate `M.cpp` and `E.cpp` from `MainTest.sd4c` and `Example.sd4c` and stores the generated test files where the CLI tool `sd4componenttesting-cli.jar` is located in a new subfolder called `tests`.

```
java -jar sd4componenttesting-cli.jar -i MainTest.sd4c Example.sd4c -path models/example -g tests/M.cpp tests/E.cpp
```

For more Information about how the Generator works and how the generated C++ Test is structured read the [Generator](src/main/java/de/monticore/lang/sd4componenttesting/generator/README.md) documentation.

# Tool

This section describes the Tool of the SD4ComponentTesting Generator.
The Tool provides typical functionality used when
processing models. To this effect, it provides funcionality
for

- parsing,
- coco-checking,
- generating.

The requirements for building and using the SD4C Tool are that JDK 8 (or JDK 11 or JDK 14), Git, and Maven are installed and available for use in Bash.
This document describes how to build the Tool from the source files.
Afterwards, this document contains a tutorial for using the Tool.

## Building the Tool from the Sources

It is possible to build an executable JAR of the Tool from the source files located in GitLab.
The following describes the process for building the Tool from the source files using Bash.
For building a Jar with the Tool with Bash from the source files available in GitLab, execute the following commands.

First, clone the repository:

```
git clone https://git.rwth-aachen.de/monticore/montithings/sd4componenttesting.git
```

Change the directory to the root directory of the cloned sources:

```
cd sd4componenttesting
```

Then build the project by running (Info: you need to have Maven installed for this):

```
maven clean install
```

Congratulations! You can now find the JAR file `sd4componenttesting-7.0.0-SNAPSHOT.jar` in the directory `target/` (accessible via `cd target/`).

## Tutorial: Getting Started Using the SD4ComponentTesting Tool

### Step 1: First Steps
First we need to import the JAR file to the maven project. The best option to do this, since we can't use a Nexus repository to this time, is probably by creating a local Maven repository. This Maven repository is just a normal directory structure with pom files in it. Let’s say the master Project is saved under `${master_project}` and subproject1 is on `${master_project}/${subproject1}`. Then create a Maven repository in: `${master_project}/local-maven-repo`. The pom file from subproject1 should be located at `${master_project}/${subproject1}/pom.xml`. Then specify the repository with file path as a URL parameter:

```
<repositories>
    <repository>
        <id>local-maven-repo</id>
        <url>file:///${project.parent.basedir}/local-maven-repo</url>
    </repository>
</repositories>
```

Now specify the dependency like in any other repository. Now the pom repository is independent. When the JAR is available in Maven central, one just needs to delete it from the local repository in order to pull it from the default repository.

```
    <dependency>
        <groupId>org.apache.felix</groupId>
        <artifactId>org.apache.felix.servicebinder</artifactId>
        <version>0.9.0-SNAPSHOT</version>
    </dependency>
```

In order to add the JAR file into the local repository, we can use `-DlocalRepositoryPath`:

```
mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file  \
    -Dfile=/some/path/on/my/local/filesystem/felix/servicebinder/target/org.apache.felix.servicebinder-0.9.0-SNAPSHOT.jar \
    -DgroupId=org.apache.felix -DartifactId=org.apache.felix.servicebinder \
    -Dversion=0.9.0-SNAPSHOT -Dpackaging=jar \
    -DlocalRepositoryPath=${master_project}/local-maven-repo
```

Once the JAR file is installed, the Maven repository can be committed to a code repository, and the whole set-up is system independent.

In case that we use MontiThings models they can be converted to MontiArc models by using the MontiThingsToMontiArcPrettyPrinter located in the MontiThings repository. The instruction on how the prettyprinter can be used can be read in the CLI section [Step 4](#step-4-using-the-model-path-to-resolve-symbols).

In order to use the tool we just do the following:

```java
de.monticore.lang.sd4componenttesting.SD4ComponentTestingTool tool = new de.monticore.lang.sd4componenttesting.SD4ComponentTestingTool();
```

Now that we are able to use the tool we can make use of our three basic tool functions, which will be explained in the following:

### Step 2: Parse SD4C Model

Tries to parse the model, checks the grammars correctness and reads in the model.

```java
tool.parseModel("path_to_modelFile");
```

### Step 3: Initialize Symbol Table

The symbol table is a data structure that maps a name to the corresponding symbol. A symbol is an abstraction of its defning model entity. It does not repeat the whole information from the AST. Initializing is automatically done in Step 4, when loading the Model, but not when parsing it in Step 2. More Information about the Symbol table and Scopes can be read in Chapter 9 of the Handbook.

### Step 4: load SD4C Model

This function creates the symbol table and performs the coco Checks. It furthermore enables us to reference the MontiArc model.
loadModel takes 2 arguments. The first argument ist the location for the MontiArc models. The second argument is the location of the testdiagram.
Currently it is only possible to reference one Test Diagram.

```java
tool.loadModel("src/test/resources/example/models/", "src/test/resources/example/ExampleTest.sd4c");
```

### Step 5: generate SD4C model

The additional third argument gives the location for the generated C++ Test. use generate like so:

```java
tool.generate("src/test/resources/example/models/", "src/test/resources/example/ExampleTest.sd4c", "target/test-path/ExampleTest.cpp");
```

## Additional context Information 

### Internet of Things

The Internet of Things (IoT) describes a collection of network-enabled devices, excluding personal computers and servers. This relatively new technology has the goal of connecting everyday objects like thermostats, cars, fridges, microwaves, smartwatches, etc. This provides the possibility for easy communication between people, processes, and things. All connected things gather data via sensors and share data with other applications.
In recent years the Internet of Things has become more and more feasible by low-cost and low-power sensor technology as well as advances in cloud computing. The gathered information can be used for running statistics, recognize patterns, make recommendations, and detect possible malfunctions in advance. In the context of IoT, the importance of data becomes clear. Many authors describe data as the “new Resource”. Collecting data about the usage of products from thousands or millions of consumers brings the possibility of improving these products in a new way.
IoT is not only a technology for home use but very useful in Industry as well. It is an essential part of Industry 4.0. IoT enables things like Mass Customization, meaning the production of highly flexible products on a large scale. By combining production technology, sensor technology with internet technology, new possibilities arise to enable innovative products and services. One of the upcoming challenges is to implement and enforce technical standards and norms to make this approach more reliable and connectible.

### Solar Power Example

In the following, we have a somewhat more realistic Example Application. This more in-depth example should give an idea of the wide range of appliances for the tool. Let’s say we have installed a solar panel on the roof and a Battery in the House. This whole system is connected to the power grid. Now we want to decide where the energy from our solar panel goes. On the left, we have modeled a Control Unit for our Solar Power Panel. We have a few subcomponents that we can just consider as a Blackbox. We just know the inputs and outputs of this System. We have the electricity price (which we consider the same for buying and selling), the battery charge level, and the current house consumption level as inputs. Furthermore, the output is the instruction on how to use the energy. One Example for input can be seen in the Test diagram on the right side. If we have 0,31€ as the price we expect the prize Checker to value that as a high price. If we have 2 kWh the charge checker values that at 25% energy level for the battery (for a Battery with 8 kWh capacity). If we have 3kW as our current consumption the consumption checker values this as 50% because our solar panel currently outputs 6kW. With these inputs to the power distribution manager, we expect that 50% should be sold. SD4ComponentTesting is now able to generate a test case for this instance.

![Diagram of the Solar Power Example](docs/solar_testdiagram.png)

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
