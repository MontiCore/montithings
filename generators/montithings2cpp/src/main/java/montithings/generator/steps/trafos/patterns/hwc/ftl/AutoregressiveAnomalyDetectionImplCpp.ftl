${tc.signature("compname", "nameOfInputPort")}
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

      float value = (float)input.get${nameOfInputPort}().value();
      bool is_anomaly = this->ad->is_anomaly(value);

      if (!is_anomaly)
      {
        result.setValue(input.get${nameOfInputPort}().value());
      }
      else
      {
        std::cout << "${compname}: " << value << " is anomaly. Block sending." << std::endl;
      }

      return result;
    }
${Utils.printNamespaceEnd(comp)}