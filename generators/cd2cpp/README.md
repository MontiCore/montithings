<!-- (c) https://github.com/MontiCore/monticore -->
# C++ Generator

This generator derives C++ from a given CD4A model.

## Features implemented:

* For each **cdType** ("class", "enum", "interface") a corresponding java file is generated
* Support for **"extends"** and **"implements"** on types
* For **attributes** in the CD private attributes (with getters and setters) are generated 
* Supported **associations** (without support for consistency in bidirectional associations)
  * (default) -> generates getter and setter
  * \[1\] (mandatory) -> generates getter and setter
  * \[*\] (multiple) -> generates getter, setter, add and remove
  * \[0..1\] (optional) -> generates getter and setter
* Generated **constructor** expecting all attributes and mandatory associations (\[1\]) as parameters
* By default the classes are generated to the **package** derived from the lowercase name of the CD, but it can be configured to a custom package (s. Usage).
 
## Features NOT implemented:

* Assurance of consistency in bidirectional associations
* Generics (CD4A does not support generics)
* Methods (CD4A does not support methods)

## Usage

```
Path outDir = Paths.get("out");
Path modelPath = Paths.get("src/main/models");
String modelName = "domain.Domain";
new CppGenerator(outDir, modelPath, modelName).generate();
// alternative
String targetPackage = "some.custom._package";
new CppGenerator(outDir, modelPath, modelName, targetPackage).generate();
```

Output: C++ in folder "out".
