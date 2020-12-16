// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

import de.monticore.cd.cd4analysis._ast.ASTCDEnumConstant;
import de.monticore.cd.cd4analysis._symboltable.CD4AnalysisGlobalScope;
import de.monticore.cd.cd4analysis._symboltable.CDFieldSymbol;
import de.monticore.symboltable.modifiers.AccessModifier;
import de.monticore.types.typesymbols._symboltable.FieldSymbol;
import de.monticore.types.typesymbols._symboltable.IFieldSymbolResolvingDelegate;
import montiarc._symboltable.adapters.CDField2FieldAdapter;
import org.codehaus.commons.nullanalysis.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class Field2CDFieldResolvingDelegate implements IFieldSymbolResolvingDelegate {

  protected CD4AnalysisGlobalScope globalScope;

  public Field2CDFieldResolvingDelegate(@NotNull CD4AnalysisGlobalScope globalScope) {
    this.globalScope = globalScope;
  }

  @Override
  public List<FieldSymbol> resolveAdaptedFieldSymbol(boolean foundSymbols, String name,
    AccessModifier modifier, Predicate<FieldSymbol> predicate) {
    List<FieldSymbol> result = new ArrayList<>();
    Optional<CDFieldSymbol> symbol = globalScope.resolveCDField(name, modifier);

    boolean symbolIsPublic = symbol.isPresent() && symbol.get().isIsPublic();

    // Enums do not allow modifiers => it's ok to import them without being public
    boolean symbolIsEnumConstant = symbol.isPresent() &&
      symbol.get().getAstNode() instanceof ASTCDEnumConstant;

    if (symbolIsPublic || symbolIsEnumConstant) {
      result.add(new CDField2FieldAdapter(symbol.get()));
    }

    return result;
  }
}