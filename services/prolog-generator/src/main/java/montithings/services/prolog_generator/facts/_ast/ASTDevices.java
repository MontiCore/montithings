// (c) https://github.com/MontiCore/monticore
package montithings.services.prolog_generator.facts._ast;

import de.monticore.lang.json._ast.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static montithings.services.prolog_generator.Utils.astJsonValue2String;
import static montithings.services.prolog_generator.Utils.getSublists;

public class ASTDevices extends ASTDevicesTOP {
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
                    String value = astJsonValue2String(arrayValue.getValue());

                    List<String> locationValues = new ArrayList<>();
                    locationValues.add(value);
                    result.put("location_" + arrayValue.getKey(), locationValues);
                    locationKeys.add(arrayValue.getKey() + value);
                }

                // Add additional properties like property(location,building1_floor1_room101,device)
                List<String> locationValues = new ArrayList<>();

                for (List<String> sublistOfKeys : getSublists(locationKeys)) {
                    String joinedValue = String.join("_", sublistOfKeys);
                    locationValues.add(joinedValue);
                }
                result.put("location", locationValues);


            } else {
                String value = "";
                if (prop.getValue() instanceof ASTJSONNumber) {
                    value = String.valueOf(astJsonValue2String(prop.getValue()));
                } else if (prop.getValue() instanceof ASTJSONNull) {
                    continue;
                } else if(prop.getValue() instanceof ASTJSONString) {
                    ASTJSONString astValue = (ASTJSONString) prop.getValue();
                    value = astValue.getStringLiteral().getValue();
                } else {
                    continue;
                }
                List<String> values = new ArrayList<>();
                values.add(value);
                result.put(prop.getKey(), values);
            }

        }

        return result;
    }

}
