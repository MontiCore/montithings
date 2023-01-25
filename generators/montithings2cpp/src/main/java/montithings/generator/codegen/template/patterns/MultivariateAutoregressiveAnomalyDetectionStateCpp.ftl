${tc.signature("packageName", "compname", "batchesOfNamesOfInputPorts", "existsHwc")}
#include "${compname}State.h"
#include <iostream>

namespace montithings {
    namespace ${packageName} {
        <#list 0..batchesOfNamesOfInputPorts?size-1 as i>
        std::vector<std::vector<float>>
        ${compname}State::get_past_values_${i}()
        {
          return this->past_values_${i};
        }

        void
        ${compname}State::add_past_value_${i}(std::vector<float> value)
        {
          this->past_values_${i}.push_back(value);
        }
        </#list>
    } // namespace ${packageName}
} // namespace montithings