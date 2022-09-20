// (c) https://github.com/MontiCore/monticore
package montithings.generator.cd2proto.helper.resolver;

import de.monticore.types.check.SymTypeArray;
import montithings.generator.cd2proto.helper.TypeHelper;

/**
 * Resolves {@link SymTypeArray} to a Protobuf declaration.
 * An input like this: <pre>int[]</pre>
 * Would turn into this: <pre>repeated int32</pre>
 */
public class ArrayTypeResolver implements ITypeResolver<SymTypeArray> {

    private TypeHelper th;

    /**
     *
     * @param th A {@link TypeHelper} that is used to resolve the type argument of the array.
     */
    public ArrayTypeResolver(TypeHelper th) {
        this.th = th;
    }

    @Override
    public String resolve(SymTypeArray sym) {
        if(sym.getDim() > 1) {
            throw new IllegalArgumentException("Multi-dimensional arrays are currently not supported");
        }
        return "repeated " + th.translate(sym.getArgument());
    }
}
