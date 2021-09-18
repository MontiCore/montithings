package montithings.util;

import arcbasis._ast.*;
import cd4montithings.CD4MontiThingsMill;
import cd4montithings._ast.ASTCDComponentInterface;
import cd4montithings._ast.ASTCDPort;
import cd4montithings._ast.ASTCDPortDeclaration;
import cd4montithings._ast.ASTCDPortDirection;
import cd4montithings._symboltable.CD4MontiThingsArtifactScope;
import cd4montithings._symboltable.ICD4MontiThingsArtifactScope;
import de.monticore.cdbasis._ast.ASTCDClass;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._ast.ASTCDDefinition;
import montiarc._ast.ASTMACompilationUnit;
import montithings._ast.ASTMTComponentType;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import montithings.types.check.MontiThingsTypeCheck;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;

import java.util.stream.Collectors;

/**
 * Helpers to create class diagrams in the scope for each atomic component
 */
public class ClassDiagramUtil {

  private static MontiThingsTypeCheck tc = new MontiThingsTypeCheck(
    new SynthesizeSymTypeFromMontiThings(), new DeriveSymTypeOfMontiThingsCombine());

  public static CD4MontiThingsArtifactScope createClassDiagram(ASTMACompilationUnit node){
    return createClassDiagram((ASTMTComponentType) node.getComponentType());
  }

  protected static CD4MontiThingsArtifactScope createClassDiagram(ASTMTComponentType comp) {
    ASTCDClass astcdClass = CD4MontiThingsMill.cDClassBuilder().setName(comp.getName()).
      setModifier(CD4MontiThingsMill.modifierBuilder().PUBLIC().build()).build();
    for (ASTPortDeclaration astPortDeclaration : comp.getPortDeclarations()) {
      ASTCDComponentInterface astcdComponentInterface =
        CD4MontiThingsMill.cDComponentInterfaceBuilder().build();
      ASTCDPortDirection portDirection;
      if (astPortDeclaration.getPortDirection() instanceof ASTPortDirectionIn) {
        portDirection = CD4MontiThingsMill.cDPortDirectionInBuilder().build();
      }
      else {
        portDirection = CD4MontiThingsMill.cDPortDirectionOutBuilder().build();
      }
      for (ASTPort astPort : astPortDeclaration.getPortList()){
        ASTCDPort port = CD4MontiThingsMill.cDPortBuilder().setName(astPort.getName()).build();
        port.setType(tc.symTypeFromAST(astPortDeclaration.getMCType()));
        ASTCDPortDeclaration portDeclaration = CD4MontiThingsMill.cDPortDeclarationBuilder().
          setCDPortDirection(portDirection).setMCType(astPortDeclaration.getMCType()).
          addCDPort(port).build();
        astcdComponentInterface.addCDPortDeclaration(portDeclaration);
      }
      astcdClass.addCDMember(astcdComponentInterface);
    }
    ASTCDDefinition astcdDefinition = CD4MontiThingsMill.cDDefinitionBuilder().
      setModifier(CD4MontiThingsMill.modifierBuilder().PUBLIC().build()).
      addCDElement(astcdClass).setName(comp.getName()).build();
    ASTCDCompilationUnit astcdCompilationUnit = CD4MontiThingsMill.cDCompilationUnitBuilder().
      setCDDefinition(astcdDefinition).build();
    CD4MontiThingsMill.globalScope();
    ICD4MontiThingsArtifactScope scope = CD4MontiThingsMill.scopesGenitorDelegator().createFromAST(astcdCompilationUnit);
    return (CD4MontiThingsArtifactScope) scope;
  }
}
