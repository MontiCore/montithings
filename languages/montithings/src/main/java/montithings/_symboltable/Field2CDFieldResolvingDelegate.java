// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;


import de.monticore.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cdinterfaceandenum._ast.ASTCDEnumConstant;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.IFieldSymbolResolver;
import de.monticore.symboltable.modifiers.AccessModifier;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Field2CDFieldResolvingDelegate implements IFieldSymbolResolver {

  protected CD4AnalysisGlobalScope globalScope;

  public Field2CDFieldResolvingDelegate(@NotNull CD4AnalysisGlobalScope globalScope) {
    this.globalScope = globalScope;
  }

  @Override
  public List<FieldSymbol> resolveAdaptedFieldSymbol(boolean foundSymbols, String name,
    AccessModifier modifier, Predicate<FieldSymbol> predicate) {
    List<FieldSymbol> result = new ArrayList<>();
    Optional<FieldSymbol> symbol = globalScope.resolveField(name, modifier);

    boolean symbolIsPublic = symbol.isPresent() && symbol.get().isIsPublic();

    // Enums do not allow modifiers => it's ok to import them without being public
    boolean symbolIsEnumConstant = symbol.isPresent() &&
      symbol.get().getAstNode() instanceof ASTCDEnumConstant;

    if (symbolIsPublic || symbolIsEnumConstant) {
      result.add(symbol.get());
    }

    return result;
  }
}