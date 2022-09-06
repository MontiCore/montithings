package montithings.generator.cd2proto.helper.resolver;

import de.monticore.types.check.SymTypeOfGenerics;
import montithings.generator.cd2proto.helper.StringHelper;
import montithings.generator.cd2proto.helper.TypeHelper;

/**
 * Resolves types involving generics such as {@link java.util.List} or {@link java.util.Map}
 */
public class GenericTypeResolver implements ITypeResolver<SymTypeOfGenerics> {

    private final TypeHelper th;
    /**
     *
     * @param th A TypeHelper used to resolve the argument(s) of the generic type
     */
    public GenericTypeResolver(TypeHelper th) {
        this.th = th;
    }

    @Override
    public String resolve(SymTypeOfGenerics sym) {
        switch(sym.getTypeInfo().getName()) {
            case "Optional":
            case "Set":
            case "List":
                if(sym.getArgument(0).getTypeInfo().getName().equals("List")) {
                    //nested list! we need to generate a wrapper message

                    //not a fan of the replace here but as a temporary measure for testing it works fine
                    //ideally this class is going to recognize whether repeated should be applied based on the nesting state
                    String typeName = "ListOf" + StringHelper.upperFirst(th.translate(sym.getArgument(0)).replace("repeated ", ""));
                    th.getNestedListHelper().addListWrapper((SymTypeOfGenerics) sym.getArgument(0), typeName);
                    return "repeated " + typeName;
                } else if(sym.getArgument(0).getTypeInfo().getName().equals("Map")) {
                    throw new UnsupportedOperationException("Protobuf does not support repeating the map type and no wrapper is currently implemented");
                }
                String genericType = th.translate(sym.getArgument(0));
                return "repeated " + genericType;
            case "Map":
                return String.format("map<%s,%s>", th.translate(sym.getArgument(0)), th.translate(sym.getArgument(1)));
            default:
                throw new IllegalArgumentException("Unsupported generic type: " + sym.getTypeInfo().getName());
        }
    }
}
