// (c) https://github.com/MontiCore/monticore
package montithings._visitor;

import com.google.common.base.Preconditions;
import de.monticore.prettyprint.IndentPrinter;
import genericarc._ast.ASTGenericArcNode;
import genericarc._ast.ASTGenericComponentHead;
import genericarc._visitor.GenericArcPrettyPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Iterator;
import java.util.List;

public class GenericToMontiArcPrettyPrinter extends GenericArcPrettyPrinter {
  
  public GenericToMontiArcPrettyPrinter(@NotNull IndentPrinter printer) {
    Preconditions.checkArgument(printer != null);
    this.printer = printer;
  }
  
  @Override
  public void handle(ASTGenericComponentHead node) {
    this.getPrinter().print("<");
    acceptGenericSeparatedList(node.getArcTypeParameterList());
    this.getPrinter().print("> ");
    if(node.isPresentParent()){
      this.getPrinter().print("extends ");
      node.getParent().accept(this.getTraverser());
    }
  }
  
  public <T extends ASTGenericArcNode> void acceptGenericSeparatedList(@NotNull List<T> list){
    if (list.size() <= 0) {
      return;
    }
    Iterator<T> iterator = list.iterator();
    iterator.next().accept(this.getTraverser());
    while (iterator.hasNext()) {
      this.getPrinter().print(", ");
      iterator.next().accept(this.getTraverser());
    }
  }
}