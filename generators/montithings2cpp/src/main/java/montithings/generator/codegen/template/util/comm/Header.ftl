# (c) https://github.com/MontiCore/monticore
<#--package montithings.generator.codegen.xtend.util

import arcbasis._symboltable.ComponentTypeSymbol
import arcbasis._symboltable.ComponentInstanceSymbol
import arcbasis._symboltable.PortSymbol
import arcbasis._ast.ASTConnector
import arcbasis._ast.ASTPortAccess
import montithings._ast.ASTMTComponentType
import montithings.generator.codegen.xtend.util.Utils
import montithings.generator.helper.ComponentHelper
import montithings.generator.codegen.ConfigParams-->
  def static String generateHeader (ComponentTypeSymbol comp, ConfigParams config) {
    return '''
    #pragma once
    #include "${comp.name}.h"
    #include "ManagementCommunication.h"
    #include "ManagementMessageProcessor.h"

    ${Utils.printNamespaceStart(comp)}

    class ${comp.name}Manager : public ManagementMessageProcessor
    {
    protected:
    montithings::hierarchy::${comp.name}* comp;
    ManagementCommunication* comm;
    std::string managementPort;
    std::string communicationPort;
    <#if config.getSplittingMode() == ConfigParams.SplittingMode.LOCAL>
 std::string portConfigFilePath;
 </#if>

    public:
    ${comp.name}Manager (${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.name} *comp, std::string managementPort, std::string communicationPort);

    /* 
     * Process management instructions from the enclosing component
     * Those are mostly connectors to other components
     */
    void process (std::string msg) override;

    /*
     * Initially create ports of this component
     */
    void initializePorts ();

    /*
     * Search for subcomponents
     * Tell subcomponents to which ports of other components they should connect
     */
    void searchSubcomponents ();
    };

    ${Utils.printNamespaceEnd(comp)}
    '''
  }