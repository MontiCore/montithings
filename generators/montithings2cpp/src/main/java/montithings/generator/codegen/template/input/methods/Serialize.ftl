<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#include "/template/input/helper/GeneralPreamble.ftl">

// can be used to serialize all input variables through cereal
friend class cereal::access;

template<class Archive>
void serialize(Archive & archive)
{
archive(
    <#list comp.getAllIncomingPorts() as port>
      ${port.getName()}
      <#sep>,</#sep>
    </#list>
);
}