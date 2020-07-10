/* (c) https://github.com/MontiCore/monticore */
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

public class PortExtensionsPrettyPrinter implements PortExtensionsVisitor {

  private PortExtensionsVisitor realThis = this;
  protected IndentPrinter printer;

  public PortExtensionsPrettyPrinter() {
    IndentPrinter printer = new IndentPrinter();
    this.printer = printer;
  }

  public PortExtensionsPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }

  @Override
  public PortExtensionsVisitor getRealThis() {
    return this.realThis;
  }

  @Override
  public void setRealThis(@NotNull PortExtensionsVisitor realThis) {
    Preconditions.checkArgument(realThis != null);
    this.realThis = realThis;
  }

  public IndentPrinter getPrinter() {
    return this.printer;
  }

  public <T extends ASTArcBasisNode> void acceptSeperatedList(@NotNull List<T> list){
    if (list.size() <= 0) {
      return;
    }
    Iterator<T> iterator = list.iterator();
    iterator.next().accept(this.getRealThis());
    while (iterator.hasNext()) {
      this.getPrinter().print(", ");
      iterator.next().accept(this.getRealThis());
    }
  }

  @Override
  public void handle(ASTAnnotatedPort node){
    node.getPortAnnotation().accept(this.getRealThis());
    this.getPrinter().print(" port ");
    acceptSeperatedList(node.getPortDeclarationList());
    this.getPrinter().println(";");
  }

  @Override
  public void handle(ASTBufferedPort node){
    this.getPrinter().print("buffer");
  }

  @Override
  public void handle(ASTSyncStatement node){
    this.getPrinter().print("sync ");
    this.getPrinter().print(StringUtils.join(node.getSyncedPortList(),","));
    this.getPrinter().println(";");
  }
}