${tc.signature("comp","config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
PortToSocket message(msg);

<#list comp.getPorts() as p>
    <#if !p.isOutgoing()>
        if (message.getLocalPort() == "${p.getName()}")
        {
        // connection information for port ${p.getName()} was received
        std::string ${p.getName()}_uri = "ws://" + message.getIpAndPort() + message.getRemotePort();
        std::cout << "Received connection: " << ${p.getName()}_uri << std::endl;
        comp->addInPort${p.getName()?cap_first}(new WSPort<${ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)}>(INCOMING, ${p.getName()}_uri));
        }
    </#if>
</#list>