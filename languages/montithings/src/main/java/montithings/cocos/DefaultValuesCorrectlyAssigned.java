/* (c) https://github.com/MontiCore/monticore */
package montithings.cocos;

import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.monticore.types.TypesHelper;
import de.monticore.types.TypesPrinter;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.ComponentSymbol;
import montiarc.helper.MontiArcHCJavaDSLTypeResolver;
import montiarc.helper.TypeCompatibilityChecker;
import montithings._ast.ASTComponent;
import montithings._ast.ASTMTPortDeclaration;
import montithings._ast.ASTPort;
import montithings._cocos.MontiThingsASTComponentCoCo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Checks that default values have types matching their port types.
 * Adapted from MontiArc's DefaultParametersCorrectlyAssigned CoCo
 *
 * @since 24.03.20
 */
public class DefaultValuesCorrectlyAssigned implements MontiThingsASTComponentCoCo {
  @Override public void check(ASTComponent node) {
    if (!node.getSymbolOpt().isPresent()) {
      Log.error(
          String.format("0xMA010 ASTComponent node \"%s\" has no " +
                  "symbol. Did you forget to run the " +
                  "SymbolTableCreator before checking cocos?",
              node.getName()));
      return;
    }
    ComponentSymbol comp = (ComponentSymbol) node.getSymbolOpt().get();
    List<ASTPort> ports = node.getPorts().stream().map(p -> (ASTPort) p)
        .collect(Collectors.toList());

    for (ASTPort port : ports) {
      for (ASTMTPortDeclaration declaration : port.getMTPortDeclarationList())
        if (declaration.isPresentDefault()) {
          int dimension = TypesHelper.getArrayDimensionIfArrayOrZero(port.getType());
          JTypeReference<? extends JTypeSymbol> paramTypeSymbol = new JavaTypeSymbolReference(
              TypesPrinter.printTypeWithoutTypeArgumentsAndDimension(port.getType()),
              comp.getSpannedScope(), dimension);

          MontiArcHCJavaDSLTypeResolver javaTypeResolver
              = new MontiArcHCJavaDSLTypeResolver();
          ASTExpression expression = declaration.getDefault().getExpression();
          // param.getDefaultValue().get().getValue().accept(javaTypeResolver);
          expression.accept(javaTypeResolver);
          Optional<JavaTypeSymbolReference> result = javaTypeResolver.getResult();
          if (!result.isPresent()) {
            Log.error(
                "0xMT013 Could not resolve type of default value " +
                    "for comparing it with the referenced port type.",
                declaration.getDefault().get_SourcePositionStart());
          }
          else if (!TypeCompatibilityChecker.doTypesMatch(result.get(),
              result.get().getReferencedSymbol().getFormalTypeParameters().stream()
                  .map(p -> (JTypeSymbol) p).collect(Collectors.toList()),
              result.get().getActualTypeArguments().stream()
                  .map(a -> (JavaTypeSymbolReference) a.getType())
                  .collect(Collectors.toList()),
              paramTypeSymbol,
              paramTypeSymbol.getReferencedSymbol().getFormalTypeParameters().stream()
                  .map(p -> (JTypeSymbol) p).collect(Collectors.toList()),
              paramTypeSymbol.getActualTypeArguments().stream()
                  .map(a -> (JavaTypeSymbolReference) a.getType())
                  .collect(Collectors.toList()))) {
            Log.error(
                String.format("0xMT014 Type of port \"%s\"" +
                        " does not match the type of its assigned " +
                        "default value. Type \"%s\" can not cast to type \"%s\".",
                    declaration.getName(),
                    paramTypeSymbol.getName(),
                    result.get().getName()),
                port.get_SourcePositionStart());
          }
        }
    }
  }
}
