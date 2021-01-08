package montithings.cocos;

import arcbasis._ast.ASTArcField;
import arcbasis._ast.ASTArcParameter;
import de.monticore.types.check.TypeCheck;
import de.monticore.types.check.cocos.TypeCheckCoCo;
import montiarc._ast.ASTMACompilationUnit;
import montiarc._cocos.MontiArcASTMACompilationUnitCoCo;
import montithings._ast.ASTMTComponentType;
import montithings._cocos.MontiThingsASTMTComponentTypeCoCo;
import montithings._visitor.MontiThingsVisitor;
import montithings.types.check.DeriveSymTypeOfMontiThings;
import montithings.types.check.SynthesizeSymTypeFromMontiThings;

public class SITypesTypeCheckCoCo extends TypeCheckCoCo implements MontiThingsASTMTComponentTypeCoCo, MontiThingsVisitor {
  /**
   * Creates an instance of TypeCheckCoCo
   *
   * @param typeCheck a {@link TypeCheck} object instantiated with the correct
   *                  ISynthesize and ITypesCalculator objects of
   *                  the current language
   */
  public SITypesTypeCheckCoCo(TypeCheck typeCheck) {
    super(typeCheck);
  }

  public static SITypesTypeCheckCoCo getCoCo() {
    TypeCheck typeCheck = new TypeCheck(new SynthesizeSymTypeFromMontiThings(), new DeriveSymTypeOfMontiThings());
    return new SITypesTypeCheckCoCo(typeCheck);
  }

  @Override
  public void check(ASTMTComponentType ast) {
    ast.accept(this);
  }

  @Override
  public void visit(ASTArcParameter node){
    if(node.isPresentDefault()){
      checkFieldOrVariable(node, node.getDefault());
    }
  }

  @Override
  public void visit(ASTArcField node){
    checkFieldOrVariable(node, node.getInitial());
  }

  //TODO: PortDeclaration
}
