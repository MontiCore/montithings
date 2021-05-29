# MontiThings -> C++ Generator

This subproject contains the code generator that takes MontiThings models and 
generates C++ from it. 
`MontiThingsGeneratorTool` is the main class that coordinates the generation.
Its `generate()` method will be called by the Groovy Script that starts the 
generator and provide it with the necessary parameters of the generation 
(such as the path to the models). 
The generator tool calls the static methods `MTGenerator` (in `codegen`) to 
trigger MontiCore's code generator.

The actual code generation is done by the FreeMarker templates under 
`codegen/template`. 
These freemarker templates contain the "blueprints" for the C++ code to be 
generated. 

In constrast to the rest of the generation, the C++ generation from the 
behavior sublanguage is carried out by `CppPrettyPrinter` (in the `helper`
folder) that uses MontiCore's Java-based pretty printer for the 
MCCommonStatements.
As we generate C++, some of the functions of this pretty printer are adapted for
C++ by the pretty printers in the `visitor` folder.

As mentioned before, the user can set various parameters for the code generator. 
These parameters are specified in the `MontiThingsConfiguration` and 
`ConfigParams` class. A `ConfigParams` object contains the actual parameters and
is given as a parameter to almost all Freemarker templates.