package montithings.util;

import arcbasis._ast.*;
import de.monticore.siunittypes4math._ast.ASTSIUnitType;
import de.monticore.types.mcbasictypes._ast.ASTMCType;
import montiarc._ast.ASTMACompilationUnit;
import montithings._visitor.MontiThingsVisitor;

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
                //.filter(p -> !p.getPortList().isEmpty())
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
     *
     * E.g. v -> sink.value
     * Left hand side is declared locally, right hand side is declared in instance sink.
     * When searching through all component instantiation, sink may resolve to type Sink.
     *
     * This method may return null.
     *
     * @param comp AST of component which contains the connection with the port
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
     * @param models Collection of AST components where is searched in
     * @param comp
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

        // TODO handle generic types

        /*
            component Calc<T extends MathExpression> {
                port in int x;
                port out int y;

                T t;

                x -> t.x;
                t.y -> y;
            }

            component Example {
                Calc<Doubler> c;
            }

            how to retrieve generic type parameters with extensions?
             */
        // Qualified names can be a generic type such as <T>
        String compName = qNameSearch.split("\\.")[1];
        List<ASTArcParameter> arcParameterList = comp.getComponentType().getHead().getArcParameterList();
        /*
        List<String> arcParameters = comp.getComponentType().getHead().streamArcParameters()
                .filter(p -> p.getName().equals(compName))
                .map(ASTArcParameter::getMCType)
                .map(Util::mCTypeToString)
                .collect(Collectors.toList());
        for (String parameter : arcParameters) {
            if (parameter.equals(qNameSearch.split("\\.")[1])) {

                //getComponentByName(models,comp,)
            }
        }
        */
        return null;
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
     * @param child AST child component
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
}
