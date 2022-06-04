package montithings.generator.cd2proto.helper;

import de.monticore.types.check.SymTypeExpression;
import de.se_rwth.commons.logging.Log;
import montithings.generator.cd2proto.helper.resolver.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Helper that takes a SymTypeExpression from the classdiagrams language and converts the type to a Protobuf-compatible type.
 */
public class TypeHelper {
    @SuppressWarnings("rawtypes")
    private final Map<Predicate<SymTypeExpression>, ITypeResolver> typeMap = new HashMap<>();

    private final NestedListHelper nlh = new NestedListHelper();
    public TypeHelper() {
        typeMap.put(SymTypeExpression::isTypeConstant, new PrimitiveTypeResolver());
        typeMap.put(SymTypeExpression::isArrayType, new ArrayTypeResolver(this));
        typeMap.put(SymTypeExpression::isGenericType, new GenericTypeResolver(this, nlh));
        typeMap.put(SymTypeExpression::isObjectType, new ObjectTypeResolver());
    }

    /**
     * Translates a SymTypeExpression to the corresponding Protobuf type declaration. Also converts lists and arrays.
     * @param type The type from the CD
     * @return A corresponding Protobuf type declaration
     */
    public String translate(SymTypeExpression type) {
        //the type should be guaranteed to be correct by the predicate
        //noinspection unchecked
        return typeMap.entrySet()
                .stream()
                .filter(e -> e.getKey().test(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported SymTypeExpression: " + type.getClass().getName()))
                .getValue()
                .resolve(type);
    }

    public NestedListHelper getNestedListHelper() {
        return nlh;
    }
}
