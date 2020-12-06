<#-- (c) https://github.com/MontiCore/monticore -->
#!/bin/sh
${tc.signature("comp", "config")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#assign instances = ComponentHelper.getInstances(comp)>

<#if config.getSplittingMode().toString() != "OFF">
  <#assign lineBreak = "\\">
<#else>
  <#assign lineBreak = ")">
</#if>

rm -f dockerStop.sh
<#list instances as pair >
  CONTAINER=$(docker run -d --rm --net=host ${pair.getKey().fullName?lower_case}:latest ${pair.getValue()} ${lineBreak}
  <#if config.getMessageBroker().toString() == "MQTT">
    host.docker.internal 1883)
  </#if>
  <#-- TODO: DDS -->
  echo docker stop $CONTAINER >> dockerStop.sh
</#list>
chmod +x dockerStop.sh