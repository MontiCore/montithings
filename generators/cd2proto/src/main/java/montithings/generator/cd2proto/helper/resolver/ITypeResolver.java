package montithings.generator.cd2proto.helper.resolver;

import de.monticore.types.check.SymTypeExpression;

/**
 * A class implementing this interface should be able to resolve a type from a CD to a corresponding type as a
 * Protobuf declaration.
 * @param <T> The kind of SymTypeExpression this resolver can resolve.
 */
@FunctionalInterface
public interface ITypeResolver<T extends SymTypeExpression> {
    String resolve(T sym);
}
