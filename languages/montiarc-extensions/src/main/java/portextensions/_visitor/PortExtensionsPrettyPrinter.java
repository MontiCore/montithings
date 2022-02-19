// (c) https://github.com/MontiCore/monticore
package portextensions._visitor;

import arcbasis._ast.ASTArcBasisNode;
import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.commons.nullanalysis.NotNull;
import portextensions._ast.ASTAnnotatedPort;
import portextensions._ast.ASTBufferedPort;
import portextensions._ast.ASTSyncStatement;

import java.util.Iterator;
import java.util.List;

public class PortExtensionsPrettyPrinter implements PortExtensionsHandler {

  protected PortExtensionsTraverser traverser;

  protected IndentPrinter printer;

  public PortExtensionsPrettyPrinter() {
    this.printer = new IndentPrinter();
  }

  public PortExtensionsPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }

  @Override
  public PortExtensionsTraverser getTraverser() {
    return traverser;
  }

  @Override
  public void setTraverser(@NotNull PortExtensionsTraverser traverser) {
    Preconditions.checkArgument(traverser != null);
    this.traverser = traverser;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  public <T extends ASTArcBasisNode> void acceptSeperatedList(@NotNull List<T> list) {
    if (list.isEmpty()) {
      return;
    }
    Iterator<T> iterator = list.iterator();
    iterator.next().accept(getTraverser());
    while (iterator.hasNext()) {
      this.getPrinter().print(", ");
      iterator.next().accept(getTraverser());
    }
  }

  @Override
  public void handle(ASTAnnotatedPort node) {
    node.getPortAnnotation().accept(getTraverser());
    this.getPrinter().print(" port ");
    acceptSeperatedList(node.getPortDeclarationList());
    this.getPrinter().println(";");
  }

  @Override
  public void handle(ASTBufferedPort node) {
    this.getPrinter().print("buffer");
  }

  @Override
  public void handle(ASTSyncStatement node) {
    this.getPrinter().print("sync ");
    this.getPrinter().print(StringUtils.join(node.getSyncedPortList(), ","));
    this.getPrinter().println(";");
  }
}