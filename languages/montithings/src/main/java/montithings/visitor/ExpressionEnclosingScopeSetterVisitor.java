/* (c) https://github.com/MontiCore/monticore */
package montithings.visitor;

import de.monticore.mcexpressions._ast.*;
import de.monticore.mcexpressions._visitor.MCExpressionsVisitor;
import de.monticore.symboltable.Scope;

/**
 * Workaround for MontiCore Bug that prevents setting enclosing scope correctly
 *
 * @author (last commit) kirchhof
 * @version , 12.02.2020
 * @since
 */
public class ExpressionEnclosingScopeSetterVisitor implements MCExpressionsVisitor {
  private MCExpressionsVisitor realThis = this;

  private final Scope enclosingScope;

  @Override
  public void setRealThis(MCExpressionsVisitor realThis) {
    this.realThis = realThis;
  }

  @Override
  public MCExpressionsVisitor getRealThis() {
    return realThis;
  }

  @Override
  public void visit(ASTExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override
  public void visit(ASTNameExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override
  public void visit(ASTLiteralExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTQualifiedNameExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTThisExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTSuperExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTGenericInvocationExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTArrayExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTCallExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTTypeCastExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTSuffixExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTPrefixExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTBooleanNotExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTLogicalNotExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTMultExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTAddExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTShiftExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTComparisonExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTInstanceofExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTIdentityExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTBinaryAndOpExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTBinaryXorOpExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTBinaryOrOpExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTBooleanAndOpExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTBooleanOrOpExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTConditionalExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTAssignmentExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTBracketExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTPrimaryThisExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTPrimarySuperExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTClassExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTPrimaryGenericInvocationExpression node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTGenericInvocationSuffix node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTSuperSuffix node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTArguments node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  @Override public void visit(ASTMCExpressionsNode node) {
    node.setEnclosingScope(this.enclosingScope);
  }

  /* ============================================================ */
  /* ====================== GENERATED CODE ====================== */
  /* ============================================================ */

  public ExpressionEnclosingScopeSetterVisitor(Scope enclosingScope) {
    this.enclosingScope = enclosingScope;
  }
}
