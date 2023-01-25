${tc.signature("packageName", "compname", "batchesOfNamesOfInputPorts", "existsHwc")}
#pragma once
#include "${compname}StateTOP.h"

namespace montithings {
    namespace ${packageName} {
        class ${compname}State : public ${compname}StateTOP
        {

        protected:
          <#list 0..batchesOfNamesOfInputPorts?size-1 as i>
          std::vector<std::vector<float>> past_values_${i};
          </#list>

        public:
          using ${compname}StateTOP::${compname}StateTOP;
          <#list 0..batchesOfNamesOfInputPorts?size-1 as i>
          std::vector<std::vector<float>> get_past_values_${i}();
          void add_past_value_${i}(std::vector<float> value);
          </#list>
        };
    } // namespace ${packageName}
} // namespace montithings