

package montithings._symboltable;

import de.monticore.java.symboltable.JavaTypeSymbol;
import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.symboltable.*;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.types.JTypeSymbolsHelper;
import de.monticore.types.TypesHelper;
import de.monticore.types.TypesPrinter;
import de.monticore.types.types._ast.ASTType;
import de.se_rwth.commons.Names;
import de.se_rwth.commons.logging.Log;
import montiarc._ast.ASTImportStatementLOCAL;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._ast.ASTStereoValue;
import montiarc._symboltable.MontiArcArtifactScope;
import montiarc._symboltable.MontiArcSymbolTableCreator;
import montiarc.helper.JavaDefaultTypesManager;
import montithings._ast.ASTControlBlock;
import montithings._ast.ASTExecutionBlock;
import montithings._ast.ASTMTCompilationUnit;
import montithings._visitor.MontiThingsVisitor;

import java.util.*;

public class MontiThingsSymbolTableCreator extends MontiArcSymbolTableCreator
        implements MontiThingsVisitor {

  private final static JTypeSymbolsHelper.JTypeReferenceFactory<JavaTypeSymbolReference> javaTypeRefFactory =
          JavaTypeSymbolReference::new;

  public MontiThingsSymbolTableCreator(
          final ResolvingConfiguration resolvingConfig, final MutableScope enclosingScope) {
    super(resolvingConfig, enclosingScope);
  }

  public MontiThingsSymbolTableCreator(final ResolvingConfiguration resolvingConfig, final Deque<MutableScope> scopeStack) {
    super(resolvingConfig, scopeStack);
  }

  /**
   * Creates the symbol table starting from the <code>rootNode</code> and
   * returns the first scope that was created.
   *
   * @param rootNode the root node
   * @return the first scope that was created
   */
  public Scope createFromAST(montithings._ast.ASTMontiThingsNode rootNode) {
    Log.errorIfNull(rootNode, "0xA7004x317 Error by creating of the MontiThingsSymbolTableCreator symbol table: top ast node is null");
    rootNode.accept(realThis);
    return getFirstCreatedScope();
  }

  private MontiThingsVisitor realThis = this;

  public MontiThingsVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void visit(ASTMTCompilationUnit compilationUnit) {
    Log.debug("Building Symboltable for Component: " + compilationUnit.getComponent().getName(),
            MontiThingsSymbolTableCreator.class.getSimpleName());
    this.compilationUnitPackage = Names.getQualifiedName(compilationUnit.getPackageList());
    List<ImportStatement> imports = new ArrayList();
    Iterator var3 = compilationUnit.getImportStatementLOCALList().iterator();

    while(var3.hasNext()) {
      ASTImportStatementLOCAL astImportStatement = (ASTImportStatementLOCAL)var3.next();
      String qualifiedImport = Names.getQualifiedName(astImportStatement.getImportList());
      ImportStatement importStatement = new ImportStatement(qualifiedImport, astImportStatement.isStar());
      imports.add(importStatement);
    }

    JavaDefaultTypesManager.addJavaDefaultImports(imports);
    this.autoinstantiate.push(true);
    ArtifactScope artifactScope = new MontiArcArtifactScope(Optional.empty(), this.compilationUnitPackage, imports);
    this.currentImports = imports;
    artifactScope.setAstNode(compilationUnit);
    compilationUnit.setSpannedScope(artifactScope);
    this.putOnStack(artifactScope);
  }

  @Override
  public void endVisit(ASTMACompilationUnit node) {
    this.removeCurrentScope();
  }


  @Override
  public void setRealThis(MontiThingsVisitor realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
    }
  }

  @Override
  public void visit(montithings._ast.ASTResourcePort node) {
    ASTType astType = node.getType();
    String typeName = TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(astType);
    ResourcePortSymbol sym = new ResourcePortSymbol(node.getName());

    int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(astType);
    JTypeReference<JavaTypeSymbol> typeRef = new JavaTypeSymbolReference(typeName,
            currentScope().get(), dimension);
    addTypeArgumentsToTypeSymbol(typeRef, astType, currentScope().get());
    sym.setTypeReference(typeRef);
    sym.setDirection(node.isIncoming());
    String uri = node.getUri();
    sym.setUri(uri);
    if (uri.startsWith("tcp://")) {
      sym.setProtocol("tcp");
    } else if (uri.startsWith("ipc://")) {
      sym.setProtocol("ipc");
    } else if (uri.startsWith("ws://")) {
      sym.setProtocol("ws");
    } else {
      sym.setProtocol("filesystem");
    }

    if (node.getStereotypeOpt().isPresent()) {
      for (ASTStereoValue st : node.getStereotypeOpt().get().getValuesList()) {
        sym.addStereotype(st.getName(), st.getValue());
      }
    }


    addToScopeAndLinkWithNode(sym, node);
  }

  private void addTypeArgumentsToTypeSymbol(JTypeReference<? extends JTypeSymbol> typeRef,
                                            ASTType astType, Scope definingScope) {
    JTypeSymbolsHelper.addTypeArgumentsToTypeSymbol(typeRef, astType, definingScope,
            javaTypeRefFactory);
  }

  @Override
  public void visit(montithings._ast.ASTResourceInterface node) {
  }

  @Override
  public void visit(montithings._ast.ASTSyncStatement ast) {
    SyncStatementSymbol syncStatement = create_SyncStatement(ast);
    initialize_SyncStatement(syncStatement, ast);
    addToScopeAndLinkWithNode(syncStatement, ast);
  }

  private SyncStatementSymbol create_SyncStatement(montithings._ast.ASTSyncStatement ast) {
    return new SyncStatementSymbol(ast.getName());
  }

  private void initialize_SyncStatement(SyncStatementSymbol syncStatement, montithings._ast.ASTSyncStatement ast) {

  }

  /**
   * The next two methods are necessary so that Guard Expression get an Enclosing Scope
   * in their AST, which is used during generation.
   * @param node
   */
  @Override
  public void visit(ASTExecutionBlock node) {
    node.setEnclosingScope(currentScope().get());
  }

  @Override
  public void endVisit(ASTExecutionBlock node) {
    setEnclosingScopeOfNodes(node);
  }


}



