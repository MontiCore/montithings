package de.rwth.se.iotlab.config._ast;

import de.monticore.lang.json._ast.*;

import javax.json.JsonValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ASTConfig extends ASTConfigTOP {
    public ArrayList<Map<String, String>> getIncompatibilities() {
        ArrayList<Map<String, String>> result = new ArrayList<>();
        ASTJSONObject root = (ASTJSONObject) this.getJSONDocument().getJSONValue();

        for (ASTJSONProperty prop : root.getPropList()) {
            if (prop.getKey().equals("incompatibilities")) {
                ((ASTJSONArray) prop.getValue())
                        .forEachJSONValues(incompatibilitiesArray -> {
                            ArrayList<String> stringOfComponents = new ArrayList<>();
                            ((ASTJSONArray) incompatibilitiesArray).forEachJSONValues(value -> {
                                stringOfComponents.add(((ASTJSONString) value).getStringLiteral().getValue());
                            });
                            stringOfComponents.add("sad");
                        });
                //for (JSONValues incompatibilities : (ASTJSONArray) prop.getValue()) {

                //}
                System.out.println(prop);
            }
        }

        return result;
    }
}
