/* (c) https://github.com/MontiCore/monticore */
package montithings.generator.visitor;

import de.monticore.prettyprint.IndentPrinter;
import de.monticore.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.siunitliterals._ast.ASTSignedSIUnitLiteral;
import de.monticore.siunitliterals.prettyprint.SIUnitLiteralsPrettyPrinter;
import de.monticore.siunitliterals.utility.SIUnitLiteralDecoder;
import de.monticore.siunits.utility.Converter;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfNumericWithSIUnit;

import javax.measure.converter.UnitConverter;

public class MySIUnitLiteralsPrettyPrinter extends SIUnitLiteralsPrettyPrinter {

  public MySIUnitLiteralsPrettyPrinter(IndentPrinter printer) {
    super(printer);
  }

  @Override
  public void traverse(ASTSIUnitLiteral node) {
    printer.print(SIUnitLiteralDecoder.doubleOf(node));
  }

  @Override
  public void traverse(ASTSignedSIUnitLiteral node) {
    printer.print(SIUnitLiteralDecoder.doubleOf(node));
  }

  public static String factorStart(UnitConverter converter) {
    if (converter != UnitConverter.IDENTITY && converter.convert(1) != 1.0)
      return "((";
    else return "";
  }

  public static String factorStartSimple(UnitConverter converter) {
    if (converter != UnitConverter.IDENTITY && converter.convert(1) != 1.0)
      return "(";
    else return "";
  }

  public static String factorEnd(UnitConverter converter) {
    if (converter != UnitConverter.IDENTITY && converter.convert(1) != 1.0) {
      String factor;
      if (converter.convert(1) > 1)
        factor = " * " + converter.convert(1);
      else
        factor = " / " + converter.inverse().convert(1);
      return ")" + factor + ")";
    } else
      return "";
  }

  public static String factorEndSimple(UnitConverter converter) {
    if (converter != UnitConverter.IDENTITY && converter.convert(1) != 1.0) {
      String factor;
      if (converter.convert(1) > 1)
        factor = " * " + converter.convert(1);
      else
        factor = " / " + converter.inverse().convert(1);
      return ")" + factor;
    } else
      return "";
  }

  public static UnitConverter getSIConverter(SymTypeExpression baseType, SymTypeExpression typeToConvert){
    if(baseType instanceof SymTypeOfNumericWithSIUnit && typeToConvert instanceof SymTypeOfNumericWithSIUnit){
      return Converter.getConverter(((SymTypeOfNumericWithSIUnit) typeToConvert).getUnit(),
              ((SymTypeOfNumericWithSIUnit) baseType).getUnit());
    }
    return UnitConverter.IDENTITY;
  }
}