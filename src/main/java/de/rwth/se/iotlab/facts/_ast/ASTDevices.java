package de.rwth.se.iotlab.facts._ast;

import de.monticore.lang.json._ast.*;

import java.util.HashMap;
import java.util.Map;

public class ASTDevices extends ASTDevicesTOP {

    /**
     * Returns a map of all device properties specified for all devices in the AST
     * @return A map with keys device names, containing maps with properties for each device
     */
    public Map<String, Map<String, String>> getProperties() {
        Map<String, Map<String, String>> result = new HashMap<>();
        ASTJSONObject root = (ASTJSONObject) this.getJSONDocument().getJSONValue();
        ASTJSONObject devices = (ASTJSONObject) root.getProp(0).getValue();

        for (ASTJSONProperty device : devices.getPropList()) {

            result.put(device.getKey(), getDeviceProps((ASTJSONObject) device.getValue()));
        }

        return result;
    }

    private Map<String, String> getDeviceProps(ASTJSONObject device) {
        Map<String, String> result = new HashMap<>();

        for (ASTJSONProperty prop : device.getPropList()) {
            if (prop.getKey().equals("hardware")) {
                ASTJSONArray value = (ASTJSONArray) prop.getValue();

                for (ASTJSONValue arrayValue : value.getJSONValueList()) {
                    ASTJSONString stringValue = (ASTJSONString) arrayValue;
                    result.put("has_hardware", stringValue.getStringLiteral().getValue());
                }


            } else if (prop.getKey().equals("location")) {

                ASTJSONObject value = (ASTJSONObject) prop.getValue();

                for (ASTJSONProperty arrayValue : value.getPropList()) {
                    ASTJSONString stringValue = (ASTJSONString) arrayValue.getValue();
                    result.put("location_"+prop.getKey(), stringValue.getStringLiteral().getValue());
                }

            } else {
                ASTJSONString value = (ASTJSONString) prop.getValue();
                result.put(prop.getKey(), value.getStringLiteral().getValue());
            }

        }

        return result;
    }

}
