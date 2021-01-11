// (c) https://github.com/MontiCore/monticore
package montithings.generator.cd2cpp;

import com.google.common.collect.LinkedListMultimap;
import de.monticore.cd4analysis.CD4AnalysisMill;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4analysis._symboltable.*;
import de.monticore.cd4analysis.cocos.CD4AnalysisCoCos;
import de.monticore.cd4analysis.prettyprint.CD4AnalysisPrettyPrinter;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeSymbolTableCreatorDelegator;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeGlobalScope;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.io.paths.ModelPath;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class CppGenerator {

  private static final List<String> primitiveTypes = new ArrayList<>(
    Arrays.asList("boolean", "byte", "char", "double", "float",
      "int", "long", "short", "String"));

  private Path outputDir;

  private TypeHelper typeHelper;

  private GeneratorSetup generatorSetup;

  private GeneratorEngine ge;

  private String _package = "";

  protected CD4AnalysisParser p;

  protected ICD4CodeGlobalScope globalScope;

  protected CD4CodeSymbolTableCreatorDelegator symbolTableCreator;

  protected CD4AnalysisCoCos cd4AnalyisCoCos;

  protected CD4AnalysisPrettyPrinter printer;

  protected CD4AnalysisScopeDeSer deSer;

  protected ASTCDCompilationUnit compilationUnit;

  private List<CDTypeSymbol> cdSymbols = new ArrayList<>();

  public CppGenerator(
    Path outputDir,
    Path modelPath,
    String modelName) {
    this.outputDir = outputDir;

    CD4AnalysisMill.init();
    CD4AnalysisParser p = new CD4AnalysisParser();

    globalScope = CD4CodeMill
      .cD4CodeGlobalScopeBuilder()
      .setModelPath(new ModelPath(modelPath))
      .setModelFileExtension(CD4AnalysisGlobalScope.EXTENSION)
      .addBuiltInTypes()
      .build();
    symbolTableCreator = new CD4CodeSymbolTableCreatorDelegator(globalScope);
    //injectPrimitives(globalScope);

    final Optional<ASTCDCompilationUnit> astcdCompilationUnit;
    try {
      astcdCompilationUnit = p
        .parse(modelPath.toFile().getPath() + "/" + modelName.replace(".", File.separator) + ".cd");
    }
    catch (IOException e) {
      e.printStackTrace();
      return;
    }
    compilationUnit = astcdCompilationUnit.get();
    final ICD4CodeArtifactScope scope = symbolTableCreator.createFromAST(compilationUnit);

    scope
      .getSubScopes()
      .stream()
      .map(ICDBasisScope::getCDTypeSymbols)
      .collect(Collectors.toList())
      .stream()
      .map(LinkedListMultimap::values)
      .forEach(l -> cdSymbols.addAll(l));
  }

  public void generate(Optional<String> targetPackage) {
    for (CDTypeSymbol symbol : cdSymbols) {
      // CD4A uses different packages. If there's a package _within_ the diagram
      // that is the symbolPackage. If there's no such package, the artifact
      // package is the package containing the diagram itself.
      String symbolPackage = symbol.getEnclosingScope().getPackageName();
      String artifactPackage = symbol.getEnclosingScope().getRealPackageName();
      _package = targetPackage.orElse(symbolPackage.equals("") ? artifactPackage : symbolPackage);
      this.typeHelper = new TypeHelper(_package);
      this.generatorSetup = new GeneratorSetup();
      this.generatorSetup.setOutputDirectory(this.outputDir.toFile());
      this.ge = new GeneratorEngine(this.generatorSetup);
      this.generate(symbol);
    }

    this.generatePackageHeader(new ArrayList<>(cdSymbols));
  }

  protected void injectPrimitives(ICD4AnalysisGlobalScope scope) {
    for (String primitive : primitiveTypes) {
      CDTypeSymbol primitiveCdType = new CDTypeSymbol(primitive);
      scope.add(primitiveCdType);
    }
  }

  protected void generatePackageHeader(Collection<CDTypeSymbol> types) {
    List<String> imports = new ArrayList<>();
    types.stream().filter(t -> !primitiveTypes.contains(t.getName()))
      .forEach(a -> imports.add(a.getName()));
    Path filePath = Paths.get(Names.getPathFromPackage(_package));
    filePath = Paths
      .get(filePath.toString().replace("::", File.separator) + File.separator + "Package.h");
    ge.generateNoA("templates.package.ftl", filePath, imports);
  }

  private void generate(CDTypeSymbol type) {
    // Skip primitives
    if (primitiveTypes.contains(type.getName()))
      return;

    Collection<ASTCDAssociation> associations = AssociationHelper.getAssociations(compilationUnit, type);

    String kind = type.isIsClass() ? "class" : (type.isIsEnum() ? "enum" : "class");

    final StringBuilder _super = new StringBuilder();
    if (type.isPresentSuperClass()) {
      _super.append("public ");
      _super.append(typeHelper.printType(type.getSuperClass().getTypeInfo()));
      _super.append(" ");
      if (!type.getInterfaceList().isEmpty()) {
        _super.append(",");
      }
    }
    else if (type.isIsInterface() && !type.getInterfaceList().isEmpty()) {
      // Allows extending other interfaces
      _super.append("public ");
      _super.append(typeHelper.printType(type.getInterfaceList().get(0).getTypeInfo()));
      _super.append(" ");
    }
    if (!type.getInterfaceList().isEmpty() && !type.isIsInterface()) {
      _super.append(" ");
      type.getInterfaceList().forEach(i -> {
        _super.append(" public ");
        _super.append(typeHelper.printType(i.getTypeInfo()));
        _super.append(",");
      });
      _super.deleteCharAt(_super.length() - 1);
    }

    String typeWithoutMT = typeHelper.printType(type);
    if (typeWithoutMT.startsWith("montithings::")) {
      typeWithoutMT = typeWithoutMT.replaceFirst("montithings::", "");
    }
    String filePathString = Names.getPathFromPackage(typeWithoutMT);
    filePathString = filePathString.replace("::", File.separator);
    Path filePath = Paths.get(Names.getPathFromPackage(filePathString) + ".h");

    // Hack to at least correctly generate java.lang.*
    // Will not work with packages that start with upper case letters
    List<String> imports = new ArrayList<>();
    /*
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
     */

    ge.generate("templates.type.ftl", filePath, type.getAstNode(),
      _package.chars().filter(ch -> ch == '.').count() + 2,
      "montithings\n{\nnamespace " + _package.replace(".", "\n{\nnamespace "), kind,
      type, _super, typeHelper, imports, associations);
  }
}
