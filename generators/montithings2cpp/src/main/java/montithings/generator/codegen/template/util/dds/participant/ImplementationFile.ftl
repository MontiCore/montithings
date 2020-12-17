<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "config")}
<#assign Utils = tc.instantiate("montithings.generator.codegen.util.Utils")>
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

#include "${comp.getName()}DDSParticipant.h"

${Utils.printNamespaceStart(comp)}

${comp.getName()}DDSParticipant::${comp.getName()}DDSParticipant
(${ComponentHelper.printPackageNamespaceForComponent(comp)}${comp.getName()} *comp, int argc, char *argv[]) : comp (comp)
{
  while (!this->tryInitializeDDS (argc, argv)) {
    <#if config.getSplittingMode().toString() == "LOCAL">
      std::cout << "Creating dds instances failed. Is multicast enabled/allowed?" << std::endl;
    <#else>
      std::cout << "Creating dds instances failed. Is the DCPSInfoRepo service running?" << std::endl;
    </#if>
    std::cout << "Trying again..." << std::endl;
    std::this_thread::sleep_for(std::chrono::seconds(1));
  }
}

bool
${comp.getName()}DDSParticipant::tryInitializeDDS (int argc, char *argv[])
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


void ${comp.getName()}DDSParticipant::onNewConnectors(std::string payload)
{
  ${tc.includeArgs("template.util.dds.participant.printOnNewConnectors", [comp, config])}
}

void
${comp.getName()}DDSParticipant::initializePorts ()
{
  ${tc.includeArgs("template.util.ports.printAddDDSPorts", [comp, config])}
}

void
${comp.getName()}DDSParticipant::publishConnectors ()
{
  ${tc.includeArgs("template.util.dds.participant.printPublishConnectors", [comp, config])}
}

${Utils.printNamespaceEnd(comp)}