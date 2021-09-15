package cd4montithings._symboltable;

import cd4montithings.CD4MontiThingsMill;
import cd4montithings._visitor.CD4MontiThingsTraverser;
import de.monticore.cd._symboltable.CDSymbolTableHelper;
import de.monticore.cd.facade.MCQualifiedNameFacade;

import de.monticore.cd4code.typescalculator.DeriveSymTypeOfCD4Code;
import de.monticore.cd4codebasis._symboltable.CD4CodeBasisSymbolTableCompleter;
import de.monticore.cdassociation._symboltable.CDAssociationSymbolTableCompleter;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDBasisSymbolTableCompleter;
import de.monticore.cdinterfaceandenum._symboltable.CDInterfaceAndEnumSymbolTableCompleter;
import de.monticore.types.mcbasictypes._ast.ASTMCImportStatement;
import de.monticore.types.mcbasictypes._ast.ASTMCQualifiedName;

import java.util.List;

public class CD4MontiThingsSymbolTableCompleter {
  protected CD4MontiThingsTraverser traverser;
  protected CDSymbolTableHelper symbolTableHelper;

  public CD4MontiThingsSymbolTableCompleter(ASTCDCompilationUnit ast) {
    this(ast.getMCImportStatementList(),
        ast.isPresentMCPackageDeclaration() ?
            ast.getMCPackageDeclaration().getMCQualifiedName() :
            MCQualifiedNameFacade.createQualifiedName(""));
  }

  public CD4MontiThingsSymbolTableCompleter(List<ASTMCImportStatement> imports, ASTMCQualifiedName packageDeclaration) {
    this.symbolTableHelper = new CDSymbolTableHelper(new DeriveSymTypeOfCD4Code())
        .setImports(imports)
        .setPackageDeclaration(packageDeclaration);
    ((CD4MontiThingsGlobalScope) CD4MontiThingsMill.globalScope()).setSymbolTableHelper(symbolTableHelper);
    this.traverser = CD4MontiThingsMill.traverser();

    final CDBasisSymbolTableCompleter cDBasisVisitor = new CDBasisSymbolTableCompleter(symbolTableHelper);
    traverser.add4CDBasis(cDBasisVisitor);
    traverser.add4OOSymbols(cDBasisVisitor);
    final CDAssociationSymbolTableCompleter cDAssociationVisitor = new CDAssociationSymbolTableCompleter(symbolTableHelper);
    traverser.add4CDAssociation(cDAssociationVisitor);
    traverser.setCDAssociationHandler(cDAssociationVisitor);
    final CDInterfaceAndEnumSymbolTableCompleter cdInterfaceAndEnumVisitor = new CDInterfaceAndEnumSymbolTableCompleter(symbolTableHelper);
    traverser.add4CDInterfaceAndEnum(cdInterfaceAndEnumVisitor);
    final CD4CodeBasisSymbolTableCompleter cd4CodeBasisVisitor = new CD4CodeBasisSymbolTableCompleter(symbolTableHelper);
    traverser.add4CD4CodeBasis(cd4CodeBasisVisitor);
  }

  public CD4MontiThingsTraverser getTraverser() {
    return traverser;
  }
}
