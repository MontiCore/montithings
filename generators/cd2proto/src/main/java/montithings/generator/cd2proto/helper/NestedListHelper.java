package montithings.generator.cd2proto.helper;

import de.monticore.symbols.basicsymbols._symboltable.TypeSymbol;
import de.monticore.types.check.SymTypeExpression;
import de.monticore.types.check.SymTypeOfGenerics;

import java.util.*;

public class NestedListHelper {
    private Map<String, SymTypeOfGenerics> requiredWrappers = new HashMap<>();

    public void addListWrapper(SymTypeOfGenerics ts, String name) {
        this.requiredWrappers.put(name, ts);
    }
    public String generateWrappers() {
        StringBuilder sb = new StringBuilder();
        requiredWrappers.forEach((k, v) -> {
            String typeName = v.getArgument(0).getTypeInfo().getName();
            if(typeName.equals("String")) typeName = "string";
            sb.append("message ").append(k).append(" {\n");
            sb.append("    repeated ").append(k).append(" ").append(typeName).append("s");
            sb.append(" = 1;\n}\n\n");
        });
        return sb.toString();
    }
}
