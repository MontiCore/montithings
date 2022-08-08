// (c) https://github.com/MontiCore/monticore
package montithings.generator.cd2cpp;

import de.monticore.cdassociation._ast.*;
import de.monticore.cdbasis._ast.ASTCDCompilationUnit;
import de.monticore.cdbasis._symboltable.CDTypeSymbol;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Makes accessing associations easier. CD4A's version 6 really makes this unnecessarily hard...
 *
 * @since 21.12.20
 */
public class AssociationHelper {

  public static String getDerivedName(ASTCDAssociation association, CDTypeSymbol thisSide) {
    ASTCDAssocSide otherSide = getOtherSide(association, thisSide);
    if (otherSide.isPresentSymbol()) {
      // Other side has a role name
      return otherSide.getSymbol().getName();
    }
    else {
      // Other side has no role name - use type name with first letter lowercase
      String otherType = getOtherSideTypeName(association, thisSide);
      return StringHelper.toFirstLower(otherType);
    }
  }

  public static ASTCDAssocSide getOtherSide(ASTCDAssociation association, CDTypeSymbol thisSide) {
    String leftSide = association.getLeftQualifiedName().getQName();
    return leftSide.equals(thisSide.getName()) ? association.getRight() : association.getLeft();
  }

  public static ASTCDCardinality getOtherSideCardinality(ASTCDAssociation association, CDTypeSymbol thisSide) {
    return getOtherSide(association, thisSide).getCDCardinality();
  }

  public static String getOtherSideTypeName(ASTCDAssociation association, CDTypeSymbol thisSide) {
    String leftSide = association.getLeftQualifiedName().getQName();
    String rightSide = association.getRightQualifiedName().getQName();
    return leftSide.equals(thisSide.getName()) ? rightSide : leftSide;
  }

  public static Collection<ASTCDAssociation> getAssociations(ASTCDCompilationUnit compilationUnit,
    CDTypeSymbol symbol) {
    List<ASTCDAssociation> associations = compilationUnit.getCDDefinition().getCDAssociationsList();
    return filterAssociationsBySymbol(associations, symbol);
  }

  protected static List<ASTCDAssociation> filterAssociationsBySymbol(
    List<ASTCDAssociation> associations, CDTypeSymbol symbol) {

    List<ASTCDAssociation> result = new ArrayList<>();
    for (ASTCDAssociation association : associations) {
      String leftSide = association.getLeftQualifiedName().getQName();
      String rightSide = association.getRightQualifiedName().getQName();

      boolean leftContainsSymbol = leftSide.equals(symbol.getName());
      boolean rightContainsSymbol = rightSide.equals(symbol.getName());
      boolean leftIsVisible = association.getCDAssocDir() instanceof ASTCDRightToLeftDir;
      boolean bidirectional = association.getCDAssocDir() instanceof ASTCDBiDir;
      boolean rightIsVisible = association.getCDAssocDir() instanceof ASTCDLeftToRightDir;

      if ((leftContainsSymbol && (rightIsVisible || bidirectional)) ||
        (rightContainsSymbol && (leftIsVisible || bidirectional))) {
        result.add(association);
      }
    }
    return result;
  }
}
