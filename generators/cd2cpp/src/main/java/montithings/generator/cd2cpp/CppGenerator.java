// (c) https://github.com/MontiCore/monticore
package montithings.generator.cd2cpp;

import com.google.common.collect.LinkedListMultimap;
import de.monticore.cd4analysis._parser.CD4AnalysisParser;
import de.monticore.cd4analysis.cocos.CD4AnalysisCoCos;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.*;
import de.monticore.cd4code.prettyprint.CD4CodeFullPrettyPrinter;
import de.monticore.cdassociation._ast.ASTCDAssociation;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;
import de.monticore.cdbasis._symboltable.ICDBasisScope;
import de.monticore.generating.GeneratorEngine;
import de.monticore.generating.GeneratorSetup;
import de.monticore.io.paths.ModelPath;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.mcbasictypes._ast.ASTMCObjectType;
import de.monticore.types.prettyprint.MCBasicTypesFullPrettyPrinter;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static montithings.generator.cd2cpp.TypeHelper.primitiveTypes;

public class CppGenerator {

  private Path outputDir;

  private Path hwcPath;

  private TypeHelper typeHelper;

  public boolean getGenerateProtobufInterface() {
    return generateProtobufInterface;
  }

  public void setGenerateProtobufInterface(boolean generateProtobufInterface) {
    this.generateProtobufInterface = generateProtobufInterface;
  }

  private boolean generateProtobufInterface = false;

  private GeneratorEngine ge;

  protected String _package = "";
  
  protected CD4AnalysisParser p;
  
  protected ICD4CodeGlobalScope globalScope;
  
  protected CD4CodeScopesGenitorDelegator symbolTableCreator;
  
  protected CD4AnalysisCoCos cd4AnalyisCoCos;
  
  protected CD4CodeFullPrettyPrinter printer;
  
  protected CD4CodeDeSer deSer;
  
  protected ASTCDCompilationUnit compilationUnit;
  
  private List<CDTypeSymbol> cdSymbols = new ArrayList<>();
  
  public CppGenerator(
    Path outputDir,
    Path modelPath,
    Path hwcPath,
    String modelName) {
    
    this.outputDir = outputDir;
    
    this.hwcPath = hwcPath;
    
    CD4CodeMill.init();
    
    CD4CodeMill.globalScope().clear();
    globalScope = CD4CodeMill.globalScope();
    globalScope.setModelPath(new ModelPath(modelPath));
    ((CD4CodeGlobalScope) CD4CodeMill.globalScope()).addBuiltInTypes();
    CD4CodeMill.globalScope().add(CD4CodeMill.typeSymbolBuilder()
      .setName("String")
      .setFullName("String")
      .setEnclosingScope(CD4CodeMill.globalScope())
      .setSpannedScope(CD4CodeMill.scope())
      .build());
    TypeSymbol inPortType = CD4CodeMill.typeSymbolBuilder().setName("InPort").setFullName("InPort").setEnclosingScope(CD4CodeMill.globalScope()).setSpannedScope(CD4CodeMill.scope()).build();
    inPortType.addTypeVarSymbol(CD4CodeMill.typeVarSymbolBuilder().setName("T").setFullName("T").build());
    TypeSymbol outPortType = CD4CodeMill.typeSymbolBuilder().setName("OutPort").setFullName("OutPort").setEnclosingScope(CD4CodeMill.globalScope()).setSpannedScope(CD4CodeMill.scope()).build();
    inPortType.addTypeVarSymbol(CD4CodeMill.typeVarSymbolBuilder().setName("T").setFullName("T").build());
    CD4CodeMill.globalScope().add(inPortType);
    CD4CodeMill.globalScope().add(outPortType);
    symbolTableCreator = CD4CodeMill.scopesGenitorDelegator();
    
    final Optional<ASTCDCompilationUnit> astcdCompilationUnit;
    try {
      astcdCompilationUnit = CD4CodeMill.parser()
        .parse(modelPath.toFile().getPath() + "/" + modelName.replace(".", File.separator) + ".cd");
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    compilationUnit = astcdCompilationUnit.get();
    final ICD4CodeArtifactScope scope = symbolTableCreator.createFromAST(compilationUnit);
    compilationUnit.accept(new CD4CodeSymbolTableCompleter(compilationUnit).getTraverser());
    
    cdSymbols.addAll(scope.getCDTypeSymbols().values());
    
    scope
      .getSubScopes()
      .stream()
      .map(ICDBasisScope::getCDTypeSymbols)
      .collect(Collectors.toList())
      .stream()
      .map(LinkedListMultimap::values)
      .forEach(l -> cdSymbols.addAll(l));
  }

  public CppGenerator(
          Path outputDir,
          Path modelPath,
          Path hwcPath,
          String modelName,
          boolean generateProtobufInterface) {
    this(outputDir,modelPath,hwcPath,modelName);
    setGenerateProtobufInterface(generateProtobufInterface);
  }


  public CppGenerator(Path outputDir, ICD4CodeScope scope) {
    this.outputDir = outputDir;
    cdSymbols.addAll(scope.getCDTypeSymbols().values());

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
    GeneratorSetup generatorSetup = new GeneratorSetup();
    generatorSetup.setTracing(false);
    generatorSetup.setOutputDirectory(this.outputDir.toFile());
    generatorSetup.getGlex().defineGlobalVar(
        "generateProtobufInterface", generateProtobufInterface);
    this.ge = new GeneratorEngine(generatorSetup);

    for (CDTypeSymbol symbol : cdSymbols) {
      // CD4A uses different packages. If there's a package _within_ the diagram
      // that is the symbolPackage. If there's no such package, the artifact
      // package is the package containing the diagram itself.
      String symbolPackage = symbol.getEnclosingScope().getName();
      String artifactPackage = symbol.getEnclosingScope().getRealPackageName();
      _package = targetPackage.orElse(symbolPackage.equals("") ? artifactPackage : symbolPackage);
      this.typeHelper = new TypeHelper(_package);
      this.generate(symbol);
    }
    
    if (!cdSymbols.isEmpty()) {
      this.generatePackageHeader(new ArrayList<>(cdSymbols));
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
    if (primitiveTypes.contains(type.getName())) {
      return;
    }

    Collection<ASTCDAssociation> associations;
    if (compilationUnit != null) {
      associations = AssociationHelper.getAssociations(compilationUnit, type);
    } else {
      associations = Collections.emptySet();
    }


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
    //workaround for component type cds
    else if (!type.getAstNode().getInterfaceList().isEmpty()) {
      MCBasicTypesFullPrettyPrinter p = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
      _super.append(" ");
      type.getAstNode().getInterfaceList().forEach(i -> {
        _super.append(" public ");
        _super.append(typeHelper.printType(i.printType(p)));
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
    // hwcPath may be null when we don't generate from a class diagram but from an automatically
    // created type symbol representing the interface of a component type
    File hwcFile = hwcPath != null ? new File(hwcPath.toString(), filePath.toString()) : new File("");
    File targetFile = new File(outputDir.toString(), filePath.toString());
    boolean existsHwc = hwcFile.exists();
    if(existsHwc){
      try {
        FileUtils.copyFile(hwcFile, targetFile);
      } catch (IOException e) {
        e.printStackTrace();
        Log.error(String.format("0xCD2CPP0100 Copying from '%s' to '%s' failed",
          hwcFile, targetFile));
      }
    }
    filePath = Paths.get(Names.getPathFromPackage(filePathString)
      + (existsHwc ? "TOP" : "") + ".h");
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

    //add imports for interfaces of component type cds
    if (type.getInterfaceList().isEmpty()) {
      MCBasicTypesFullPrettyPrinter p = new MCBasicTypesFullPrettyPrinter(new IndentPrinter());
      for (ASTMCObjectType superType : type.getAstNode().getInterfaceList()) {
        String typeName = superType.printType(p);
        imports.add("../" + typeName + "/" + typeName + ".h");
      }
    }

    ge.generate("templates.type.ftl", filePath, type.getAstNode(),
      _package.chars().filter(ch -> ch == '.').count() + 2,
      "montithings\n{\nnamespace " + _package.replace(".", "\n{\nnamespace "), kind,
      type, _super, typeHelper, imports, associations, existsHwc);
  }
}