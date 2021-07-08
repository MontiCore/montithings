// (c) https://github.com/MontiCore/monticore

package de.monticore.lang.sd4componenttesting.prettyprint;

import arcbasis._ast.ASTArcBasisNode;
import com.google.common.base.Preconditions;
import de.monticore.lang.sd4componenttesting._visitor.SD4ComponentTestingHandler;
import de.monticore.lang.sd4componenttesting._visitor.SD4ComponentTestingTraverser;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.lang.sd4componenttesting._ast.*;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Iterator;
import java.util.List;

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

  public <T extends ASTArcBasisNode> void acceptSeperatedList(@NotNull List<T> list) {
    if (list.size() <= 0) {
      return;
    }
    Iterator<T> iterator = list.iterator();
    iterator.next().accept(getTraverser());
    while (iterator.hasNext()) {
      this.getPrinter().print(", ");
      iterator.next().accept(getTraverser());
    }
  }

  //node handler

  @Override
  public void handle(@NotNull ASTSD4Artifact node) {
    getTraverser().visit(node);
    getTraverser().traverse(node);
    getTraverser().endVisit(node);
    getPrinter().print("SD4Artifact ");
  }

  @Override
  public void handle(@NotNull ASTTestDiagram node) {
    this.getPrinter().print(node.getName());
  }

  @Override
  public void handle(@NotNull ASTSD4CConnection node) {
    this.getPrinter().print(node.getEnclosingScope());
    getPrinter().print("SD4CConnection ");
  }

  @Override
  public void handle(@NotNull ASTSD4CElement node) {
    getPrinter().print("SD4CElement ");
  }

  @Override
  public void handle(@NotNull ASTSD4CExpression node) {
    getPrinter().print("SD4CExpression ");
  }

  @Override
  public void handle(@NotNull ASTSD4ComponentTestingNode node) {
    getPrinter().print("ComponentTestingNode ");
  }

}


