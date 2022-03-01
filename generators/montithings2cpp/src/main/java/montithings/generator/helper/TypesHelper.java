// (c) https://github.com/MontiCore/monticore
package montithings.generator.helper;

import arcbasis._ast.ASTComponentInstantiation;
import arcbasis._ast.ASTComponentType;
import arcbasis._ast.ASTPortAccess;
import arcbasis._symboltable.ComponentInstanceSymbol;
import arcbasis._symboltable.ComponentTypeSymbol;
import arcbasis._symboltable.PortSymbol;
import cdlangextension._ast.ASTCDEImportStatement;
import cdlangextension._symboltable.CDEImportStatementSymbol;
import cdlangextension._symboltable.ICDLangExtensionScope;
import de.monticore.ast.ASTNode;
import de.monticore.expressions.commonexpressions._ast.ASTFieldAccessExpression;
import de.monticore.siunits._ast.ASTSIUnit;
import de.monticore.siunits.utility.Converter;
import de.monticore.siunits.utility.UnitFactory;
import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.symbols.oosymbols._symboltable.FieldSymbol;
import de.monticore.symbols.oosymbols._symboltable.OOTypeSymbol;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;
import de.monticore.types.mccollectiontypes._ast.ASTMCTypeArgument;
import de.monticore.types.mcsimplegenerictypes._ast.ASTMCBasicGenericType;
import de.se_rwth.commons.logging.Log;
import montithings._symboltable.IMontiThingsScope;
import montithings._visitor.MontiThingsFullPrettyPrinter;
import montithings.generator.codegen.util.Utils;
import montithings.generator.config.ConfigParams;
import montithings.util.ClassDiagramUtil;

import javax.measure.unit.Unit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static montithings.generator.helper.ComponentHelper.getPortSymbolFromPortAccess;

public class TypesHelper {
  /**
   * @return Corresponding CPP types from input java types
   */
  public static String java2cppTypeString(String type) {
    return java2cppTypeString(type, false);
  }

  public static String java2cppTypeString(String type, boolean preventRecursion) {
    String replacedArray = type.replaceAll("([^<]*)\\[]", "std::vector<$1>");
    while (!type.equals(replacedArray)) {
      type = replacedArray;
      replacedArray = type.replaceAll("([^<]*)\\[]", "std::vector<$1>");
    }
    type = type.replaceAll("(\\W|^)String(\\W|$)", "$1std::string$2");
    type = type.replaceAll("(\\W|^)Integer(\\W|$)", "$1int$2");
    type = type.replaceAll("(\\W|^)Map(\\W|$)", "$1std::map$2");
    type = type.replaceAll("(\\W|^)Set(\\W|$)", "$1std::set$2");
    type = type.replaceAll("(\\W|^)List(\\W|$)", "$1std::list$2");
    type = type.replaceAll("(\\W|^)Boolean(\\W|$)", "$1bool$2");
    type = type.replaceAll("(\\W|^)boolean(\\W|$)", "$1bool$2");
    type = type.replaceAll("(\\W|^)Character(\\W|$)", "$1char$2");
    type = type.replaceAll("(\\W|^)Double(\\W|$)", "$1double$2");
    type = type.replaceAll("(\\W|^)Float(\\W|$)", "$1float$2");

    if (preventRecursion) {
      return type;
    }

    while (!java2cppTypeString(type, true).equals(type)) {
      type = java2cppTypeString(type);
    }
    return type;
  }

  public static boolean isJavaType(String type) {
    return type.startsWith("String") ||
      type.startsWith("Map") ||
      type.startsWith("Set") ||
      type.startsWith("List") ||
      type.startsWith("Integer") ||
      type.startsWith("int") ||
      type.startsWith("Boolean") ||
      type.startsWith("boolean") ||
      type.startsWith("Character") ||
      type.startsWith("char") ||
      type.startsWith("Double") ||
      type.startsWith("double") ||
      type.startsWith("Float") ||
      type.startsWith("float")
      ;
  }

  /**
   * Gets the type arguments of a component as a comma-separated list
   */
  public static String getTypeArguments(ComponentInstanceSymbol instance) {
    final ComponentTypeSymbol component = instance.getType();

    if (Utils.hasTypeParameter(component)) {
      ASTComponentInstantiation instantiation = getInstantiation(instance);
      if (instantiation.getMCType() instanceof ASTMCBasicGenericType) {
        List<ASTMCTypeArgument> types = new ArrayList<>(
          ((ASTMCBasicGenericType) instantiation.getMCType()).getMCTypeArgumentList());
        return types.stream()
          .map(TypesPrinter::printTypeArgumentIterate)
          .collect(Collectors.joining(", "));
      }
    }

    return "";
  }

  /**
   * True if the given port uses a type from a class diagram (== OOTypeSymbol)
   * @param portSymbol the port to check
   * @return True if the given port uses a type from a class diagram, false otherwise
   */
  public static boolean portUsesCdType(PortSymbol portSymbol) {
    return portSymbol.getTypeInfo() instanceof OOTypeSymbol;
  }

  //============================================================================
  // region SI Units
  //============================================================================

  public static double getConversionFactorFromSourceAndTarget(ASTPortAccess source,
    ASTPortAccess target) {
    Optional<PortSymbol> pss = getPortSymbolFromPortAccess(source);
    if (pss.isPresent() && pss.get().getType() instanceof SymTypeOfNumericWithSIUnit) {
      Optional<PortSymbol> pst = getPortSymbolFromPortAccess(target);
      if (pst.isPresent()) {
        return getConversionFactor(((SymTypeOfNumericWithSIUnit) pss.get().getType()).getUnit(),
          ((SymTypeOfNumericWithSIUnit) pst.get().getType()).getUnit());
      }
    }
    return 1;
  }

  public static boolean isSIUnitPort(ASTPortAccess portAccess) {
    Optional<PortSymbol> ps = getPortSymbolFromPortAccess(portAccess);
    return ps.filter(TypesHelper::isSIUnitPort).isPresent();
  }

  public static boolean isSIUnitPort(PortSymbol portSymbol) {
    return portSymbol.getType() instanceof SymTypeOfNumericWithSIUnit;
  }

  public static ASTComponentInstantiation getInstantiation(ComponentInstanceSymbol instance) {
    ASTNode node = instance.getEnclosingScope().getSpanningSymbol().getAstNode();
    if (!(node instanceof ASTComponentType)) {
      Log.error("0xMT0792 instance is not spanned by ASTComponentType.");
      System.exit(-1); // unreachable, but silences static analyzer
    }
    Optional<ASTComponentInstantiation> result = ((ASTComponentType) node)
      .getSubComponentInstantiations()
      .stream().filter(i -> i.getComponentInstanceList().contains(instance.getAstNode()))
      .findFirst();
    if (!result.isPresent()) {
      Log.error("0xMT0791 instance not found.");
      System.exit(-1); // unreachable, but silences static analyzer
    }
    return result.get();
  }

  public static List<String> getSIUnitPortNames(ComponentTypeSymbol comp) {
    List<String> names = new ArrayList<>();
    for (PortSymbol ps : comp.getAllIncomingPorts()) {
      if (ps.getType() instanceof SymTypeOfNumericWithSIUnit) {
        names.add(ps.getName());
      }
    }
    return names;
  }

  /**
   * Get the factor by which source needs to be multiplied to be converted
   * to target
   */
  public static double getConversionFactor(ASTSIUnit source, ASTSIUnit target){
    Unit sourceUnit = UnitFactory.createUnit(source);
    Unit targetUnit = UnitFactory.createUnit(target);
    return getConversionFactor(sourceUnit, targetUnit);
  }

  /**
   * Get the factor by which source needs to be multiplied to be converted
   * to target
   */
  public static double getConversionFactor(Unit sourceUnit, Unit targetUnit){
    return Converter.convert(1, sourceUnit, targetUnit);
  }

  // endregion
  //============================================================================
  // region CD Lang Extension
  //============================================================================

  /**
   * Gets the c++ import statement for a given port type if available.
   *
   * @param typeSymbol type symbol that may be replaced in CDE file
   * @param config     config containing a cdlangextension, that is used to search for import statements.
   * @return c++ import statement of the port type if specified in the cde model. Otherwise empty.
   */
  public static Optional<ASTCDEImportStatement> getCDEReplacement(TypeSymbol typeSymbol,
    ConfigParams config) {
    ICDLangExtensionScope scope = config.getCdLangExtensionScope();

    if (scope != null && typeSymbol instanceof OOTypeSymbol) {
      Optional<CDEImportStatementSymbol> cdeImportStatementSymbol = scope
        .resolveASTCDEImportStatement("Cpp", (OOTypeSymbol) typeSymbol);
      if (cdeImportStatementSymbol.isPresent() && cdeImportStatementSymbol.get()
        .isPresentAstNode()) {
        return Optional.of(cdeImportStatementSymbol.get().getAstNode());
      }
    }
    return Optional.empty();
  }

  /**
   * Gets the c++ import statement for a given port type if available.
   *
   * @param portSymbol port using a class diagram type.
   * @param config     config containing a cdlangextension, that is used to search for import statements.
   * @return c++ import statement of the port type if specified in the cde model. Otherwise empty.
   */
  public static Optional<ASTCDEImportStatement> getCDEReplacement(PortSymbol portSymbol,
    ConfigParams config) {
    if (!portUsesCdType(portSymbol)) {
      return Optional.empty();
    }
    TypeSymbol typeSymbol = portSymbol.getTypeInfo();
    return getCDEReplacement(typeSymbol, config);
  }

  public static boolean fieldAccessIsEnumConstant(ASTFieldAccessExpression node) {
    Optional<FieldSymbol> enumConstant = getFieldSymbolOfEnumConstant(node);
    return enumConstant.isPresent();
  }

  public static Optional<FieldSymbol> getFieldSymbolOfEnumConstant(ASTFieldAccessExpression node) {
    IMontiThingsScope enclosingScope = ((IMontiThingsScope)node.getEnclosingScope());
    MontiThingsFullPrettyPrinter pp = new MontiThingsFullPrettyPrinter();
    Optional<TypeSymbol> type = enclosingScope.resolveType(pp.prettyprint(node.getExpression()));
    if (type.isPresent()) {
      IMontiThingsScope enumScope = ((IMontiThingsScope) type.get().getSpannedScope());
      return enumScope.resolveField(node.getName());
    }
    return Optional.empty();
  }

  public static String getComponentTypePrefix(){
    return ClassDiagramUtil.COMPONENT_TYPE_PREFIX;
  }

}
