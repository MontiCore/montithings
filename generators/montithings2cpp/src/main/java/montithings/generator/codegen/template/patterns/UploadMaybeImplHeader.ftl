${tc.signature("packageName", "compname")}
#pragma once
#include "${compname}ImplTOP.h"
#include "patterns/AzureBlobStorageClient.h"
#include "Utils.h"

namespace montithings {
    namespace ${packageName} {
        class ${compname}Impl : public ${compname}ImplTOP
        {
        public:
          using ${compname}ImplTOP::${compname}ImplTOP;
          ${compname}Result getInitialValues() override;
          ${compname}Result compute(${compname}Input input) override;
        };
    } // namespace ${packageName}
} // namespace montithings