${tc.signature("packageName", "compname", "namesOfInputPorts", "existsHwc")}
#include "${compname}State.h"
#include <iostream>

namespace montithings {
    namespace ${packageName} {
        <#list 0..namesOfInputPorts?size-1 as i>
        std::vector<float>
        ${compname}State::get_past_values_${namesOfInputPorts[i]}()
        {
          return this->past_values_${namesOfInputPorts[i]};
        }

        void
        ${compname}State::add_past_value_${namesOfInputPorts[i]}(float value)
        {
          this->past_values_${namesOfInputPorts[i]}.push_back(value);
        }
        </#list>
    } // namespace ${packageName}
} // namespace montithings