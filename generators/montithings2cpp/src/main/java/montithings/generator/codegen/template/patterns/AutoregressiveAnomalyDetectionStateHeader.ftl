${tc.signature("packageName", "compname", "namesOfInputPorts")}
#pragma once
#include "${compname}StateTOP.h"

namespace montithings {
    namespace ${packageName} {
        class ${compname}State : public ${compname}StateTOP
        {

        protected:
          <#list 0..namesOfInputPorts?size-1 as i>
          std::vector<float> past_values_${namesOfInputPorts[i]};
          </#list>
          
        public:
          using ${compname}StateTOP::${compname}StateTOP;
          <#list 0..namesOfInputPorts?size-1 as i>
          std::vector<float> get_past_values_${namesOfInputPorts[i]}();
          void add_past_value_${namesOfInputPorts[i]}(float value);
          </#list>
        };
    } // namespace ${packageName}
} // namespace montithings