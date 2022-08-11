package montithings.generator.cd2proto.helper.resolver;

import com.google.common.collect.ImmutableMap;
import de.monticore.types.check.SymTypeConstant;
import montithings.generator.cd2proto.helper.resolver.ITypeResolver;

import java.util.HashMap;
import java.util.Map;

/**
 * Resolves primitive types from Java to Protobuf.
 */
public class PrimitiveTypeResolver implements ITypeResolver<SymTypeConstant> {
    private final Map<String, String> map = new HashMap<>();

    public PrimitiveTypeResolver() {
        map.put("int", "int32");
        map.put("long", "int64");
        map.put("boolean", "bool");
        map.put("byte", "int32");
        map.put("char", "uint32");
        map.put("double", "double");
        map.put("float", "float");
        map.put("short", "int32");
    }

    @Override
    public String resolve(SymTypeConstant sym) {
        if(!map.containsKey(sym.getTypeInfo().getName())) {
            throw new IllegalArgumentException("Unsupported primitive type: " + sym.getTypeInfo().getName());
        }
        return map.get(sym.getTypeInfo().getName());
    }

    /**
     * @return A map showing which Java types are mapped to which Protobuf types
     */
    public Map<String, String> getMappedTypes() {
        return ImmutableMap.copyOf(map);
    }
}
