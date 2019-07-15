/**
 *
 *  ******************************************************************************
 *  MontiCAR Modeling Family, www.se-rwth.de
 *  Copyright (c) 2017, Software Engineering Group at RWTH Aachen,
 *  All rights reserved.
 *
 *  This project is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 3.0 of the License, or (at your option) any later version.
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this project. If not, see <http://www.gnu.org/licenses/>.
 * *******************************************************************************
 */
package de.monticore.lang.tagging.helper;

import javax.measure.quantity.Quantity;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexStringHelper {

    public static String getMatcher(String input) {
        input = input.replaceFirst("\\{\\s*", "");
        input = input.replaceFirst("\\s*}$", "");
        input = input.replaceFirst("^\\s+", "");

        Matcher m = Pattern.compile("\\$\\{(\\w+):(\\w+)\\}").matcher(input);
        while (m.find()) {
            input = input.replace(m.group(0), handleVarGroup(m.group(2)));
        }

        return input;
    }

    private static String handleVarGroup(String type) {
        String ret = "";

        if (type.equals("String")) {
            ret += "(\\\\w+";
        } else {
            ret += "(\\\\d+(?:\\\\.\\\\d+)?(?:n|m|c|d|h|k|M|G|T)?";
            ret += getUnit(type);
            ret += ")";
        }

        ret += "\\\\s*";
        return ret;
    }

    private static String getUnit(String type) {
        switch (type) {
            case "Length":
                return "m";
            case "Mass":
                return "g";
            case "Duration":
                return "s";
            case "ElectricCurrent":
                return "A";
            case "Temperature":
                return "(?:K|C|F)";
            default:
                return null;
        }
    }
}
