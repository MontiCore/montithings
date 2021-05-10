<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config", "existsHWC")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign className = comp.getName() + "DDSParticipant">
<#if existsHWC>
    <#assign className += "TOP">
</#if>

#include "${className}.h"

${Utils.printNamespaceStart(comp)}

${className}::${className}
(${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.getName()} *comp, int argc, char *argv[]) : comp (comp)
{
  while (!this->tryInitializeDDS (argc, argv)) {
    <#if config.getSplittingMode().toString() == "LOCAL">
      CLOG (DEBUG, "DDS") << "Creating dds instances failed. Is multicast enabled/allowed?";
    <#else>
      CLOG (DEBUG, "DDS") << "Creating dds instances failed. Is the DCPSInfoRepo service running?";
    </#if>
    CLOG (DEBUG, "DDS") << "Trying again...";
    std::this_thread::sleep_for(std::chrono::seconds(1));
  }
}

bool
${className}::tryInitializeDDS (int argc, char *argv[])
{
  DDS::DomainParticipantFactory_var dpf = TheParticipantFactoryWithArgs (argc, argv);

  // We do not make use of multiple DDS domains yet, arbitrary but fixed id will do it
  int domainId = 42;
  if (!participant)
    {
      participant = dpf->create_participant (domainId, PARTICIPANT_QOS_DEFAULT,
                                             // no listener required
                                             0,
                                             // default status mask ensures that
                                             // all relevant communication status
                                             // changes are communicated to the
                                             // application
                                             OpenDDS::DCPS::DEFAULT_STATUS_MASK);
    }

  if (!participant)
    {
      std::cerr << "DDS creation of the participant instance failed." << std::endl;
      return false;
    }

  DDSMessage::MessageTypeSupport_var ts = new DDSMessage::MessageTypeSupportImpl;
  type_name = ts->get_type_name ();

  if (ts->register_type (participant, "") != DDS::RETCODE_OK)
    {
      std::cerr << "DDS creation of the message type support failed." << std::endl;
      return false;
    }

  if (!publisher)
    {
      publisher = participant->create_publisher (PUBLISHER_QOS_DEFAULT, 0,
                                                 OpenDDS::DCPS::DEFAULT_STATUS_MASK);
    }
  if (!publisher)
    {
      std::cerr << "DDS creation of the publisher instance failed." << std::endl;
      return false;
    }
  if (!subscriber)
    {
      subscriber = participant->create_subscriber (SUBSCRIBER_QOS_DEFAULT, 0,
                                                   OpenDDS::DCPS::DEFAULT_STATUS_MASK);
    }
  if (!subscriber)
    {
      std::cerr << "DDS creation of the subsciber instance failed." << std::endl;
      return false;
    }

  return true;
}


void ${className}::onNewConnectors(std::string payload)
{
  ${tc.includeArgs("template.util.dds.participant.printOnNewConnectors", [comp, config])}
}

void
${className}::initializePorts ()
{
  ${tc.includeArgs("template.component.helper.AddDDSPorts", [comp, config])}
}

void
${className}::publishConnectors ()
{
  ${tc.includeArgs("template.util.dds.participant.printPublishConnectors", [comp, config])}
}

${Utils.printNamespaceEnd(comp)}