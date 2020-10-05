<#-- (c) https://github.com/MontiCore/monticore -->
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>

<#macro printVars comp ports config>
    // Ports
    <#list ports as port>
        <#assign type = ComponentHelper.getRealPortCppTypeString(port.getComponent().get(), port, config)>
        <#assign name = port.getName()>
        InOutPort<${type}>* ${name} = new InOutPort<${type}>();
    </#list>

    <#if comp.isDecomposed()>
        // Internal monitoring of ports (for pre- and postconditions of composed components)
        <#list ports as port>
            <#assign name = port.getName()>
            sole::uuid portMonitorUuid${name?cap_first} = sole::uuid4 ();
        </#list>
    </#if>
</#macro>

<#macro printMethodHeaders ports config>
    <#list ports as port>
        <#assign type = ComponentHelper.getRealPortCppTypeString(port.getComponent().get(), port, config)>
        <#assign name = port.getName()>
        InOutPort<${type}>* getPort${name?cap_first}();
        void addInPort${name?cap_first}(Port<${type}>* ${name});
        void removeInPort${name?cap_first}(Port<${type}>* ${name});
        void addOutPort${name?cap_first}(Port<${type}>* ${name});
        void removeOutPort${name?cap_first}(Port<${type}>* ${name});
    </#list>
</#macro>

<#macro printMethodBodies ports comp compname config>
    <#list ports as port>
        <#assign type = ComponentHelper.getRealPortCppTypeString(port.getComponent().get(), port, config)>
        <#assign name = port.getName()>
        ${Utils.printTemplateArguments(comp)}
        InOutPort<${type}>* ${compname}${Utils.printFormalTypeParameters(comp)}::getPort${name?cap_first}(){
        return ${name};
        }

        ${Utils.printTemplateArguments(comp)}
        void ${compname}${Utils.printFormalTypeParameters(comp)}::addInPort${name?cap_first}(Port<${type}>* port){
          ${name}->getInport ()->addManagedPort (port);
        }

        ${Utils.printTemplateArguments(comp)}
        void ${compname}${Utils.printFormalTypeParameters(comp)}::removeInPort${name?cap_first}(Port<${type}>* port){
          ${name}->getInport ()->removeManagedPort (port);
        }

        ${Utils.printTemplateArguments(comp)}
        void ${compname}${Utils.printFormalTypeParameters(comp)}::addOutPort${name?cap_first}(Port<${type}>* port){
          ${name}->getOutport ()->addManagedPort (port);
        }

        ${Utils.printTemplateArguments(comp)}
        void ${compname}${Utils.printFormalTypeParameters(comp)}::removeOutPort${name?cap_first}(Port<${type}>* port){
          ${name}->getOutport ()->removeManagedPort (port);
        }
    </#list>
</#macro>
