// (c) https://github.com/MontiCore/monticore
package montithings.services.prolog_generator;

import de.monticore.lang.json._ast.ASTJSONNumber;
import de.monticore.lang.json._ast.ASTJSONString;
import de.monticore.lang.json._ast.ASTJSONValue;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedNatLiteral;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static List<List<String>> getSublists(List<String> list) {
        List<List<String>> res = new ArrayList<>();

        // Iterate over all possible combinations by iterating in binary from 11...111 to 00..001
        String[] binaryMax = new String[list.size()];
        Arrays.fill(binaryMax, "1");
        int binaryMaxInt = Integer.parseInt(String.join("", binaryMax), 2);

        for (int i = binaryMaxInt; i >= 0; i--) {
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
        String nameWithoutRegistry = name.split("/")[name.split("/").length-1];
        String nameWithoutDockerTag = nameWithoutRegistry.split(":")[0];
        String nameAsPrologVariable = capitalize(nameWithoutDockerTag);
        nameAsPrologVariable = replaceNonAlphanumeric(nameAsPrologVariable);
        nameAsPrologVariable = nameAsPrologVariable.replaceAll("[^a-zA-Z0-9_]", "");
        return nameAsPrologVariable;
    }

    protected static String capitalize(String input) {
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    protected static String replaceNonAlphanumeric(String input) {
        Pattern p = Pattern.compile( "[^a-zA-Z0-9]([a-zA-Z0-9])" );
        Matcher m = p.matcher( input );
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(sb, m.group(1).toUpperCase());
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
