<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp","config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#if comp.getSubComponents()?size == 0>
    // component has no subcomponents - nothing to do
<#else>

    bool allConnected = 0;
    while (!allConnected)
    {
    std::cout << "Searching for subcomponents\n";
    <#list comp.subComponents as subcomponent>
        <#assign subcomponentSymbol = subcomponent.type>
        // ${subcomponentSymbol.getName()} ${subcomponent.getName()}
        ${tc.includeArgs("template.util.comm.SCDetailsHelper", [comp, subcomponent])}

        <#if config.getSplittingMode().toString() == "LOCAL">
            std::ifstream i (this->portConfigFilePath);
            json j;
            i >> j;
            std::string ${subcomponent.getName()}_port = j["${subcomponent.getName()}"]["management"].get${"<std::string>"} ();
        <#else>
            std::string ${subcomponent.getName()}_port = "1337";
        </#if>

        std::string ${subcomponent.getName()}_ip = comm->getIpOfComponent ("${subcomponent.getName()}");

        // tell subcomponent where to connect to
        <#list comp.getAstNode().getConnectors() as connector>
            <#list connector.targetList as target>
                <#if !target.isPresentComponent() && subcomponent.getName() == connector.getSource().getComponent()>
                    <#list subcomponentSymbol.ports as p>
                        <#if p.getName() == connector.getSource().port>
                            // set receiver
                            std::string communicationPort = j["${subcomponent.getName()}"]["communication"].get${"<std::string>"} ();
                            std::string ${subcomponent.getName()}_uri = "ws://" + ${subcomponent.getName()}_ip + ":" + communicationPort + "/" + comp->getInstanceName () + ".${subcomponent.getName()}/out/${p.getName()}";

                            // implements "${connector.getSource().getQName()} -> ${target.getQName()}"
                            comp->addInPort${target.port?cap_first}(new WSPort<${ComponentHelper.getRealPortCppTypeString(p.getComponent().get(), p, config)}>(INCOMING, ${subcomponent.getName()}_uri));

                        </#if>
                    </#list>
                </#if>
            <#-- TODO: What happens if !target.isPresentComponent() --><#if target.isPresentComponent() && subcomponent.getName() == target.getComponent()>
                {
                <#if !connector.getSource().isPresentComponent()>
                    PortToSocket message ("${target.port}", comm->getOurIp() + ":" + communicationPort, "/" + comp->getInstanceName () + "/out/${connector.getSource().port}");
                <#else>
                    <#list comp.subComponents as sourceSubcomp>
                        <#if sourceSubcomp.getName() == connector.getSource().getComponent()>
                            std::string communicationPort = j["${connector.getSource().getComponent()}"]["communication"].get${"<std::string>"} ();
                            PortToSocket message ("${target.port}", comp->get${connector.getSource().getComponent()?cap_first}IP() + ":" + communicationPort, "/${sourceSubcomp.fullName}/out/${connector.getSource().port}");
                        </#if>
                    </#list>
                </#if>

                comm->sendManagementMessage (${subcomponent.getName()}_ip, ${subcomponent.getName()}_port, &message);
                }
            </#if>
            </#list>
        </#list>

        comp->set${subcomponent.getName()?cap_first}IP(${subcomponent.getName()}_ip);
        std::cout << "Found ${subcomponentSymbol.getName()} ${subcomponent.getName()}\n";

        continue;
        }
    </#list>

    // continue if all components are connected
    allConnected =
    <#list comp.subComponents as subcomponent >
        comp->get${subcomponent.getName()?cap_first}IP().length() != 0<#sep>&&</#sep>
    </#list>;
    if (!allConnected) {
    // circuit breaker
    std::this_thread::sleep_for(std::chrono::milliseconds(100));
    }
    std::cout << "Found all subcomponents." << std::endl;
    }
</#if>