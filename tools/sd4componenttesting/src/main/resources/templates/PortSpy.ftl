<#macro printPortSpy compTypeName portName package port compName="" isComponent=false>
<#if isComponent == true>
  <#assign displayName = compName>
  <#assign displayClass = compTypeName + "_" + compName?cap_first>
<#else>
  <#assign displayName = compTypeName>
  <#assign displayClass = compTypeName>
</#if>
/**
 * This class records values of the "${displayName}" component's "${portName}" port
 */
class PortSpy_${displayClass}_${portName?cap_first} : public PortSpy<${package}${compTypeName}, ${port.getType().getTypeInfo().getName()}>
{
public:
  using PortSpy::PortSpy;

  void onEvent () override
  {
    tl::optional<Message<${port.getType().getTypeInfo().getName()}>> value
        = component->getInterface()->getPort${portName?cap_first}()->getCurrentValue(this->getUuid ());
    recordedMessages.push_back (value);
  }
};
</#macro>
