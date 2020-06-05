package de.rwth.se.iotlab.config._ast;

import de.monticore.lang.json._ast.*;

import javax.json.JsonValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
}
