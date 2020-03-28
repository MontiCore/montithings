/* (c) https://github.com/MontiCore/monticore */
package montithings.cocos;

import de.monticore.java.symboltable.JavaTypeSymbolReference;
import de.monticore.mcexpressions._ast.ASTExpression;
import de.monticore.symboltable.types.JTypeSymbol;
import de.monticore.symboltable.types.references.JTypeReference;
import de.se_rwth.commons.logging.Log;
import montiarc._symboltable.PortSymbol;
import montiarc.helper.MontiArcHCJavaDSLTypeResolver;
import montiarc.helper.TypeCompatibilityChecker;
import montithings._ast.ASTComponent;
import montithings._ast.ASTPortValuation;
import montithings._cocos.MontiThingsASTComponentCoCo;
import montithings._symboltable.ComponentSymbol;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * TODO
 *
 * @since 28.03.20
 */
public class IfThenElseValuesCorrectlyAssigned implements MontiThingsASTComponentCoCo {

  protected boolean typesCompatible(JTypeReference<? extends JTypeSymbol> from,
      JTypeReference<? extends JTypeSymbol> to) {
    return TypeCompatibilityChecker.doTypesMatch(from,
        from.getReferencedSymbol().getFormalTypeParameters().stream()
            .map(p -> (JTypeSymbol) p).collect(Collectors.toList()),
        from.getActualTypeArguments().stream()
            .map(a -> (JavaTypeSymbolReference) a.getType())
            .collect(Collectors.toList()),
        to,
        to.getReferencedSymbol().getFormalTypeParameters().stream()
            .map(p -> (JTypeSymbol) p).collect(Collectors.toList()),
        to.getActualTypeArguments().stream()
            .map(a -> (JavaTypeSymbolReference) a.getType())
            .collect(Collectors.toList()));
  }

  protected boolean typesCompatible(ASTExpression from, JTypeReference<? extends JTypeSymbol> to) {
    return typesCompatible(getExpressionType(from), to);
  }

  protected JTypeReference<? extends JTypeSymbol> getExpressionType(ASTExpression expression) {
    MontiArcHCJavaDSLTypeResolver javaTypeResolver
        = new MontiArcHCJavaDSLTypeResolver();
    expression.accept(javaTypeResolver);
    Optional<JavaTypeSymbolReference> expressionType = javaTypeResolver.getResult();
    if (!expressionType.isPresent()) {
      Log.error("0xMT013 Could not resolve type of default value " +
          "for comparing it with the referenced port type.", expression.get_SourcePositionStart());
      // Unreachable
      return null;
    }
    return expressionType.get();
  }

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
    Set<ASTPortValuation> portValuations = comp.getExecutionStatements().stream()
        .flatMap(s -> s.getPortValuationList().stream()).collect(Collectors.toSet());
    if (comp.getElseStatement().isPresent()) {
      portValuations.addAll(comp.getElseStatement().get().getPortValuationList());
    }

    List<PortSymbol> ports = new ArrayList<>();
    List<ASTExpression> expressions = new ArrayList<>();
    for (ASTPortValuation valuation : portValuations) {
      Optional<PortSymbol> portOpt = comp.getPort(valuation.getPort());
      if (!portOpt.isPresent()) {
        Log.error("0xMT015 Port " + valuation.getPort() + " used in if statement does not exist. ");
        return;
      }
      ports.add(portOpt.get());
      expressions.add(valuation.getValue().getExpression());
    }

    if (ports.size() != expressions.size()) {
      Log.error("0xMT016 Value assignments in if-then-else statements are malformed.");
    }

    for (int i = 0; i < ports.size(); i++) {
      JTypeReference<? extends JTypeSymbol> portType = ports.get(i).getTypeReference();

      if (!typesCompatible(expressions.get(i), portType)) {
        Log.error(
            String.format("0xMT014 Type of port \"%s\"" +
                    " does not match the type of the" +
                    " value assigned to it in if-then-else statement. "
                    + "Type \"%s\" can not cast to type \"%s\".",
                ports.get(i).getName(), getExpressionType(expressions.get(i)).getName(),
                portType.getName()));
      }
    }
  }
}