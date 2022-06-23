package montithings.generator.cd2proto.helper.resolver;

import de.monticore.types.check.SymTypeOfObject;

/**
 * I am currently unsure what the {@link SymTypeOfObject} entails, my best guess is any type explicitly declared in
 * the {@link montithings.generator.cd2proto.ProtoGenerator} via the mill/scope such as String or the ports.
 */
public class ObjectTypeResolver implements ITypeResolver<SymTypeOfObject> {
    @Override
    public String resolve(SymTypeOfObject sym) {
        if(sym.getBaseName().equals("String")) return "string";
        return sym.getBaseName();
    }
}
