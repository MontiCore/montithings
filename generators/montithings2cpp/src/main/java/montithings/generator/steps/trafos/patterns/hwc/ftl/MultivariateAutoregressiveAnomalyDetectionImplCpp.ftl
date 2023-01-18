${tc.signature("compname", "namesOfInputPorts", "namesOfOutputPorts")}
#include "MultivariateAutoregressiveAnomalyDetectionImpl.h"
#include "${compname}Impl.h"
#include <iostream>

${Utils.printNamespaceStart(comp)}
    ${compname}Result
    ${compname}Impl::getInitialValues()
    {
      return {};
    }

    ${compname}Result
    ${compname}Impl::compute(${compname}Input input)
    {
      ${compname}Result result;

      <#list namesOfInputPorts as nameOfInputPort>
      float ${nameOfInputPort} = (float)input.get${nameOfInputPort}().value();
      </#list>

      std::vector<float> inputs(${namesOfInputPorts?size});

      <#list namesOfInputPorts as nameOfInputPort>
      inputs.push_back(${nameOfInputPort});
      </#list>

      bool is_anomaly = this->ad->is_anomaly(inputs);

      if (!is_anomaly)
      {
        <#list 0..namesOfOutputPorts?size-1 as i>
        result.set${namesOfOutputPorts[i]}(input.get${namesOfInputPorts[i]}().value());
        </#list>
      }
      else
      {
        std::cout << "${compname}: Input is anomaly. Block sending." << std::endl;
      }

      return result;
    }
${Utils.printNamespaceEnd(comp)}