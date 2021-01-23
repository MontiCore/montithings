package montithings._symboltable;

import com.google.common.collect.Lists;
import de.monticore.siunittypes4computing._ast.ASTSIUnitType4Computing;
import de.monticore.siunittypes4math._ast.ASTSIUnitType;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclaration;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.statements.mcvardeclarationstatements._symboltable.IMCVarDeclarationStatementsScope;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfNull;
import de.monticore.types.check.SynthesizeSymTypeFromMCFullGenericTypes;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;

import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public class MCVarDeclarationStatementsSTCForMontiThings extends MCVarDeclarationStatementsSTCForMontiThingsTOP{

  public MCVarDeclarationStatementsSTCForMontiThings(Deque<? extends IMCVarDeclarationStatementsScope> scopeStack) {
    super(scopeStack);
  }

  @Override
  public void endVisit(ASTLocalVariableDeclaration ast) {
    List<FieldSymbol> symbols = Lists.newArrayList();
    Iterator var4 = ast.getVariableDeclaratorList().iterator();

    while(var4.hasNext()) {
      ASTVariableDeclarator v = (ASTVariableDeclarator)var4.next();
      SymTypeExpression simpleType = this.createTypeLoader(ast.getMCType());
      v.getDeclarator().getSymbol().setType(simpleType);
      symbols.add(v.getDeclarator().getSymbol());
    }

    this.addModifiersToVariables(symbols, ast.getMCModifierList());
  }

  private SymTypeExpression createTypeLoader(ASTMCType ast) {
    if(ast instanceof ASTSIUnitType){
      SynthesizeSymTypeFromMontiThings synthesizeSymTypeFromMontiThings = new SynthesizeSymTypeFromMontiThings();
      ((ASTSIUnitType) ast).accept(synthesizeSymTypeFromMontiThings);
      return (SymTypeExpression)synthesizeSymTypeFromMontiThings.getResult().orElse(new SymTypeOfNull());
    }
    else if(ast instanceof ASTSIUnitType4Computing){
      SynthesizeSymTypeFromMontiThings synthesizeSymTypeFromMontiThings = new SynthesizeSymTypeFromMontiThings();
      ((ASTSIUnitType4Computing) ast).accept(synthesizeSymTypeFromMontiThings);
      return (SymTypeExpression)synthesizeSymTypeFromMontiThings.getResult().orElse(new SymTypeOfNull());
    }
    else {
      SynthesizeSymTypeFromMCFullGenericTypes syn = new SynthesizeSymTypeFromMCFullGenericTypes();
      ast.accept(syn);
      return (SymTypeExpression)syn.getResult().orElse(new SymTypeOfNull());
    }
  }
}
