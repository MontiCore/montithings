package de.rwth.se.iotlab;

import de.monticore.lang.json._ast.ASTJSONNull;
import de.monticore.lang.json._ast.ASTJSONNumber;
import de.monticore.lang.json._ast.ASTJSONString;
import de.monticore.lang.json._ast.ASTJSONValue;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedNatLiteral;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utils {
    public static List<List<String>> getSublists(List<String> list) {
        List<List<String>> res = new ArrayList<>();

        // Iterate over all possible combinations by iterating in binary from 11...111 to 00..001
        String[] binaryMax = new String[list.size()];
        Arrays.fill(binaryMax, "1");
        int binaryMaxInt = Integer.parseInt(String.join("", binaryMax), 2);

        for (int i = binaryMaxInt; i > 0; i--) {
            List<String> combination = new ArrayList<>();
            char[] binaryArray = new char[binaryMax.length];
            Arrays.fill(binaryArray, '0');

            char[] iAsBinaryArray = Integer.toBinaryString(i).toCharArray();
            int lengthDiff = binaryArray.length - iAsBinaryArray.length;
            for (int k = 0; k < iAsBinaryArray.length; k++) {
                binaryArray[k + lengthDiff] = iAsBinaryArray[k];
            }

            for (int index = 0; index < binaryArray.length; index++) {
                if (binaryArray[index] == '1') {
                    combination.add((String) list.toArray()[index]);
                }
            }
            res.add(combination);

        }

        return res;
    }

    public static String astJsonValue2String(ASTJSONValue value) {
        if (value instanceof ASTJSONNumber) {
            ASTSignedLiteral literal = ((ASTJSONNumber) value).getSignedNumericLiteral();
            if (literal != null) {
                ASTSignedNatLiteral nat = (ASTSignedNatLiteral) literal;
                return String.valueOf(nat.getValue());
            }
            throw new IllegalArgumentException("Unexpected Could not convert number to int");
        } else {
            ASTJSONString astValue = (ASTJSONString) value;
            return astValue.getStringLiteral().getValue();
        }
    }

    public static String generatePrologCompliantName(String name) {
        return "D" + Math.abs(name.hashCode());
    }
}
