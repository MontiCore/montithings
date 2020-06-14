package de.rwth.se.iotlab.facts._ast;

import de.monticore.lang.json._ast.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ASTDevices extends ASTDevicesTOP {

    private List<List<String>> getSublists(List<String> list) {
        List<List<String>> res = new ArrayList<>();

        for (int a = 0; a < list.size(); a++) {
            for (int b = a + 1; b <= list.size(); b++) {
                res.add(list.subList(a, b));
            }
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
