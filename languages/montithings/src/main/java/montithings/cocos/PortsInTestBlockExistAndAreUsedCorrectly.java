package montithings.cocos;

import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import com.google.common.collect.LinkedListMultimap;
import componenttest._ast.ASTExpectValueOnPort;
import componenttest._ast.ASTSendValueOnPort;
import componenttest._ast.ASTTestBlock;
import componenttest._cocos.ComponentTestASTTestBlockCoCo;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symboltable.IScopeSpanningSymbol;
import de.se_rwth.commons.logging.Log;
import montithings._symboltable.MontiThingsScope;
import montithings.util.MontiThingsError;

import java.util.ArrayList;
import java.util.List;

import static montithings.util.PortUtil.findPortSymbolOfTestBlock;

public class PortsInTestBlockExistAndAreUsedCorrectly implements ComponentTestASTTestBlockCoCo {
  @Override
  public void check(ASTTestBlock node) {
    PortSymbol port = findPortSymbolOfTestBlock(node);
    List<String> incomingPortNames = new ArrayList<>();
    List<String> outgoingPortNames = new ArrayList<>();
    if (port != null) {
      TypeSymbol type = port.getTypeInfo();
      if (type.getSpannedScope() instanceof MontiThingsScope) {
        LinkedListMultimap<String, FieldSymbol> fieldSymbols = ((MontiThingsScope) type.getSpannedScope()).getFieldSymbols();
        for (String name : fieldSymbols.keySet()) {
          // check whether port is incoming or outgoing
          String portType = fieldSymbols.get(name).get(0).getType().print();
          if (portType.contains(".InPort<")) {
            incomingPortNames.add(name);
          } else if (portType.contains(".OutPort<")) {
            outgoingPortNames.add(name);
          }
        }
      }
    }

    // check that all ports used in test blocks exist
    for (ASTSendValueOnPort sendValueOnPort : node.getSendValueOnPortList()) {
      if (!incomingPortNames.contains(sendValueOnPort.getName())) {
        Log.error(String.format(MontiThingsError.PORTS_IN_TEST_BLOCK_DO_NOT_EXIST.toString()));
      }
    }
    for (ASTExpectValueOnPort expectValueOnPort : node.getExpectValueOnPortList()) {
      if (!outgoingPortNames.contains(expectValueOnPort.getName())) {
        Log.error(String.format(MontiThingsError.PORTS_IN_TEST_BLOCK_DO_NOT_EXIST.toString()));
      }
    }
  }
}
