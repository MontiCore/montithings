// (c) https://github.com/MontiCore/monticore

package de.monticore.lang.sd4componenttesting._visitor;

import arcbasis._ast.ASTPortAccess;
import com.google.common.base.Preconditions;
import de.monticore.literals.mcliteralsbasis._ast.ASTLiteral;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.lang.sd4componenttesting._ast.*;
import de.monticore.lang.sd4componenttesting.util.SD4CElementType;

import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Iterator;

public class SD4ComponentTestingPrettyPrinter implements SD4ComponentTestingHandler {

  protected SD4ComponentTestingTraverser traverser;

  protected IndentPrinter printer;

  protected SD4CElementType lastElementType;

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
  public void traverse(de.monticore.lang.sd4componenttesting._ast.ASTSD4Artifact node) {
    if (node.isPresentPackageDeclaration()) {
      this.getPrinter().print("package ");
      node.getPackageDeclaration().accept(getTraverser());
      this.getPrinter().print(";\n\n");
    }
    {
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
    this.getPrinter().indent(1);
    for (int i = 0; i < node.getSD4CElementList().size(); i++) {
      ASTSD4CElement c = node.getSD4CElement(i);
      c.accept(this.getTraverser());
      this.getPrinter().println(";");
    }
    this.getPrinter().unindent();
    this.getPrinter().print("}\n");
  }


  @Override
  public void handle(@NotNull ASTSD4CConnection node) {
    groupByElementType(node);
    if (node.isPresentSource()) {
      node.getSource().accept(this.getTraverser());
      this.getPrinter().print(" ");
    }
    this.getPrinter().print("-> ");
    if (!node.getTargetList().isEmpty()) {
      int counter = 0;
      for (ASTPortAccess target : node.getTargetList()) {
        counter++;
        target.accept(this.getTraverser());
        if (node.getTargetList().size() > counter) {
          this.getPrinter().print(", ");
        }
      }
      this.getPrinter().print(" ");
    }
    this.getPrinter().print(": ");
    if (!node.getValueList().isEmpty()) {
      int counter = 0;
      for (ASTLiteral value : node.getValueList()) {
        counter++;
        value.accept(this.getTraverser());
        if (node.getValueList().size() > counter) {
          this.getPrinter().print(", ");
        }
      }
    }

  }

  public void groupByElementType(@NotNull ASTSD4CElement node) {
    if (this.lastElementType != node.getType()) {
      if (this.lastElementType == null) {
        // first element, skip
      } else if (node.getType() == SD4CElementType.EXPRESSION) {
        // line break before assert block
        this.getPrinter().println();
      } else if (this.lastElementType == SD4CElementType.EXPRESSION) {
        // line break after assert block
        this.getPrinter().println();
      }
      this.lastElementType = node.getType();
    }
  }

  @Override
  public void handle(@NotNull ASTSD4CExpression node) {
    groupByElementType(node);
    this.getPrinter().print("assert ");
    node.getExpression().accept(this.getTraverser());
  }

  @Override
  public void handle(@NotNull ASTSD4CDelay node) {
    groupByElementType(node);
    this.getPrinter().print("delay");
    this.getPrinter().print(" ");
    node.getSIUnitLiteral().accept(this.getTraverser());
  }
}


