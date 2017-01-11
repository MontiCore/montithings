/* generated from model null*/
/* generated by template symboltable.SymbolTableCreator*/

package de.monticore.lang.montiarc.ajava._symboltable;

import java.util.Deque;

import de.monticore.automaton.ioautomatonjava._symboltable.IOAutomatonJavaSymbolTableCreator;
import de.monticore.java.symboltable.JavaSymbolTableCreator;
import de.monticore.lang.montiarc.ajava._ast.ASTAJavaDefinition;
import de.monticore.lang.montiarc.ajava._visitor.AJavaDelegatorVisitor;
import de.monticore.lang.montiarc.ajava._visitor.AJavaVisitor;
import de.monticore.lang.montiarc.ajava._visitor.CommonAJavaDelegatorVisitor;
import de.monticore.lang.montiarc.montiarc._symboltable.MontiArcSymbolTableCreator;
import de.monticore.lang.montiarc.montiarcautomaton._symboltable.MontiArcAutomatonSymbolTableCreator;
import de.monticore.lang.montiarc.montiarcautomaton._symboltable.MontiArcBehaviorSymbolTableCreator;
import de.monticore.symboltable.CommonSymbolTableCreator;
import de.monticore.symboltable.MutableScope;
import de.monticore.symboltable.ResolvingConfiguration;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.logging.Log;

public class AJavaSymbolTableCreator extends CommonSymbolTableCreator
    implements AJavaVisitor {
  
  private final AJavaDelegatorVisitor visitor = new CommonAJavaDelegatorVisitor();
  
  public AJavaSymbolTableCreator(
      final ResolvingConfiguration resolverConfig,
      final MutableScope enclosingScope) {
    super(resolverConfig, enclosingScope);
    initSuperSTC(resolverConfig);
  }
  
  public AJavaSymbolTableCreator(
      final ResolvingConfiguration resolverConfig,
      final Deque<MutableScope> scopeStack) {
    super(resolverConfig, scopeStack);
    initSuperSTC(resolverConfig);
  }
  
  MontiArcAutomatonSymbolTableCreator maaSTC;
  
  MontiArcSymbolTableCreator maSTC;
  
  IOAutomatonJavaSymbolTableCreator automatonSTC;
  
  MontiArcBehaviorSymbolTableCreator behaviorSTC;
  
  JavaSymbolTableCreator javaSTC;
  
  private void initSuperSTC(ResolvingConfiguration resolverConfig) {
    maaSTC = new MontiArcAutomatonSymbolTableCreator(resolverConfig, scopeStack);
    maSTC = new MontiArcSymbolTableCreator(resolverConfig, scopeStack);
    automatonSTC = new IOAutomatonJavaSymbolTableCreator(resolverConfig, scopeStack);
    behaviorSTC = new MontiArcBehaviorSymbolTableCreator(resolverConfig, scopeStack);
    javaSTC = new ExtendedJavaSymbolTableCreator(resolverConfig, scopeStack);
    
    visitor.set_de_monticore_lang_montiarc_ajava__visitor_AJavaVisitor(this);
    visitor.set_de_monticore_java_javadsl__visitor_JavaDSLVisitor(javaSTC);
    visitor
        .set_de_monticore_lang_montiarc_montiarcautomaton__visitor_MontiArcAutomatonVisitor(maaSTC);
    visitor.set_de_monticore_lang_montiarc_montiarc__visitor_MontiArcVisitor(maSTC);
    visitor
        .set_de_monticore_automaton_ioautomatonjava__visitor_IOAutomatonJavaVisitor(automatonSTC);
    visitor.set_de_monticore_lang_montiarc_montiarcbehavior__visitor_MontiArcBehaviorVisitor(
        behaviorSTC);
  }
  
  /**
   * Creates the symbol table starting from the <code>rootNode</code> and
   * returns the first scope that was created.
   *
   * @param rootNode the root node
   * @return the first scope that was created
   */
  public Scope createFromAST(
      de.monticore.lang.montiarc.montiarc._ast.ASTMACompilationUnit rootNode) {
    Log.errorIfNull(rootNode,
        "0xA7004_750 Error by creating of the MontiArcAutomatonSymbolTableCreator symbol table: top ast node is null");
    rootNode.accept(realThis);
    return getFirstCreatedScope();
  }
  
  @Override
  public MutableScope getFirstCreatedScope() {
    return maSTC.getFirstCreatedScope();
  }
  
  private AJavaVisitor realThis = visitor;
  
  @Override
  public AJavaVisitor getRealThis() {
    return realThis;
  }
  
  @Override
  public void setRealThis(AJavaVisitor realThis) {
    if (this.realThis != realThis) {
      this.realThis = realThis;
      visitor.setRealThis(realThis);
    }
  }
  
  @Override
  public void visit(ASTAJavaDefinition node) {
    AJavaDefinitionSymbol ajavaDef = new AJavaDefinitionSymbol(node.getName());
    addToScopeAndLinkWithNode(ajavaDef, node);
  }
  
  @Override
  public void endVisit(ASTAJavaDefinition node){
    removeCurrentScope();
  }
  
}
