package montithings.generator.cd2proto.helper.resolver;

import de.monticore.types.check.SymTypeOfGenerics;
import montithings.generator.cd2proto.helper.NestedListHelper;
import montithings.generator.cd2proto.helper.StringHelper;
import montithings.generator.cd2proto.helper.TypeHelper;

public class GenericTypeResolver implements ITypeResolver<SymTypeOfGenerics> {

    private final TypeHelper th;
    private final NestedListHelper nlh;

    public GenericTypeResolver(TypeHelper th, NestedListHelper nlh) {
        this.th = th;
        this.nlh = nlh;
    }

    @Override
    public String resolve(SymTypeOfGenerics sym) {
        switch(sym.getTypeInfo().getName()) {
            case "List":
                if(sym.getArgument(0).getTypeInfo().getName().equals("List")) {
                    //nested list! we need to generate a wrapper message

                    //not a fan of the replace here but as a temporary measure for testing it works fine
                    //ideally this class is going to recognize whether repeated should be applied based on the nesting state
                    String typeName = "ListOf" + StringHelper.upperFirst(th.translate(sym.getArgument(0)).replace("repeated ", ""));
                    nlh.addListWrapper((SymTypeOfGenerics) sym.getArgument(0), typeName);
                    return "repeated " + typeName;
                }
                String genericType = th.translate(sym.getArgument(0));
                return "repeated " + genericType;
                //TODO: do we need Map/Set/Collection?
            default:
                throw new IllegalArgumentException("Unsupported generic type: " + sym.getTypeInfo().getName());
        }
    }
}
