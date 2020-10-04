<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("comp", "compname")}
<#assign ComponentHelper = tc.instantiate("montithings.generator.helper.ComponentHelper")>
<#--package montithings.generator.codegen.xtend

import arcbasis._symboltable.ComponentTypeSymbol
import montithings.generator.helper.ComponentHelper
import montithings.generator.codegen.ConfigParams-->
    #include "${compname}.h"
    
    ${ComponentHelper.printPackageNamespaceForComponent(comp)}${compname} cmp;
    const long interval = ${ComponentHelper.getExecutionIntervalInMillis(comp)};
    unsigned long previousMillis = 0;
    
    void setup() {
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
    
    void loop() {
      <#if ComponentHelper.isTimesync(comp)>
      unsigned long currentMillis = millis();

      if (currentMillis >= previousMillis + interval) {
        previousMillis = currentMillis;
        cmp.compute();
      }
      </#if>
    }