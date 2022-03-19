<#-- (c) https://github.com/MontiCore/monticore -->
<#macro printObserver mainComp package>
<#import "/templates/PortSpy.ftl" as portSpy>
/**
 * This (abstract) class records all messages going through port.
 * The recorded messages can then be checked by the test case against the expected values.
 *
 * \tparam ComponentType class of the component this spy is attached to
 * \tparam PortType typename of the messages going through the port
 */
template <typename ComponentType, typename PortType> class PortSpy : public EventObserver
{
  protected:
    ComponentType *component;
    std::vector<tl::optional<Message<PortType>>> recordedMessages;

  public:
    explicit PortSpy (ComponentType *component) : component (component) {}

    const std::vector<tl::optional<Message<PortType>>> &
    getRecordedMessages () const
    {
      return recordedMessages;
    }
};


<#list mainComp.getPorts() as port>
  <#assign compTypeName = mainComp.getName()>
  <#assign portName = port.getName()>
  <@portSpy.printPortSpy compTypeName=compTypeName portName=portName package=package port=port/>

</#list>


<#list mainComp.getSubComponents() as component>
  <#list component.getType().getPorts() as port>
    <#assign compTypeName = component.getType().getName()>
    <#assign compName = component.getName()>
    <#assign portName = port.getName()>
    <@portSpy.printPortSpy compTypeName=compTypeName portName=portName package=package port=port compName=compName isComponent=true/>

  </#list>

</#list>
</#macro>
