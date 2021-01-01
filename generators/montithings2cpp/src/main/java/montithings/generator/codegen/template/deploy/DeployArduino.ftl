<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "compname", "existsHWC")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>

#include "${compname}.h"

${ComponentHelper.printPackageNamespaceForComponent(comp)}${compname} cmp;
const long interval = ${ComponentHelper.getExecutionIntervalInMillis(comp)};
unsigned long previousMillis = 0;

void setup<#if existsHWC>TOP</#if>() {
Serial.begin(9600);
cmp.setUp(<#if ComponentHelper.isTimesync(comp)>
  TIMESYNC
<#else>
  EVENTBASED
</#if>);
cmp.init();
<#if !ComponentHelper.isTimesync(comp)>
  cmp.start();
</#if>
}

void loop<#if existsHWC>TOP</#if>() {
<#if ComponentHelper.isTimesync(comp)>
  unsigned long currentMillis = millis();

  if (currentMillis >= previousMillis + interval) {
  previousMillis = currentMillis;
  cmp.compute();
  }
</#if>
}