${tc.signature("packageName", "compname", "nameOfUrlInputPort", "nameOfDataInputPort", "nameOfOutputPort")}
#include "${compname}Impl.h"

namespace montithings {
    namespace ${packageName} {
        ${compname}Result
        ${compname}Impl::getInitialValues()
        {
          return {};
        }

        ${compname}Result
        ${compname}Impl::compute(${compname}Input input)
        {
          ${compname}Result result;

          if (input.get${nameOfUrlInputPort?cap_first}()) {
            AzureBlobStorageClient *blobClient = new AzureBlobStorageClient();

            std::string json = blobClient->download(input.get${nameOfUrlInputPort?cap_first}().value());

            auto origData = jsonToData(json);

            result.set${nameOfOutputPort?cap_first}(origData);
            interface.getPort${nameOfOutputPort?cap_first}()->setNextValue(result.get${nameOfOutputPort?cap_first}Message());
          }

          if (input.get${nameOfUrlInputPort?cap_first}()) {
            result.set${nameOfOutputPort?cap_first}(input.get${nameOfUrlInputPort?cap_first}().value());
            interface.getPort${nameOfOutputPort?cap_first}()->setNextValue(result.get${nameOfOutputPort?cap_first}Message());
          }

          return result;
        }
    } // namespace ${packageName}
} // namespace montithings