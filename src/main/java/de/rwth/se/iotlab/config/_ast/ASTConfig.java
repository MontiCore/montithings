package de.rwth.se.iotlab.config._ast;

import de.monticore.lang.json._ast.*;
import de.monticore.literals.mccommonliterals._ast.ASTSignedLiteral;
import de.monticore.literals.mccommonliterals._ast.ASTSignedNatLiteral;

import javax.json.JsonValue;
import java.util.*;

public class ASTConfig extends ASTConfigTOP {

    private int aSTJSONNumber2Int(ASTJSONNumber number) {
        ASTSignedLiteral literal = number.getSignedNumericLiteral();
        if (literal instanceof ASTSignedLiteral) {
            ASTSignedNatLiteral nat = (ASTSignedNatLiteral) literal;
            return nat.getValue();
        }
        throw new IllegalArgumentException("Unexpected Could not convert number to int");
    }

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

    public ArrayList<Distribution> getDistributions() {
        ArrayList<Distribution> result = new ArrayList<>();
        ASTJSONObject root = (ASTJSONObject) this.getJSONDocument().getJSONValue();

        for (ASTJSONProperty prop : root.getPropList()) {
            if (prop.getKey().equals("distribution")) {
                List<ASTJSONProperty> distributionProperties = ((ASTJSONObject) prop.getValue()).getPropList();
                distributionProperties.forEach(distributionProperty -> {
                    Distribution distribution = new Distribution();
                    distribution.setName(formatComponentString(distributionProperty.getKey()));

                    List<ASTJSONProperty> distributionProperties2 = ((ASTJSONObject) distributionProperty.getValue()).getPropList();
                    distributionProperties2.forEach(prop2 -> {
                        if (prop2.getKey().equals("distribution_selection")) {
                            ASTJSONArray array = (ASTJSONArray) prop2.getValue();
                            array.getJSONValueList().forEach(item -> {
                                        if (((ASTJSONArray) item).getJSONValue(0) instanceof ASTJSONString) {
                                            // conjunction
                                            List<ASTJSONValue> itemValue = ((ASTJSONArray) item).getJSONValueList();
                                            distribution.addSelectionConjunctionProperty(
                                                    ((ASTJSONString) itemValue.get(0)).getStringLiteral().getValue(),
                                                    ((ASTJSONString) itemValue.get(1)).getStringLiteral().getValue(),
                                                    ((ASTJSONString) itemValue.get(2)).getStringLiteral().getValue(),
                                                    String.valueOf(aSTJSONNumber2Int((ASTJSONNumber) itemValue.get(3))));
                                        } else {
                                            //disjunction
                                        }
                                    }
                            );
                        } else if (prop2.getKey().equals("distribution_constraints")) {
                            ASTJSONArray array = (ASTJSONArray) prop2.getValue();
                            array.getJSONValueList().forEach(item -> {

                                List<ASTJSONValue> itemValue = ((ASTJSONArray) item).getJSONValueList();
                                String operator = ((ASTJSONString) itemValue.get(2)).getStringLiteral().getValue();
                                String number = "0";
                                if (itemValue.get(3) instanceof ASTJSONNumber) {
                                    number = String.valueOf(aSTJSONNumber2Int((ASTJSONNumber) itemValue.get(3)));
                                } else {
                                    number = ((ASTJSONString) itemValue.get(3)).getStringLiteral().getValue();
                                }
                                switch (operator) {
                                    case "<=":
                                        distribution.addLteConstraint(
                                                ((ASTJSONString) itemValue.get(0)).getStringLiteral().getValue(),
                                                ((ASTJSONString) itemValue.get(1)).getStringLiteral().getValue(),
                                                operator,
                                                number);
                                        break;
                                    case "==":
                                        if (number.equals("all")) {
                                            distribution.addCheckAllConstraint(
                                                    ((ASTJSONString) itemValue.get(0)).getStringLiteral().getValue(),
                                                    ((ASTJSONString) itemValue.get(1)).getStringLiteral().getValue(),
                                                    operator,
                                                    number);
                                        } else {
                                            distribution.addEqualConstraint(
                                                    ((ASTJSONString) itemValue.get(0)).getStringLiteral().getValue(),
                                                    ((ASTJSONString) itemValue.get(1)).getStringLiteral().getValue(),
                                                    operator,
                                                    number);
                                        }
                                        break;
                                    case ">=":
                                        distribution.addGteConstraint(
                                                ((ASTJSONString) itemValue.get(0)).getStringLiteral().getValue(),
                                                ((ASTJSONString) itemValue.get(1)).getStringLiteral().getValue(),
                                                operator,
                                                number);
                                        break;
                                }


                            });
                        }
                    });
                    result.add(distribution);
                });
            }
        }

        return result;
    }

    public ArrayList<Map<String, String>> getDependencies() {
        ArrayList<Map<String, String>> result = new ArrayList<>();
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
                                        amount_at_least = aSTJSONNumber2Int((ASTJSONNumber) dprop.getValue());
                                        break;
                                    default:
                                        throw new IllegalStateException("Unexpected value: " + dprop.getKey());
                                }
                            }
                            if (amount_at_least != null &&
                                    type != null &&
                                    dependency != null &&
                                    dependent != null) {
                                Map<String, String> item = new HashMap<>();
                                item.put("type", type);
                                item.put("dependency", dependency);
                                item.put("dependent", dependent);
                                item.put("amount_at_least", amount_at_least.toString());
                                result.add(item);
                            }
                        });
            }
        }
        return result;
    }
}
