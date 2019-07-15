
package montiarc.tagging.distribution;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;


import de.monticore.lang.tagging._ast.ASTNameScope;
import de.monticore.lang.tagging._ast.ASTScope;
import de.monticore.lang.tagging._ast.ASTTag;
import de.monticore.lang.tagging._ast.ASTTaggingUnit;
import de.monticore.lang.tagging._symboltable.TagSymbolCreator;
import de.monticore.lang.tagging._symboltable.TaggingResolver;
import de.monticore.symboltable.Scope;
import de.monticore.symboltable.Symbol;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.logging.Log;

import de.monticore.lang.tagvalue._parser.TagValueParser;
import de.monticore.lang.tagvalue._ast.ASTStringTagValue;
import montiarc._symboltable.PortSymbol;

import javax.sound.sampled.Port;

/**
 * Creator for Connection Symbols
 */
public class ConnectionSymbolCreator implements TagSymbolCreator {

  public static Scope getGlobalScope(final Scope scope) {
    Scope s = scope;
    while (s.getEnclosingScope().isPresent()) {
      s = s.getEnclosingScope().get();
    }
    return s;
  }

  /**
   *  Creates the Connection symbol for an ASTNode and links it with the MontiArc PortSymbol that
   *  was tagged.
   * @param unit ASTNode for the symbol that is created
   * @param tagging
   */
  public void create(ASTTaggingUnit unit, TaggingResolver tagging) {
    if (unit.getQualifiedNameList().stream()
        .map(q -> q.toString())
        .filter(n -> n.endsWith("DistributionSchema"))
        .count() == 0) {
      return; // the tagging model is not conform to the DistributionSchema tagging schema
    }
    final String packageName = Joiners.DOT.join(unit.getPackageList());
    final String rootCmp = // if-else does not work b/c of final (required by streams)
        (unit.getTagBody().getTargetModelOpt().isPresent()) ?
            Joiners.DOT.join(packageName, ((ASTNameScope) unit.getTagBody().getTargetModelOpt().get())
                .getQualifiedName().toString()) :
            packageName;

    for (ASTTag element : unit.getTagBody().getTagList()) {
           element.getTagElementList().stream()
              .filter(t -> t.getName().equals("Connection"))
              .filter(t -> t.isPresentTagValue())
              .map(t -> checkContent(t.getTagValueOpt().get()))
              .filter(r -> r != null)
              .forEachOrdered(v ->
                  element.getScopeList().stream()
                    .filter(this::checkScope)
                    .map(s -> (ASTNameScope) s)
                    .map(s -> tagging.resolve(Joiners.DOT.join(rootCmp, // resolve down does not try to reload symbol
                        s.getQualifiedName().toString()), PortSymbol.KIND))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEachOrdered(s -> {
                        ConnectionSymbol tmpSymbol = new ConnectionSymbol(v);
                        tagging.addTag(s, tmpSymbol);
                          PortSymbol p = (PortSymbol) s;
                          p.setConnectionSymbol(tmpSymbol);
                      }
                    ));
    }
  }

  protected String checkContent(String s) {
    TagValueParser parser = new TagValueParser();
    Optional<ASTStringTagValue> ast;
    try {
      boolean enableFailQuick = Log.isFailQuickEnabled();
      Log.enableFailQuick(false);
      long errorCount = Log.getErrorCount();

      ast = parser.parse_StringStringTagValue(s);

      Log.enableFailQuick(enableFailQuick);
      if (Log.getErrorCount() > errorCount) {
        throw new Exception("Error occured during parsing.");
      }
    } catch (Exception e) {
      Log.error(String.format("0xT0004 Could not parse %s with TagValueParser#parseStringTagValue.",
          s), e);
      return null;
    }
    if (!ast.isPresent()) {
      return null;
    }
    return ast.get().getString();
  }

  protected PortSymbol checkKind(Collection<Symbol> symbols) {
    PortSymbol ret = null;
    for (Symbol symbol : symbols) {
      if (symbol.getKind().isSame(PortSymbol.KIND)) {
        if (ret != null) {
          Log.error(String.format("0xA4095 Found more than one symbol: '%s' and '%s'",
              ret, symbol));
          return null;
        }
        ret = (PortSymbol)symbol;
      }
    }
    if (ret == null) {
      Log.error(String.format("0xT0001 Invalid symbol kinds: %s. tagTypeName expects as symbol kind 'PortSymbol.KIND'.",
          symbols.stream().map(s -> "'" + s.getKind().toString() + "'").collect(Collectors.joining(", "))));
      return null;
    }
    return ret;
  }

  protected boolean checkScope(ASTScope scope) {
    if (scope.getScopeKind().equals("NameScope")) {
      return true;
    }
    Log.error(String.format("0xT0005 Invalid scope kind: '%s'. Connection expects as scope kind 'NameScope'.",
        scope.getScopeKind()), scope.get_SourcePositionStart());
    return false;
  }
}