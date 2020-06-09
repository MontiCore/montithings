/* (c) https://github.com/MontiCore/monticore */
package de.monticore.lang.json.prettyprint;

import de.monticore.lang.json._ast.*;
import de.monticore.lang.json._ast.ASTSignedBasicDoubleLiteral;
import de.monticore.lang.json._visitor.JSONVisitor;
import de.monticore.literals.mccommonliterals._ast.*;
import de.monticore.prettyprint.IndentPrinter;

public class JSONPrettyPrinter extends IndentPrinter implements JSONVisitor {
  
  private LineState ls;
  
  /** Default Constructor. */
  public JSONPrettyPrinter() {
    
  }
  
  /**
   * Serializes and pretty-prints the JSON-AST.
   * 
   * @param jsonDocument The root node of the input AST
   * @return The pretty-printed JSON-AST as String
   */
  public String printJSONDocument(ASTJSONDocument jsonDocument) {
    clearBuffer();
    ls = LineState.OPEN_BLOCK;
    getRealThis().handle(jsonDocument);
    return getContent();
  }
  
  @Override
  public void visit(ASTJSONArray node) {
    smartLineBreak();
    println("[");
    indent();
    ls = LineState.OPEN_BLOCK;
  }
  
  @Override
  public void endVisit(ASTJSONArray node) {
    ls = LineState.CLOSE_BLOCK;
    smartLineBreak();
    unindent();
    print("]");
    ls = LineState.INLINE;
  }
  
  @Override
  public void visit(ASTJSONBoolean node) {
    smartLineBreak();
    print(node.getBooleanLiteral().getValue());
  }
  
  @Override
  public void endVisit(ASTJSONBoolean node) {
    ls = LineState.INLINE;
  }
  
  @Override
  public void visit(ASTJSONNull node) {
    smartLineBreak();
    print("null");
  }
  
  @Override
  public void endVisit(ASTJSONNull node) {
    ls = LineState.INLINE;
  }
  
  @Override
  public void visit(ASTJSONObject node) {
    smartLineBreak();
    println("{");
    indent();
    ls = LineState.OPEN_BLOCK;
  }
  
  @Override
  public void endVisit(ASTJSONObject node) {
    ls = LineState.CLOSE_BLOCK;
    smartLineBreak();
    unindent();
    print("}");
    ls = LineState.INLINE;
  }
  
  @Override
  public void visit(ASTJSONProperty node) {
    smartLineBreak();
    print("\"" + node.getKey() + "\": ");
    ls = LineState.OPEN_BLOCK;
  }
  
  @Override
  public void endVisit(ASTJSONProperty node) {
    ls = LineState.INLINE;
  }
  
  @Override
  public void visit(ASTSignedBasicDoubleLiteral node) {
    smartLineBreak();
    print(node.getSource());
  }
  
  @Override
  public void endVisit(ASTSignedBasicDoubleLiteral node) {
    ls = LineState.INLINE;
  }
  
  @Override
  public void visit(ASTSignedBasicFloatLiteral node) {
    smartLineBreak();
    print(node.getSource());
  }
  
  @Override
  public void endVisit(ASTSignedBasicFloatLiteral node) {
    ls = LineState.INLINE;
  }
  
  @Override
  public void visit(ASTSignedBasicLongLiteral node) {
    smartLineBreak();
    print(node.getSource());
  }
  
  @Override
  public void endVisit(ASTSignedBasicLongLiteral node) {
    ls = LineState.INLINE;
  }
  
  @Override
  public void visit(ASTSignedNatLiteral node) {
    smartLineBreak();
    print(node.getSource());
  }
  
  @Override
  public void endVisit(ASTSignedNatLiteral node) {
    ls = LineState.INLINE;
  }
  
  @Override
  public void visit(ASTStringLiteral node) {
    smartLineBreak();
    print("\"" + node.getSource() + "\"");
  }
  
  @Override
  public void endVisit(ASTStringLiteral node) {
    ls = LineState.INLINE;
  }
  
  /**
   * Prints custom line breaks with respect to the printed brackets from arrays
   * and properties. Adds a comma for inline collections.
   */
  private void smartLineBreak() {
    switch (ls) {
      case INLINE:
        println(",");
        break;
      case CLOSE_BLOCK:
        println();
        break;
      case OPEN_BLOCK:
        // no line break
        break;
      default:
        break;
    }
  }
  
  /**
   * Enumeration to track the line state in JSON with respect to the printed
   * brackets from arrays and properties.
   */
  public enum LineState {
    INLINE, OPEN_BLOCK, CLOSE_BLOCK
  }
}
