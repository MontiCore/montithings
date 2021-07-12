// (c) https://github.com/MontiCore/monticore

package de.monticore.lang.sd4componenttesting._visitor;

import arcbasis._ast.ASTPortAccess;
import com.google.common.base.Preconditions;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.lang.sd4componenttesting._ast.*;
import org.codehaus.commons.nullanalysis.NotNull;
import java.util.Iterator;

public class SD4ComponentTestingPrettyPrinter implements SD4ComponentTestingHandler {

  protected SD4ComponentTestingTraverser traverser;

  protected IndentPrinter printer;

  public SD4ComponentTestingPrettyPrinter() {
    IndentPrinter printer = new IndentPrinter();
    this.printer = printer;
  }

  public SD4ComponentTestingPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }

  @Override
  public SD4ComponentTestingTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NotNull SD4ComponentTestingTraverser traverser) {
    Preconditions.checkArgument(traverser != null);
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  //node handler

  @Override
  public  void traverse (de.monticore.lang.sd4componenttesting._ast.ASTSD4Artifact node)  {


    if (node.isPresentPackageDeclaration()) {
      this.getPrinter().print("package ");
      node.getPackageDeclaration().accept(getTraverser());
      this.getPrinter().print(";\n\n");
    }
    { // TODO werden aktuell nicht betarchtet
      Iterator<de.monticore.types.mcbasictypes._ast.ASTMCImportStatement> iter_mCImportStatements = node.getMCImportStatementList().iterator();
      while (iter_mCImportStatements.hasNext()) {
        iter_mCImportStatements.next().accept(getTraverser());
      }
    }
    if (null != node.getTestDiagram()) {
      node.getTestDiagram().accept(getTraverser());
    }


  }


  public void handle(@NotNull ASTTestDiagram node) {
    this.getPrinter().print("testdiagram ");
    this.getPrinter().print(node.getName());
    this.getPrinter().print(" for ");
    this.getPrinter().print(node.getMainComponent());
    this.getPrinter().print(" {\n");
    this.getTraverser().traverse(node);
    this.getPrinter().print("}");
  }


  @Override
  public void handle(@NotNull ASTSD4CConnection node) {
    if(node.isPresentSource()) {
      node.getSource().accept(this.getTraverser());
    }
    this.getPrinter().print(" -> ");
    if(!node.getTargetList().isEmpty()) {
      int counter = 0;
      for(ASTPortAccess target: node.getTargetList()) {
        counter++;
        target.accept(this.getTraverser());
        if(node.getTargetList().size() > counter) {
          this.getPrinter().print(", ");
        }
      }
    }
    this.getPrinter().print(" : ");
    if(!node.getValueList().isEmpty()) {
      int counter = 0;
      for(ASTLiteral value: node.getValueList()) {
        counter++;
        value.accept(this.getTraverser());
        if(node.getValueList().size() > counter) {
          this.getPrinter().print(", ");
        }
      }
    }
    this.getPrinter().print(";\n");

  }
}


