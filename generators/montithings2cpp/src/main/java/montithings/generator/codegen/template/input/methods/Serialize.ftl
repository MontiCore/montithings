<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/input/helper/GeneralPreamble.ftl">

// can be used to serialize all input variables through cereal
friend class cereal::access;

template<class Archive>
void serialize(Archive & archive)
{
<#if comp.getAllIncomingPorts()?has_content>
  archive(
    <#assign ports = []>
    <#list comp.getAllPorts() as port>
      <#if port.isIncoming()>
        <#assign ports = ports + [ "${port.getName()}" ] />
      <#else>
        // include ports which are target ports of subcomponents as well
        <#if !comp.isAtomic()>
          <#list comp.getAstNode().getConnectors() as connector>
            <#list connector.getTargetList() as target>
              <#if target.getQName() == port.getName()>
                <#assign ports = ports + [ "${port.getName()}" ] />
             </#if>
          </#list>
        </#list>
      </#if>
    </#if>
    </#list>

    <#list ports as port>
    CEREAL_NVP_("${port}", ${port}.getPayload())
    <#sep>,</#sep>
    </#list>
    );
</#if>
}