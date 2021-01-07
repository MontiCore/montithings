package montithings._symboltable;

import arcbasis._symboltable.IArcBasisScope;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunits._ast.ASTSIUnitsNode;
import de.monticore.siunittypes4math._ast.ASTSIUnitType;
import de.monticore.siunittypes4math.prettyprint.SIUnitTypes4MathPrettyPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.prettyprint.MCBasicTypesPrettyPrinter;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.Deque;

public class ArcBasisSTCForMontiThings extends ArcBasisSTCForMontiThingsTOP{

  public ArcBasisSTCForMontiThings(Deque<? extends IArcBasisScope> scopeStack){
    super(scopeStack);
  }

  public ArcBasisSTCForMontiThings(Deque<? extends IArcBasisScope> scopeStack, MCBasicTypesPrettyPrinter typePrinter){
    super(scopeStack);
    setTypePrinter(typePrinter);
  }

  public void setTypeVisitor(MCBasicTypesPrettyPrinter typePrinter){
    setTypePrinter(typePrinter);
  }

  @Override
  protected String printType(@NotNull ASTMCType type) {
    assert type != null;
    if (!(type instanceof ASTSIUnitType)){
      return type.printType(this.getTypePrinter());
    }
    return SIUnitTypes4MathPrettyPrinter.prettyprint((ASTSIUnitType) type);
  }
}
