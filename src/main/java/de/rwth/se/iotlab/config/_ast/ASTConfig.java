package de.rwth.se.iotlab.config._ast;

import de.monticore.lang.json._ast.*;

import javax.json.JsonValue;
import java.util.*;

public class ASTConfig extends ASTConfigTOP {

    private ArrayList<Map<String, String>> getAllPairsOfListItems(ArrayList<String> list) {
        ArrayList<Map<String, String>> res = new ArrayList<>();

        list.forEach(item -> {
            list.forEach(item2 -> {
                if (!item.equals(item2)) {
                    Map<String, String> comb = new HashMap<String, String>();
                    comb.put(item, item2);
                    res.add(comb);
                }
            });
        });
        return res;
    }

    private String formatComponentString(String component) {
        component = component.replace(":", "_");
        return "D" + component;
    }

    public ArrayList<Map<String, String>> getIncompatibilities() {
        ArrayList<Map<String, String>> result = new ArrayList<>();
        ASTJSONObject root = (ASTJSONObject) this.getJSONDocument().getJSONValue();

        for (ASTJSONProperty prop : root.getPropList()) {
            if (prop.getKey().equals("incompatibilities")) {
                ((ASTJSONArray) prop.getValue())
                        .forEachJSONValues(incompatibilitiesArray -> {
                            ArrayList<String> stringOfComponents = new ArrayList<>();
                            ((ASTJSONArray) incompatibilitiesArray).forEachJSONValues(value -> {
                                String component = ((ASTJSONString) value).getStringLiteral().getValue();
                                stringOfComponents.add(formatComponentString(component));
                            });
                            result.addAll(getAllPairsOfListItems(stringOfComponents));
                        });
            }
        }

        return result;
    }

    public ArrayList<String> getDistributions() {
        ArrayList<String> result = new ArrayList<>();
        ASTJSONObject root = (ASTJSONObject) this.getJSONDocument().getJSONValue();

        for (ASTJSONProperty prop : root.getPropList()) {
            if (prop.getKey().equals("distribution")) {
                List<ASTJSONProperty> distributionProperties = ((ASTJSONObject) prop.getValue()).getPropList();
                distributionProperties.forEach(distributionProperty -> {
                    result.add(formatComponentString(distributionProperty.getKey()));
                });
            }
        }

        return result;
    }

    public ArrayList<String> getDependencies() {
        ArrayList<String> result = new ArrayList<>();
        ASTJSONObject root = (ASTJSONObject) this.getJSONDocument().getJSONValue();

        for (ASTJSONProperty prop : root.getPropList()) {
            if (prop.getKey().equals("dependencies")) {
                ((ASTJSONArray) prop.getValue())
                        .forEachJSONValues(dependenciesArray -> {

                            String dependent = null;
                            String dependency = null;
                            String type = null;
                            Integer amount_at_least = null;

                            List<ASTJSONProperty> propList = ((ASTJSONObject) dependenciesArray).getPropList();
                            for (ASTJSONProperty dprop : propList) {
                                switch (dprop.getKey()) {
                                    case "type":
                                        type = ((ASTJSONString) dprop.getValue()).getStringLiteral().getValue();
                                        break;
                                    case "dependent":
                                        dependent = ((ASTJSONString) dprop.getValue()).getStringLiteral().getValue();
                                        break;
                                    case "dependency":
                                        dependency = ((ASTJSONString) dprop.getValue()).getStringLiteral().getValue();
                                        break;
                                    case "amount_at_least":
                                        amount_at_least = ((ASTJSONNumber) dprop.getValue()).getSignedNumericLiteral().getValue();
                                        break;
                                    default:
                                        throw new IllegalStateException("Unexpected value: " + dprop.getKey());
                                }
                            }
                            Integer t = 1;
                        });
            }
        }
        return result;
    }
}
