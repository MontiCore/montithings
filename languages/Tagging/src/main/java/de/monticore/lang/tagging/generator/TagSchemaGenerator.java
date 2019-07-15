/**
 *
 *  ******************************************************************************
 *  MontiCAR Modeling Family, www.se-rwth.de
 *  Copyright (c) 2017, Software Engineering Group at RWTH Aachen,
 *  All rights reserved.
 *
 *  This project is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3.0 of the License, or (at your option) any later version.
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * *******************************************************************************
 */
package de.monticore.lang.tagging.generator;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.lang.tagging.helper.RegexStringHelper;
import de.monticore.lang.tagging.helper.UnitKinds;
import de.monticore.lang.tagschema._ast.*;
import de.monticore.lang.tagschema._parser.TagSchemaParser;
import de.monticore.symboltable.ImportStatement;
import de.monticore.types.types._ast.ASTImportStatement;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Splitters;
import de.se_rwth.commons.logging.Log;
import org.jscience.physics.amount.Amount;

/**
 * Created by Michael von Wenckstern on 09.06.2016.
 */
public class TagSchemaGenerator extends GeneratorEngine {

  public TagSchemaGenerator(GeneratorSetup generatorSetup) {
    super(generatorSetup);
  }

  public static Path getPathFromRelativePath(Path relPath) throws Exception {
    return Paths.get(URLClassLoader.newInstance(new URL[] { relPath.toUri().toURL() }).getURLs()[0].toURI());
  }

  public static void generate(String tagSchemaLocation, String modelPath, String outputFolder) {

    try {

      GeneratorSetup setup = new GeneratorSetup();
      setup.setOutputDirectory(getPathFromRelativePath(Paths.get(outputFolder)).toFile());
      setup.setTracing(true);
      TagSchemaGenerator generator = new TagSchemaGenerator(setup);

      Map<String, String> symbolScopeMap = new LinkedHashMap<>();
      symbolScopeMap.put("Component", "NameScope");
      symbolScopeMap.put("ComponentInstance", "NameScope");
      symbolScopeMap.put("Port", "NameScope");
      symbolScopeMap.put("PortInstance", "NameScope");
      symbolScopeMap.put("Connector", "ConnectorScope");
      symbolScopeMap.put("ConnectorInstance", "ConnectorScope");

      List<String> list = Splitters.DOT.splitToList(tagSchemaLocation);
      Path pathTagschema = Paths.get(list.get(0));
      for (int i = 1; i < list.size(); i++) {
        pathTagschema = pathTagschema.resolve(list.get(i));
      }

      generator.generate(pathTagschema, Paths.get(modelPath), symbolScopeMap);

    } catch (Exception e) {
      Log.error("Error during TagSchemaGenerator", e);
    }
  }

      public void generate(Path tagSchemaLocation, Path modelPath, Map<String, String> symbolScopeMap) {
    try {
      _generate(tagSchemaLocation, modelPath, symbolScopeMap);
    }
    catch (Exception e) {
      Log.error("An error occured during the tag schema generation process", e);
    }
  }

  protected void _generate(Path tagSchemaLocation, Path modelPath, Map<String, String> symbolScopeMap)
      throws Exception {
    TagSchemaParser parser = new TagSchemaParser();
    ASTTagSchemaUnit tagSchemaUnit = Log.errorIfNull(parser.parse(
        modelPath.resolve(tagSchemaLocation).toString() +
            (tagSchemaLocation.endsWith(".tagschema") ? "" : ".tagschema")
    ).orElse(null),
        String.format("Could not load tagschema '%s'", tagSchemaLocation.toString()));

    List<String> tagTypeNames = new ArrayList<>();
    String packageName = Joiners.DOT.join(tagSchemaUnit.getPackageList());
    List<ASTTagType> tagTypes = tagSchemaUnit.getTagTypeList();
    for (ASTTagType tagType : tagTypes) {
      generateTagType(tagType, tagSchemaUnit, packageName, symbolScopeMap);
      tagTypeNames.add(tagType.getName());
    }

    generateTagSchema(tagSchemaUnit, packageName, tagTypeNames);
  }

  public void generateTagSchema(ASTTagSchemaUnit tagSchemaUnit, String packageName, List<String> tagTypeNames) {
    generate("templates.de.monticore.lang.tagschema.TagSchema",
        Paths.get(createPackagePath(packageName).toString(), tagSchemaUnit.getName(), tagSchemaUnit.getName() + ".java"),
        tagSchemaUnit, packageName, tagSchemaUnit.getName(), tagTypeNames);
  }

  protected void generateTagType(ASTTagType tagType, ASTTagSchemaUnit tagSchemaUnit, String packageName, Map<String, String> symbolScopeMap) {
    if (tagType instanceof ASTSimpleTagType) {
      generateSimpleTagType((ASTSimpleTagType) tagType, tagSchemaUnit, packageName, symbolScopeMap);
    }
    else if (tagType instanceof ASTValuedTagType) {
      generateValuedTagType((ASTValuedTagType) tagType, tagSchemaUnit, packageName, symbolScopeMap);
    }
    else if (tagType instanceof ASTComplexTagType) {
      generateComplexTagType((ASTComplexTagType) tagType, tagSchemaUnit, packageName, symbolScopeMap);
    }
  }

  protected void generateComplexTagType(ASTComplexTagType complexTagType, ASTTagSchemaUnit tagSchemaUnit, String packageName, Map<String,String> symbolScopeMap) {
    // extract basic information
    String complexTagTypeName = complexTagType.getName();
    List<String> scopeIdentifiers = new LinkedList<>();
    if (complexTagType.isPresentScope()) {
      complexTagType.getScope().getScopeIdentifierList().forEach(s -> scopeIdentifiers.add(s.getScopeName()));
    }

    // extract variables used in the complex type
    Map<String, String> complexVars = new HashMap<>();
    String complexTag = complexTagType.getComplexTag();
    Matcher m = Pattern.compile("\\$\\{(\\w+):(\\w+)\\}").matcher(complexTag);
    String symbolParams = "";
    int count = 1;
    while (m.find()) {
      String name = m.group(1);
      String type = m.group(2);
      if (!UnitKinds.contains(type) && !type.equals("Boolean") && !type.equals("String") && !type.equals("Number")) {
        Log.error(String.format("Unit kind '%s' is not supported. Currently the following types are available: Boolean, String, Number or '%s' ",
                type, UnitKinds.available()), complexTagType.get_SourcePositionStart());
        return;
      }
      if (complexVars.containsKey(name)) {
        Log.error("Variable name: " + name + " in complex tagType: " + complexTagTypeName + " in tagschema: " + tagSchemaUnit.getName() + " is not unique");
        return;
      }
      complexVars.put(name, type);
      if (UnitKinds.contains(type)) {
        symbolParams += "Amount.valueOf(m.group(" + count + ")), ";
      }
      else if (type.equals("Boolean") || type.equals("Number")) {
        symbolParams += "m.group(" + count + "), ";
      }
      else if (type.equals("String")) {
        symbolParams += "\"m.group(" + count + ")\", ";
      }
      count++;
    }
    if (symbolParams.length() > 0) {
      symbolParams = symbolParams.substring(0, symbolParams.length() - 2);
    }


    // build matching string
    String complexMatching = RegexStringHelper.getMatcher(complexTag);

    // generate creator
    List<String> imports = new LinkedList<String>();
    for(ASTImportStatement importStatement : tagSchemaUnit.getImportStatementList()) {
      String importString = Joiners.DOT.join(importStatement.getImportList());
      if(importStatement.isStar()) importString += ".*";
      imports.add(importString);
    }
    String scopeSymbol = complexTagType.getScopeOpt().get().getScopeIdentifierList().get(0).getScopeName();
    String nameScopeType = Log.errorIfNull(symbolScopeMap.get(scopeSymbol), String.format("For the scope symbol '%s' is no scope type defined.", scopeSymbol));
    generate("templates.de.monticore.lang.tagschema.ComplexTagTypeCreator",
            Paths.get(createPackagePath(packageName).toString(), tagSchemaUnit.getName(), complexTagTypeName + "SymbolCreator.java"),
            tagSchemaUnit,
            packageName, tagSchemaUnit.getName(), complexTagTypeName, imports, scopeSymbol + "Symbol", nameScopeType, complexMatching, complexTag, symbolParams);
    // generate symbol
    generate("templates.de.monticore.lang.tagschema.ComplexTagType",
            Paths.get(createPackagePath(packageName).toString(), tagSchemaUnit.getName(), complexTagType.getName() + "Symbol.java"),
            tagSchemaUnit,
            packageName, tagSchemaUnit.getName(), complexTagType.getName(), complexVars, true);

  }

  protected void generateValuedTagType(ASTValuedTagType valuedTagType, ASTTagSchemaUnit tagSchemaUnit, String packageName, Map<String, String> symbolScopeMap) {
    String dataType = null;
    boolean isUnit = false;
    if (valuedTagType.getBooleanOpt().isPresent()) {
      dataType = "Boolean";
    }
    else if (valuedTagType.getNumberOpt().isPresent()) {
      dataType = "Number";
    }
    else if (valuedTagType.getStringOpt().isPresent()) {
      dataType = "String";
    }
    else if (valuedTagType.getUnitKindOpt().isPresent()) {
      isUnit = true;
      dataType = valuedTagType.getUnitKindOpt().get();
      if (!UnitKinds.contains(dataType)) {
        Log.error(String.format("Unit kind '%s' is not supported. Currently the following unit kinds are available '%s'",
            dataType, UnitKinds.available()), valuedTagType.get_SourcePositionStart());
        return;
      }
    }
    Log.errorIfNull(dataType, "Not supported data type in generator");
    generate("templates.de.monticore.lang.tagschema.ValuedTagType",
        Paths.get(createPackagePath(packageName).toString(), tagSchemaUnit.getName(), valuedTagType.getName() + "Symbol.java"),
        tagSchemaUnit, packageName, tagSchemaUnit.getName(), valuedTagType.getName(), dataType, isUnit);

    List<String> imports = new LinkedList<String>();
    for(ASTImportStatement importStatement : tagSchemaUnit.getImportStatementList()) {
      String importString = Joiners.DOT.join(importStatement.getImportList());
      if(importStatement.isStar()) importString += ".*";
      imports.add(importString);
    }
    String scopeSymbol = valuedTagType.getScopeOpt().get().getScopeIdentifierList().get(0).getScopeName();
    String nameScopeType = Log.errorIfNull(symbolScopeMap.get(scopeSymbol), String.format("For the scope symbol '%s' is no scope type defined.", scopeSymbol));
    generate("templates.de.monticore.lang.tagschema.ValuedTagTypeCreator",
        Paths.get(createPackagePath(packageName).toString(),tagSchemaUnit.getName(),  valuedTagType.getName() + "SymbolCreator.java"),
        tagSchemaUnit, packageName, tagSchemaUnit.getName(), valuedTagType.getName(), imports, scopeSymbol + "Symbol", nameScopeType, dataType, isUnit);
  }

  public static Path createPackagePath(String packageName) {
    List<String> parts = Splitters.DOT.splitToList(packageName);
    Path p = Paths.get(parts.get(0));
    for (int i = 1; i < parts.size(); i++) {
      p = Paths.get(p.toString(), parts.get(i));
    }
    return p;
  }

  protected void generateSimpleTagType(ASTSimpleTagType simpleTagType, ASTTagSchemaUnit tagSchemaUnit,
      String packageName, Map<String, String> symbolScopeMap) {
    generate("templates.de.monticore.lang.tagschema.SimpleTagType",
        Paths.get(createPackagePath(packageName).toString(), tagSchemaUnit.getName(), simpleTagType.getName() + "Symbol.java"),
        tagSchemaUnit, packageName, tagSchemaUnit.getName(), simpleTagType.getName());

    List<String> imports = new LinkedList<String>();
    for(ASTImportStatement importStatement : tagSchemaUnit.getImportStatementList()) {
      String importString = Joiners.DOT.join(importStatement.getImportList());
      if(importStatement.isStar()) importString += ".*";
      imports.add(importString);
    }    String scopeSymbol = simpleTagType.getScopeOpt().get().getScopeIdentifierList().get(0).getScopeName();
    String nameScopeType = Log.errorIfNull(symbolScopeMap.get(scopeSymbol), String.format("For the scope symbol '%s' is no scope type defined.", scopeSymbol));
    generate("templates.de.monticore.lang.tagschema.SimpleTagTypeCreator",
        Paths.get(createPackagePath(packageName).toString(), tagSchemaUnit.getName(), simpleTagType.getName() + "SymbolCreator.java"),
        tagSchemaUnit, packageName, tagSchemaUnit.getName(), simpleTagType.getName(), imports, scopeSymbol + "Symbol", nameScopeType);
  }
}
