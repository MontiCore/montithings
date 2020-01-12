// (c) https://github.com/MontiCore/monticore
package montithings._symboltable;

import com.google.common.collect.ImmutableSet;
import de.monticore.symboltable.SymbolKind;
import de.se_rwth.commons.Joiners;
import de.se_rwth.commons.Splitters;
import montiarc._symboltable.ComponentInstanceSymbol;
import montiarc._symboltable.ComponentSymbol;
import montiarc._symboltable.PortSymbol;
import montiarc._symboltable.VariableSymbol;
import montiarc.cocos.PackageLowerCase;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Helps loading inner components, by mapping their full-qualified names to the full-qualified name
 * of the most outer component of the file the inner one is defined in. This way the SymTab knows
 * which file to load. By convention, package names must be lower-case (see {@link PackageLowerCase}
 * ) and component names must start upper-case (see {@link montiarc.cocos.NamesCorrectlyCapitalized}). This
 * ensures, that we can calculate the most outer component, by searching for the first upper-case
 * part of a full-qualified name, e.g.:<br/>
 * a.b.C.D.E -> a.b.C
 *
 * @author Robert Heim, Michael von Wenckstern
 */
public class MontiThingsModelNameCalculator
    extends de.monticore.CommonModelNameCalculator {

  @Override
  public Set<String> calculateModelNames(final String name, final SymbolKind kind) {

    if (ComponentSymbol.KIND.isKindOf(kind)) {
      return calculateModelNameForComponent(name);
    }
    else if (PortSymbol.KIND.isKindOf(kind) || VariableSymbol.KIND.isKindOf(kind)) {
      return calculateModelNameForPort(name);
    }
    else if (ComponentInstanceSymbol.KIND.isKindOf(kind)) {
      return calculateModelNameForComponentInstance(name);
    }

    if (ResourcePortSymbol.KIND.isKindOf(kind)) {
      return calculateModelNamesForResourcePort(name);
    }

    if (SyncStatementSymbol.KIND.isKindOf(kind)) {
      return calculateModelNamesForSyncStatement(name);
    }

    return new LinkedHashSet<>();
  }

  private Set<String> calculateModelNameForComponent(String name) {
    List<String> parts = Splitters.DOT.splitToList(name);
    Set<String> ret = new LinkedHashSet<>();

    for (int i = 0; i < parts.size(); i++) {
      char[] c = parts.get(i).toCharArray();
      if (Character.isUpperCase(c[0])) {
        ret.add(Joiners.DOT.join(parts.subList(0, i + 1)));
      }
    }
    //There is no Part that starts with Uppercase. This Error is handled by NamesCorrectlyCapitalized but does not result in SymTab error
    if (ret.isEmpty()) {
      ret.add(name);
    }
    return Collections.unmodifiableSet(ret);
  }

  private Set<String> calculateModelNameForPort(String name) {
    List<String> parts = Splitters.DOT.splitToList(name);
    if (parts.size() > 1) {
      String modelName = Joiners.DOT.join(parts.subList(0, parts.size() - 1));
      return ImmutableSet.<String>builder()
          .addAll(calculateModelNameForComponent(modelName))
          .build();
    }
    return ImmutableSet.of();
  }

  private Set<String> calculateModelNameForComponentInstance(String name) {
    List<String> parts = Splitters.DOT.splitToList(name);
    if (parts.size() > 1) {
      return calculateModelNameForComponent(Joiners.DOT.join(parts.subList(0, parts.size() - 1)));
    }
    return ImmutableSet.of();
  }

  protected Set<String> calculateModelNameForConnector(String name) {
    List<String> parts = Splitters.DOT.splitToList(name);
    if (parts.size() == 1) {
      return calculateModelNameForComponent(Joiners.DOT.join(parts.subList(0, parts.size() - 1)));
    }
    else if (parts.size() >= 2) {
      return ImmutableSet.<String>builder()
          .addAll(
              calculateModelNameForComponent(Joiners.DOT.join(parts.subList(0, parts.size() - 1))))
          .addAll(
              calculateModelNameForComponent(Joiners.DOT.join(parts.subList(0, parts.size() - 2))))
          .build();
    }
    return ImmutableSet.of();
  }

  private Set<String> calculateModelNamesForResourcePort(String name) {
    final Set<String> modelNames = new LinkedHashSet<>();
    modelNames.add(name);
    return modelNames;
  }

  protected Set<String> calculateModelNamesForSyncStatement(String name) {
    final Set<String> modelNames = new LinkedHashSet<>();
    modelNames.add(name);
    return modelNames;
  }

}
