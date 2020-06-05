package de.rwth.se.iotlab.config._ast;

import de.monticore.lang.json._ast.*;

import java.util.HashMap;
import java.util.Map;

public class ASTConfig extends ASTConfigTOP {
    public Map<String, Map<String, String>> getProperties() {
        Map<String, Map<String, String>> result = new HashMap<>();
        ASTJSONObject config = (ASTJSONObject) this.getJSONDocument().getJSONValue();

        for (ASTJSONProperty configProp : config.getPropList()) {
            switch (configProp.getKey()) {
                case "distribution":
                    result.put(configProp.getKey(), getimageDistrConfigProps((ASTJSONObject) configProp.getValue()));
                    break;
                case "incompatibilities":
                    result.put(configProp.getKey(), getIncompatibilitiesProps((ASTJSONArray) configProp.getValue()));
                    break;
                case "dependencies":
                    //result.put(configProp.getKey(), getDependenciesProps((ASTJSONObject) configProp.getValue()));
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + configProp.getKey());
            }
        }

        return result;
    }

    private Map<String, String> getimageDistrConfigProps(ASTJSONObject config) {
        Map<String, String> result = new HashMap<>();

        for (ASTJSONProperty imageDistrConfig : config.getPropList()) {
            // eg. imageDistrConfig key = fire_detection:latest
            for (ASTJSONProperty distributionConfig : ((ASTJSONObject) imageDistrConfig.getValue()).getPropList()) {
                switch (distributionConfig.getKey()) {
                    case "distribution_selection":
                        result.put(configProp.getKey(), getimageDistrConfigProps((ASTJSONObject) configProp.getValue()));
                        break;
                    case "distribution_constraints":
                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + distributionConfig.getKey());
                }
            }

        }

        return result;
    }

    private Map<String, String> getIncompatibilitiesProps(ASTJSONArray config) {
        Map<String, String> result = new HashMap<>();

        for (ASTJSONValue imageDistrConfig : config.getJSONValueList()) {
            result.put("test", imageDistrConfig.toString());

        }

        return result;
    }

    private Map<String, String> getDependenciesProps(ASTJSONObject config) {
        Map<String, String> result = new HashMap<>();

        return result;
    }
}
