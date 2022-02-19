// (c) https://github.com/MontiCore/monticore
package montithings.trafos;

import arcbasis._ast.*;
import arcbasis._symboltable.PortSymbol;
import arcbasis._symboltable.PortSymbolTOP;
import behavior._ast.ASTConnectStatement;
import de.monticore.prettyprint.IndentPrinter;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.se_rwth.commons.logging.Log;
import genericarc._ast.ASTGenericComponentHead;
import montiarc._ast.ASTMACompilationUnit;
import montithings._ast.ASTBehavior;
import montithings._ast.ASTMTComponentType;
import montithings._visitor.MontiThingsFullPrettyPrinter;
import montithings._visitor.MontiThingsTraverser;

import java.util.*;
import java.util.stream.Collectors;

import static montithings.util.TrafoUtil.findParents;
import static montithings.util.TrafoUtil.getComponentByUnqualifiedName;

public class ComponentTypePortsNamingTrafo extends BasicTransformations implements MontiThingsTrafo, MontiThingsTraverser {
  protected static final String TOOL_NAME = "ComponentTypePortsNamingTrafo";
  protected ASTMACompilationUnit compilationUnit;
  protected Collection<ASTMACompilationUnit> allModels;
  protected Set<String> portsToIgnore;

  public static Set<ASTMACompilationUnit> getChangedCompilationUnits() {
    return changedCompilationUnits;
  }

  protected static Set<ASTMACompilationUnit> changedCompilationUnits = new HashSet<>();

  public boolean isChanged() {
    return changed;
  }

  public void setChanged(boolean changed) {
    this.changed = changed;
  }

  protected boolean changed;

  public ComponentTypePortsNamingTrafo(Set<PortSymbol> portsToIgnore) {
    this.portsToIgnore = portsToIgnore.stream().map(PortSymbolTOP::getName).collect(Collectors.toSet());

  }

  public Collection<ASTMACompilationUnit> transform(Collection<ASTMACompilationUnit> originalModels, Collection<ASTMACompilationUnit> addedModels, ASTMACompilationUnit targetComp) {
    Log.info("Apply transformation: Component Type Port Names: " + targetComp.getComponentType().getName(), TOOL_NAME);
    compilationUnit = targetComp;
    allModels = originalModels;
    targetComp.accept(this);
    return originalModels;
  }

  @Override
  public void visit(ASTMTComponentType comp) {
    //skip components with type parameters
    if (comp.getHead() instanceof ASTGenericComponentHead) {
      return;
    }
    for (Map.Entry<String, ASTMCType> port : getUnconnectedPorts(comp)) {
      String newPortName;
      if (findParents(allModels, compilationUnit).isEmpty()) {
        newPortName = comp.getName().toLowerCase() + "_" + port.getKey().replaceAll("\\.", "_");
      } else {
        newPortName = port.getKey().replaceAll("\\.", "_");
      }
      try {
        addPort(compilationUnit, newPortName, false, port.getValue());
        changedCompilationUnits.add(compilationUnit);
        changed = true;
      } catch (Exception e) {
        Log.error(e.getCause().getMessage());
        e.printStackTrace();
      }
      addConnection(comp, newPortName, port.getKey());
    }
  }

  private Set<Map.Entry<String, ASTMCType>> getUnconnectedPorts(ASTMTComponentType comp) {
    Set<Map.Entry<String, ASTMCType>> portNames = new HashSet<>();
    Collection<String> targets = this.getTargetNames(comp);
    addConnectorTargetsFromBehavior(targets, comp);
    for (ASTComponentInstantiation componentInstantiation : comp.getSubComponentInstantiations()) {
      String componentInstanceTypeName =
        new MontiThingsFullPrettyPrinter(new IndentPrinter()).prettyprint(componentInstantiation.getMCType());

      //remove generic type arguments from name
      componentInstanceTypeName = componentInstanceTypeName.replaceAll("<.*>", "");

      for (ASTComponentInstance componentInstance : componentInstantiation.getComponentInstanceList()) {
        //get all ports of subcomponents
        String componentInstanceName = componentInstance.getName();
        ASTMACompilationUnit compilationUnit = getComponentByUnqualifiedName(allModels, componentInstanceTypeName);
        Collection<ASTPortDeclaration> portDeclarations = compilationUnit.getComponentType().getPortDeclarations();
        Map<String, ASTMCType> subInputPorts = new HashMap<>();
        Map<String, ASTMCType> subOutputPorts = new HashMap<>();
        for (ASTPortDeclaration portDeclaration : portDeclarations) {
          if (portDeclaration.getPortDirection() instanceof ASTPortDirectionIn) {
            for (ASTPort port : portDeclaration.getPortList()) {
              subInputPorts.put(componentInstanceName + "." + port.getName(), portDeclaration.getMCType());
            }
          } else if (portDeclaration.getPortDirection() instanceof ASTPortDirectionOut) {
            for (ASTPort port : portDeclaration.getPortList()) {
              subOutputPorts.put(componentInstanceName + "." + port.getName(), portDeclaration.getMCType());
            }
          }
        }

        //remove all ports which appear in connectors
        for (String target : targets) {
          subInputPorts.remove(target);
        }

        for (Map.Entry<String, ASTMCType> port : subInputPorts.entrySet()) {
          if (!portsToIgnore.contains(port.getKey())) {
            portNames.add(port);
          }
        }
      }
    }
    return portNames;
  }

  protected void addConnectorTargetsFromBehavior(Collection<String> sources, ASTMTComponentType node) {
    List<ASTBehavior> behaviors = node.getBody().getArcElementList().stream().filter(element -> element instanceof ASTBehavior).map(behavior -> (ASTBehavior) behavior).collect(Collectors.toList());
    for (ASTBehavior behavior : behaviors) {
      List<ASTConnectStatement> connectStatements = behavior.getMCJavaBlock().getMCBlockStatementList().stream().filter(element -> element instanceof ASTConnectStatement).map(connector -> ((ASTConnectStatement) connector)).collect(Collectors.toList());
      for (ASTConnectStatement connectStatement : connectStatements) {
        sources.addAll(connectStatement.getConnector().getTargetsNames());
      }
    }
  }

  protected Collection<String> getTargetNames(ASTComponentType node) {
    return node.getConnectors().stream().map(ASTConnector::getTargetsNames).flatMap(Collection::stream).collect(Collectors.toList());
  }
}
