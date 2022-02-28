// (c) https://github.com/MontiCore/monticore
package montithings.generator.cocos;

import arcbasis._symboltable.ComponentTypeSymbol;
import com.google.common.base.Preconditions;
import de.se_rwth.commons.logging.Log;
import montithings._ast.ASTMTComponentType;
import montithings._cocos.MontiThingsASTMTComponentTypeCoCo;
import montithings.generator.helper.ComponentHelper;
import montithings.generator.helper.FileHelper;
import montithings.util.MontiThingsError;

import java.io.File;

/**
 * Checks that components have some kind of behavior (e.g. MCStatements or HWC)
 */
public class ComponentHasBehavior implements MontiThingsASTMTComponentTypeCoCo {
  protected final File hwcPath;

  public ComponentHasBehavior(File hwcPath) {
    this.hwcPath = hwcPath;
  }

  @Override public void check(ASTMTComponentType node) {
    Preconditions.checkArgument(node != null);
    Preconditions.checkArgument(node.isPresentSymbol(), "ASTComponent node '%s' has no symbol. "
      + "Did you forget to run the SymbolTableCreator before checking cocos?", node.getName());
    final ComponentTypeSymbol compSymbol = node.getSymbol();

    boolean hasHwc = FileHelper.existsHWCClass(hwcPath, compSymbol.getFullName());
    boolean hasBehavior = ComponentHelper.hasBehavior(compSymbol)
      ||  ComponentHelper.hasStatechart(compSymbol)
      || !ComponentHelper.getPortSpecificBehaviors(compSymbol).isEmpty()
      || ComponentHelper.hasInitBehavior(compSymbol);
    boolean hasEveryBlock = !ComponentHelper.getEveryBlocks(compSymbol).isEmpty();
    boolean isComposed = compSymbol.isDecomposed();
    boolean isInterfaceComp = node.getMTComponentModifier().isInterface();

    if (!hasHwc && !hasBehavior && !hasEveryBlock && !isComposed && !isInterfaceComp) {
      Log.error(String.format(MontiThingsError.NO_BEHAVIOR.toString(), compSymbol.getFullName()));
    }

    boolean hasIncomingPorts = !compSymbol.getIncomingPorts().isEmpty();

    if (!hasBehavior && hasEveryBlock && hasIncomingPorts) {
      Log.warn(String.format(MontiThingsError.NO_BEHAVIOR_ONLY_EVERY.toString(), compSymbol.getFullName()));
    }
  }
}
