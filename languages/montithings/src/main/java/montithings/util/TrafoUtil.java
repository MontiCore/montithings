package montithings.util;

import arcbasis._ast.*;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import genericarc._ast.ASTArcTypeParameter;
import genericarc._ast.ASTGenericComponentHead;
import montiarc._ast.ASTMACompilationUnit;

import java.util.*;
import java.util.stream.Collectors;

import static montithings.util.GenericBindingUtil.printSimpleType;

public abstract class TrafoUtil {

    /**
     * Returns the port type of a given port instance
     *
     * @param comp     AST of component which is modified
     * @param portName Qualified name of the port
     * @return ASTMCType or throws an exception if port is not found
     */
    public static ASTMCType getPortTypeByName(ASTMACompilationUnit comp, String portName) throws Exception {
        List<ASTPortDeclaration> sourcePorts = comp.getComponentType().getBody().streamArcElements()
                .filter(el -> el instanceof ASTComponentInterface)
                .map(el -> ((ASTComponentInterface) el).getPortDeclarationList())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        Optional<ASTMCType> portType = sourcePorts.stream()
                //TODO: code smell
                .filter(p -> p.getPort(0).getName().equals(portName))
                .map(ASTPortDeclarationTOP::getMCType).findFirst();

        if (!portType.isPresent()) {
            throw new Exception("Port named " + portName + " not found, could not get type.");
        }

        return portType.get();
    }

    /**
     * Returns the component type which declared the given port (part of a connection).
     * If the port is declared locally the name of the given component is returned
     * Otherwise the type is searched in the sub-component instantiations.
     * <p>
     * E.g. v -> sink.value
     * Left hand side is declared locally, right hand side is declared in instance sink.
     * When searching through all component instantiation, sink may resolve to type Sink.
     * <p>
     * This method may return null.
     *
     * @param comp      AST of component which contains the connection with the port
     * @param qNamePort AST of port which is part of a connection in comp
     * @return String of component type or null
     */
    public static String getPortOwningComponentType(ASTMACompilationUnit comp, ASTPortAccess qNamePort) {
        // TODO checking portSource.getCompoent() == null crashes app if null -> workaround via qName for now
        String test = qNamePort.getQName();
        if (!qNamePort.getQName().contains(".")) {
            // port is declared locally
            return comp.getComponentType().getName();
        }

        // Searches for the port declaration which is either in the current comp or in one of its subcomponents
        List<ASTArcElement> arcElementList = comp.getComponentType().getBody().getArcElementList();
        for (ASTArcElement element :
                arcElementList) {
            if (element instanceof ASTComponentInstantiation) {
                ASTComponentInstantiation inst = (ASTComponentInstantiation) element;
                for (String name : inst.getInstancesNames()) {
                    if (name.equals(qNamePort.getComponent())) {
                        return printSimpleType(inst.getMCType());
                    }
                }
            }
        }

        // TODO avoid returning null
        return null;
    }

    /**
     * Searches in the collection of models for the given name
     *
     * @param models      Collection of AST components where is searched in
     * @param comp        Component where the name appears. When there is no such model in the list,
     *                    the generic types of the given component will be checked.
     * @param qNameSearch Qualified component name
     * @return AST of searched component
     */
    public static ASTMACompilationUnit getComponentByName(Collection<ASTMACompilationUnit> models, ASTMACompilationUnit comp, String qNameSearch) {
        for (ASTMACompilationUnit model : models) {
            String qName = model.getPackage().getQName() + "." + model.getComponentType().getName();
            if (qName.equals(qNameSearch)) {
                return model;
            }
        }
        throw new NoSuchElementException("There is no such model named " + qNameSearch);
    }

    /**
     * Capitalizes first character of the given string
     *
     * @param str string to capitalized
     * @return capitalized string
     */
    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }

        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * @param models Collection of AST models where is searched in
     * @param child  AST child component
     * @return collection of parent component names
     */
    public static Collection<String> findParents(Collection<ASTMACompilationUnit> models, ASTMACompilationUnit child) {
        Collection<String> res = new ArrayList<>();
        String name = child.getComponentType().getName();

        for (ASTMACompilationUnit model : models) {
            boolean isParent = model.getComponentType().getSubComponentInstantiations().stream()
                    .map(s -> printSimpleType(s.getMCType()))
                    .filter(Objects::nonNull)
                    .anyMatch(t -> t.equals(name));
            if (isParent) {
                String qName = model.getPackage() + "." + model.getComponentType().getName();
                res.add(qName);
            }
        }

        return res;
    }

    /**
     * @param comp AST of model where the instantiation is declared
     * @param compName String of instantiated type
     * @return whether compName is a generic type in comp or not
     */
    public static boolean isGeneric(ASTMACompilationUnit comp, String compName) {
        List<ASTArcTypeParameter> typeParameters =
                ((ASTGenericComponentHead) comp.getComponentType().getHead()).getArcTypeParameterList();

        return typeParameters.stream().anyMatch(p -> p.getName().equals(compName));
    }

    public static List<String> getInterfaces(ASTMACompilationUnit comp, String compName) {
        List<ASTArcTypeParameter> typeParameters =
                ((ASTGenericComponentHead) comp.getComponentType().getHead()).getArcTypeParameterList();

        return typeParameters.stream()
                .filter(p -> p.getName().equals(compName))
                .map(ASTArcTypeParameter::getUpperBoundList)
                .flatMap(Collection::stream)
                .map(GenericBindingUtil::printSimpleType)
                .collect(Collectors.toList());
    }
}
