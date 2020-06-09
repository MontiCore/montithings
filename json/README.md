<!-- (c) https://github.com/MontiCore/monticore -->
# JSON


The MontiCore language JSON defines the parsing and processing infrastructure 
for JSON artifacts.
The language component (and full language) is part of the MontiCore language 
library.

Please note that JSON (like XML or ASCII) is just a carrier language.
The concrete JSON dialect and the question, how to recreate the
real objects / data structures, etc. behind the JSON tree structure
is beyond this grammar but can be applied to the AST defined here.

* Main grammar [`de.monticore.lang.JSON.mc4`](src/main/grammars/de/monticore/lang/JSON.mc4).


## Functionality

### Parsing JSON artifacts and symbol table creation.

* available ([see language explanation](json.md))

### Structure Extraction

* (Under construction) 
Automatically extracts the structure of a set of JSON artifacts and stores it 
as a class diagram adhering to [`CD4Analysis`][CD4Analysis].
  

## Further Links

* [JSON grammar](src/main/grammars/de/monticore/lang/JSON.mc4)

* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

