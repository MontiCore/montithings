package de.rwth.se.iotlab.facts._ast;

import de.monticore.lang.json._ast.*;
import sun.nio.cs.Surrogate;

import java.util.*;

public class ASTDevices extends ASTDevicesTOP {

    private List<List<String>> getSublists(List<String> list) {
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
                binaryArray[k+lengthDiff] = iAsBinaryArray[k];
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

    /**
     * Returns a map of all device properties specified for all devices in the AST
     *
     * @return A map with keys device names, containing maps with properties for each device
     */
    public Map<String, Map<String, List<String>>> getProperties() {
        Map<String, Map<String, List<String>>> result = new HashMap<>();
        ASTJSONObject root = (ASTJSONObject) this.getJSONDocument().getJSONValue();
        ASTJSONObject devices = (ASTJSONObject) root.getProp(0).getValue();

        for (ASTJSONProperty device : devices.getPropList()) {

            result.put(device.getKey(), getDeviceProps((ASTJSONObject) device.getValue()));
        }

        return result;
    }

    private Map<String, List<String>> getDeviceProps(ASTJSONObject device) {
        Map<String, List<String>> result = new HashMap<>();

        for (ASTJSONProperty prop : device.getPropList()) {
            if (prop.getKey().equals("hardware")) {
                ASTJSONArray value = (ASTJSONArray) prop.getValue();

                List<String> hardwareValues = new ArrayList<>();
                for (ASTJSONValue arrayValue : value.getJSONValueList()) {
                    ASTJSONString stringValue = (ASTJSONString) arrayValue;
                    hardwareValues.add(stringValue.getStringLiteral().getValue());
                }
                result.put("has_hardware", hardwareValues);


            } else if (prop.getKey().equals("location")) {

                ASTJSONObject locationProperties = (ASTJSONObject) prop.getValue();

                List<String> locationKeys = new ArrayList<>();
                for (ASTJSONProperty arrayValue : locationProperties.getPropList()) {
                    ASTJSONString stringValue = (ASTJSONString) arrayValue.getValue();
                    String value = stringValue.getStringLiteral().getValue();

                    List<String> locationValues = new ArrayList<>();
                    locationValues.add(value);
                    result.put("location_" + arrayValue.getKey(), locationValues);
                    locationKeys.add(arrayValue.getKey() + value);
                }

                // Add additional properties like property(location,building1_floor1_room101,device)
                List<String> locationValues = new ArrayList<>();

                for (List<String> sublistOfKeys : this.getSublists(locationKeys)) {
                    String joinedValue = String.join("_", sublistOfKeys);
                    locationValues.add(joinedValue);
                }
                result.put("location", locationValues);


            } else {
                ASTJSONString value = (ASTJSONString) prop.getValue();
                List<String> values = new ArrayList<>();
                values.add(value.getStringLiteral().getValue());
                result.put(prop.getKey(), values);
            }

        }

        return result;
    }

}
