/* (c) https://github.com/MontiCore/monticore */
package montithings.generator.cd2cpp;

import de.monticore.ModelingLanguageFamily;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.io.paths.ModelPath;
import de.monticore.symboltable.GlobalScope;
import de.monticore.symboltable.Scope;
import de.monticore.umlcd4a.CD4AnalysisLanguage;
import de.monticore.umlcd4a.symboltable.CDSymbol;
import de.monticore.umlcd4a.symboltable.CDTypeSymbol;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CppGenerator {

  private Path outputDir;

  private TypeHelper typeHelper;

  private GeneratorSetup generatorSetup;

  private GeneratorEngine ge;

  private String _package = "";

  private CDSymbol cdSymbol;

  public CppGenerator(Path outputDir, Path modelPath, String modelName) {
    this(outputDir, modelPath, modelName, Optional.empty());
  }

  public CppGenerator(Path outputDir, Path modelPath, String modelName, String targetPackage) {
    this(outputDir, modelPath, modelName, Optional.of(targetPackage));
  }

  private CppGenerator(
      Path outputDir,
      Path modelPath,
      String modelName,
      Optional<String> targetPackage) {
    this.outputDir = outputDir;

    CD4AnalysisLanguage lang = new CD4AnalysisLanguage();
    ModelingLanguageFamily fam = new ModelingLanguageFamily();
    fam.addModelingLanguage(lang);
    Scope st = new GlobalScope(new ModelPath(modelPath), fam);
    cdSymbol = st.<CDSymbol>resolve(modelName, CDSymbol.KIND).get();
    _package = targetPackage.orElse(cdSymbol.getName().toLowerCase());

    this.typeHelper = new TypeHelper(_package);
    this.generatorSetup = new GeneratorSetup();
    this.generatorSetup.setOutputDirectory(this.outputDir.toFile());
    this.ge = new GeneratorEngine(this.generatorSetup);
  }

  public void generate() {
    cdSymbol.getTypes().forEach(this::generate);
    generatePackageHeader(cdSymbol.getTypes());
    Log.info("Done.", "Generator");
  }

  private void generatePackageHeader(Collection<CDTypeSymbol> types) {
    List<String> imports = new ArrayList<>();
    types.forEach(a -> imports.add(a.getName()));
    Path filePath = Paths.get(Names.getPathFromPackage(_package));
    filePath = Paths
        .get(filePath.toString().replace("::", File.separator) + File.separator + "Package.h");
    ge.generateNoA("templates.package.ftl", filePath, imports);
  }

  private void generate(CDTypeSymbol type) {
    String kind = type.isClass() ? "class" : (type.isEnum() ? "enum" : "class");

    final StringBuilder _super = new StringBuilder();
    if (type.getSuperClass().isPresent()) {
      _super.append("public ");
      _super.append(typeHelper.printType(type.getSuperClass().get()));
      _super.append(" ");
      if (!type.getInterfaces().isEmpty()) {
        _super.append(",");
      }
    }
    else if (type.isInterface() && !type.getInterfaces().isEmpty()) {
      // Allows extending other interfaces
      _super.append("public ");
      _super.append(typeHelper.printType(type.getInterfaces().get(0)));
      _super.append(" ");
    }
    if (!type.getInterfaces().isEmpty() && !type.isInterface()) {
      _super.append(" ");
      type.getInterfaces().forEach(i -> {
        _super.append(" public ");
        _super.append(typeHelper.printType(i));
        _super.append(",");
      });
      _super.deleteCharAt(_super.length() - 1);
    }

    String typeWithoutMT = typeHelper.printType(type);
    if (typeWithoutMT.startsWith("montithings::")) {
      typeWithoutMT = typeWithoutMT.replaceFirst("montithings::", "");
    }
    Path filePath = Paths.get(Names.getPathFromPackage(typeWithoutMT) + ".h");
    filePath = Paths.get(filePath.toString().replace("::", File.separator));

    // Hack to at least correctly generate java.lang.*
    // Will not work with packages that start with upper case letters
    List<String> imports = new ArrayList<>();
    for (String anImport : cdSymbol.getImports()) {
      final String[] split = anImport.split("\\.");
      if (split.length > 0) {
        final char firstOfLastElement = split[split.length - 1].charAt(0);
        if (Character.isLowerCase(firstOfLastElement)) {
          imports.add(anImport + ".*");
        }
        else {
          imports.add(anImport);
        }
      }
    }

    ge.generate("templates.type.ftl", filePath, type.getAstNode().get(),
        "montithings::" + _package.replace(".", "::"), kind,
        type, _super, typeHelper, imports);
  }
}
