${tc.signature("packageName", "schemaName", "tagTypeName", "imports", "scopeSymbol", "nameScopeType",
  "regexPattern", "commentRegexPattern", "symbolParams")}

package ${packageName}.${schemaName};

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

<#list imports as import>
import ${import};
</#list>

import de.monticore.lang.tagging._ast.ASTNameScope;
import de.monticore.lang.tagging._ast.ASTScope;
import de.monticore.lang.tagging._ast.ASTTag;
import de.monticore.lang.tagging._ast.ASTTaggingUnit;
import de.monticore.lang.tagging._symboltable.TagSymbolCreator;
import de.monticore.lang.tagging._symboltable.TaggingResolver;
import de.monticore.symboltable.Scope;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;
import org.jscience.physics.amount.Amount;

/**
 * created by ComplexTagTypeCreator.ftl
 */
public class ${tagTypeName}SymbolCreator implements TagSymbolCreator {

  /**
   * regular expression pattern for:
   * ${commentRegexPattern}
   *
   * the pattern can be tested online at:
   * http://www.regexplanet.com/advanced/java/index.html
   */
  public static final Pattern pattern = Pattern.compile(
    "${regexPattern}"
  );

  public static Scope getGlobalScope(final Scope scope) {
    Scope s = scope;
    while (s.getEnclosingScope().isPresent()) {
      s = s.getEnclosingScope().get();
    }
    return s;
  }

  public void create(ASTTaggingUnit unit, TaggingResolver tagging) {
    if (unit.getQualifiedNameList().stream()
        .map(q -> q.toString())
        .filter(n -> n.endsWith("${schemaName}"))
        .count() == 0) {
      return; // the tagging model is not conform to the ${schemaName} tagging schema
    }
    final String packageName = Joiners.DOT.join(unit.getPackageList());
    final String rootCmp = // if-else does not work b/c of final (required by streams)
        (unit.getTagBody().getTargetModelOpt().isPresent()) ?
            Joiners.DOT.join(packageName, ((ASTNameScope) unit.getTagBody().getTargetModelOpt().get())
                .getQualifiedName().toString()) :
            packageName;

     for (ASTTag element : unit.getTagBody().getTagList()) {
            element.getTagElementList().stream()
              .filter(t -> t.getName().equals("${tagTypeName}"))
              .filter(t -> t.isPresentTagValue())
              .map(t -> matchRegexPattern(t.getTagValueOpt().get()))
              .filter(r -> r != null)
              .forEachOrdered(m ->
                  element.getScopeList().stream()
                    .filter(this::checkScope)
                    .map(s -> (ASTNameScope) s)
                    .map(s -> tagging.resolve(Joiners.DOT.join(rootCmp, // resolve down does not try to reload symbol
                            s.getQualifiedName().toString()), ${scopeSymbol}.KIND))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEachOrdered(s -> tagging.addTag(s,
                        new ${tagTypeName}Symbol(
                          ${symbolParams}
                        ))));
      }
  }

  protected Matcher matchRegexPattern(String regex) {
    Matcher matcher = pattern.matcher(regex);
    if (matcher.matches()) {
      return matcher;
    }
    Log.error(String.format("'%s' does not match the specified regex pattern '%s'",
        "It should fit this; ${commentRegexPattern}"
    ));
    return null;
  }

  protected boolean checkScope(ASTScope scope) {
    if (scope.getScopeKind().equals("${nameScopeType}")) {
      return true;
    }
    Log.error(String.format("0xT0005 Invalid scope kind: '%s'. ${tagTypeName} expects as scope kind '${nameScopeType}'.",
        scope.getScopeKind()), scope.get_SourcePositionStart());
    return false;
  }
}