package de.monticore.lang.sd4componenttesting.util;

import de.monticore.siunitliterals._ast.ASTSIUnitLiteral;
import de.monticore.siunitliterals.utility.SIUnitLiteralDecoder;
import de.monticore.siunits.prettyprint.SIUnitsPrettyPrinter;

public class ComponentHelper {
    public String printTime(ASTSIUnitLiteral lit) {
        String time = "milliseconds";
        if (SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit()).equals("ns")) {
            time = "nanoseconds";
        } else if (SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit()).equals("μs")) {
            time = "microseconds";
        } else if (SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit()).equals("ms")) {
            time = "milliseconds";
        } else if (SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit()).equals("s")) {
            time = "seconds";
        } else if (SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit()).equals("min")) {
            time = "minutes";
        } else if (SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit()).equals("h")) {
            time = "hours";
        }
        SIUnitLiteralDecoder decoder = new SIUnitLiteralDecoder();
        double value = decoder.getDouble(lit);
        time += "(" + (int) value + ")";
        return time;
    }

    public static int getTimeInMillis(ASTSIUnitLiteral lit) {
        SIUnitLiteralDecoder decoder = new SIUnitLiteralDecoder();
        double value = decoder.getValue(lit);
        switch (SIUnitsPrettyPrinter.prettyprint(lit.getSIUnit())) {
            case ("ns"):
                return (int) (value / 1000);
            case ("μs"):
                return (int) (value);
            case ("ms"):
                return (int) (value * 1000);
            case ("s"):
                return (int) (value * 1000 * 1000);
            case ("min"):
                return (int) (value * 1000 * 1000 * 60);
            case ("h"):
                return (int) (value * 1000 * 1000 * 60 * 60);
        }
        return 0;
    }
}
