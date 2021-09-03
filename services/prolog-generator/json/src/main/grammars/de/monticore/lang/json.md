<!-- (c) https://github.com/MontiCore/monticore -->

<!-- Beta-version: This is intended to become a MontiCore stable explanation. -->

# JSON Language Description

* The MontiCore language JSON contains the grammar 
  and symbol management infrastructure for parsing and processing 
  JSON artifacts
```json
    {
        "Alice": {
            "name": "Alice Anderson",
            "address": {
                "postal_code": 10459,
                "street": "Beck Street",
                "number": 56
            }
        },
        "Bob": {
            "name": "Bob Barkley",
            "address": {
                "postal_code": 10459,
                "street": "Freeman Street",
                "number": 73
            }
        }
    }
```
* The main purpose of this language is parsing general artifacts in JSON format
  that adhere to the common [standard](http://www.ecma-international.org/publications/files/ECMA-ST/ECMA-404.pdf).
* The JSON grammar adheres to the common **standard** and allows parsing 
  arbitrary JSON artifacts for further processing.
* Actually the grammar represents a slight superset to the official JSON standard. 
  It is intended for parsing JSON-compliant artifacts. Further well-formedness
  checks are not included, because we assume to parse correctly produced JSON 
  documents only.
* Please note that JSON (like XML or ASCII) is just a carrier language.
  The conrete JSON dialect and the question, how to recreate the
  real objects / data structures, etc. behind the JSON tree structure
  is beyond this grammar, but can be applied to the AST defined here.
* Main grammar [`de.monticore.lang.JSON.mc4`](src/main/grammars/de/monticore/lang/JSON.mc4).

## Symboltable
* The JSON artifacts provide symbols of type JSONPropertySymbol. 
* Symbol management:
  * JSON artifacts provide a hierarchy of scopes along the objects they define.
  * Each *"attribute name"* (i.e., each property key) acts as a symbol.

### Symbol kinds used by JSON (importable):
* None, because JSON does not have mechanisms to refer to external symbols.

### Symbol kinds defined by JSON:
* `JSONPropertySymbol` contains a JSON attribute name
* JSON attribute names act as symbol names and 
  can be ordinary strings (which differs from 
  standard approach to use `Name`s only and leads to problems if "."
  is included in the symbol and qualified search is used.)

### Symbols exported by JSON:
* JSON documents generally do NOT export any symbol to external artifacts. 
  This has two reasons:
  * Usually JSON dialect encode their information in various 
    specific forms. A default symbol table would therefore 
    not be useful.
  * JSON is mainly a transport technique for data, e.g. during runtime
    of products, services, but also tools and simulators. JSON artefacts 
    are meant for reading and processing, not usually for referring to 
    their internal information by other artefacts.
* Thus there is no symbol-table to be stored.  
  JSON Symbols are available only when the model has been loaded.
* Please note that alternatives are possible and could be implemented
  based on the MontiCore symboltable infrastructure. E.g. 
  * The top-level symbols, like `"Alice"` and `"Bob"` would be available. 
  * All symbols would be available. That would also include e.g. 
    fully qualified `"Bob.adress.street"`. This, however, would leed to a 
    symboltable that is larger that the original model and therefore,
    we suggest to load the original model then instead.
  * A special kind of JSON property, such as `"name"` within each
    *objects* contains the usable object name.
  * A special kind of JSON property, such as `"uuid"` within each
    *objects* contains an anonymously generated reference allowing
    to rebuild graph structures (if needed).

## Functionality: CoCos
* none provided; it is assumed that the JSON model was produced correctly.

## Handwritten Extensions
* [JSONPrettyPrinter](./src/main/java/de/monticore/lang/json/prettyprint/JSONPrettyPrinter.java)
  A pretty-printer for serialzing JSON-ASTs into JSON-compliant artifacts.

## Further Information

* [JSON grammar](src/main/grammars/de/monticore/lang/JSON.mc4)
* [Functions for JSON available](./README.md)
* [CD4Analysis](https://git.rwth-aachen.de/monticore/cd4analysis/cd4analysis)
* [Project root: MontiCore @github](https://github.com/MontiCore/monticore)
* [MontiCore documentation](http://www.monticore.de/)
* [Licence definition](https://github.com/MontiCore/monticore/blob/master/00.org/Licenses/LICENSE-MONTICORE-3-LEVEL.md)

