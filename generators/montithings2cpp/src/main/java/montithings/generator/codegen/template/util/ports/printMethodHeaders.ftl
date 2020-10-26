<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("ports","config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#list ports as port>
    <#assign type = ComponentHelper.getRealPortCppTypeString(port.getComponent().get(), port, config)>
    <#assign name = port.getName()>
    InOutPort<${type}>* getPort${name?cap_first}();
    void addInPort${name?cap_first}(Port<${type}>* ${name});
    void removeInPort${name?cap_first}(Port<${type}>* ${name});
    void addOutPort${name?cap_first}(Port<${type}>* ${name});
    void removeOutPort${name?cap_first}(Port<${type}>* ${name});
</#list>