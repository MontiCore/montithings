// (c) https://github.com/MontiCore/monticore
package montithings.util;

import arcbasis._ast.ASTPort;
import arcbasis._ast.ASTPortDeclaration;
import arcbasis._ast.ASTPortDirectionIn;
import de.monticore.cd4code.CD4CodeMill;
import de.monticore.cd4code._symboltable.CD4CodeArtifactScope;
import de.monticore.cd4code._symboltable.ICD4CodeArtifactScope;
import de.monticore.cdbasis._ast.*;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeExpressionFactory;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCCustomTypeArgument;
import montiarc._ast.ASTMACompilationUnit;
import montithings._ast.ASTMTComponentType;
import montithings.types.check.DeriveSymTypeOfMontiThingsCombine;
import montithings.types.check.MontiThingsTypeCheck;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;

/**
 * Helpers to create class diagrams in the scope for each atomic component
 */
public class ClassDiagramUtil {

  private static MontiThingsTypeCheck tc = new MontiThingsTypeCheck(
          new SynthesizeSymTypeFromMontiThings(), new DeriveSymTypeOfMontiThingsCombine());

  public static CD4CodeArtifactScope createClassDiagram(ASTMACompilationUnit node) {
    return createClassDiagram((ASTMTComponentType) node.getComponentType());
  }

  protected static CD4CodeArtifactScope createClassDiagram(ASTMTComponentType comp) {
    ASTCDClass astcdClass = CD4CodeMill.cDClassBuilder().setName(comp.getName() + "Class").
            setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).build();
    for (ASTPortDeclaration astPortDeclaration : comp.getPortDeclarations()) {
      boolean incoming;
      if (astPortDeclaration.getPortDirection() instanceof ASTPortDirectionIn) {
        incoming = true;
      } else {
        incoming = false;
      }
      for (ASTPort astPort : astPortDeclaration.getPortList()) {
        ASTMCType astmcType;
        ASTMCCustomTypeArgument typeArgument = CD4CodeMill.mCCustomTypeArgumentBuilder().
                setMCType(astPortDeclaration.getMCType()).build();
        if (incoming) {
          astmcType = CD4CodeMill.mCBasicGenericTypeBuilder().addName( "InPort").
                  addMCTypeArgument(typeArgument).build();
        } else {
          astmcType = CD4CodeMill.mCBasicGenericTypeBuilder().addName( "OutPort").
                  addMCTypeArgument(typeArgument).build();
        }
        ASTCDAttribute attribute = CD4CodeMill.cDAttributeBuilder().setName(astPort.getName()).setInitialAbsent()
                .setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).setMCType(astmcType).build();
        astcdClass.addCDMember(attribute);
      }
    }
    ASTCDDefinition astcdDefinition = CD4CodeMill.cDDefinitionBuilder().
            setModifier(CD4CodeMill.modifierBuilder().PUBLIC().build()).
            addCDElement(astcdClass).setName(comp.getName()).build();
    ASTCDCompilationUnit astcdCompilationUnit = CD4CodeMill.cDCompilationUnitBuilder().
            setCDDefinition(astcdDefinition).build();
    ICD4CodeArtifactScope scope = CD4CodeMill.scopesGenitorDelegator().createFromAST(astcdCompilationUnit);
    for (ASTCDMember astcdAttribute : astcdClass.getCDMemberList()) {
      setSymType((ASTCDAttribute) astcdAttribute);
    }
    return (CD4CodeArtifactScope) scope;
  }

  private static void setSymType(ASTCDAttribute attribute) {
    SymTypeExpression symType;
    if (attribute.getMCType() instanceof ASTMCQualifiedType) {
      symType = SymTypeExpressionFactory.createTypeObject(((ASTMCQualifiedType) attribute.getMCType()).getMCQualifiedName().getQName(), attribute.getEnclosingScope());
    } else {
      symType = tc.symTypeFromAST(attribute.getMCType());
    }
    attribute.getSymbol().setType(symType);
  }
}
