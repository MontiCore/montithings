// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

import com.google.common.collect.Lists;
import de.monticore.siunittypes4computing._ast.ASTSIUnitType4Computing;
import de.monticore.siunittypes4math._ast.ASTSIUnitType;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTLocalVariableDeclaration;
import de.monticore.statements.mcvardeclarationstatements._ast.ASTVariableDeclarator;
import de.monticore.statements.mcvardeclarationstatements._symboltable.MCVarDeclarationStatementsSTCompleteTypes;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.types.check.FullSynthesizeFromMCFullGenericTypes;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfNull;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;

import java.util.Iterator;
import java.util.List;

public class MCVarDeclarationStatementsSTCForMontiThings
  extends MCVarDeclarationStatementsSTCompleteTypes {

  @Override
  public void endVisit(ASTLocalVariableDeclaration ast) {
    List<FieldSymbol> symbols = Lists.newArrayList();

    for (ASTVariableDeclarator v : ast.getVariableDeclaratorList()) {
      SymTypeExpression simpleType = this.createTypeLoader(ast.getMCType());
      v.getDeclarator().getSymbol().setType(simpleType);
      symbols.add(v.getDeclarator().getSymbol());
    }

    this.addModifiersToVariables(symbols, ast.getMCModifierList());
  }

  protected SymTypeExpression createTypeLoader(ASTMCType ast) {
    if (ast instanceof ASTSIUnitType) {
      SynthesizeSymTypeFromMontiThings synthesizeSymTypeFromMontiThings = new SynthesizeSymTypeFromMontiThings();
      ((ASTSIUnitType) ast).accept(synthesizeSymTypeFromMontiThings.getTraverser());
      return synthesizeSymTypeFromMontiThings.getResult().orElse(new SymTypeOfNull());
    }
    else if (ast instanceof ASTSIUnitType4Computing) {
      SynthesizeSymTypeFromMontiThings synthesizeSymTypeFromMontiThings = new SynthesizeSymTypeFromMontiThings();
      ((ASTSIUnitType4Computing) ast).accept(synthesizeSymTypeFromMontiThings.getTraverser());
      return synthesizeSymTypeFromMontiThings.getResult().orElse(new SymTypeOfNull());
    }
    else {
      FullSynthesizeFromMCFullGenericTypes syn = new FullSynthesizeFromMCFullGenericTypes();
      ast.accept(syn.getTraverser());
      return syn.getResult().orElse(new SymTypeOfNull());
    }
  }
}
