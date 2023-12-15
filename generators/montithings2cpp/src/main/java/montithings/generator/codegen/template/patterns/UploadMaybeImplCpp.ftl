<#-- (c) https://github.com/MontiCore/monticore -->
${tc.signature("packageName", "compname", "maxPortSize", "containername", "nameOfUrlOutputPort", "nameOfDataOutputPort", "nameOfInputPort")}
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

          float maxPortSize = ${maxPortSize};
          std::string containername = "${containername}";
          std::string containerSasUrl = std::getenv("containerSasUrl");

          if (input.get${nameOfInputPort?cap_first}()) {
            std::string json = dataToJson(input.get${nameOfInputPort?cap_first}().value());
            std::string filename = "${compname}${nameOfInputPort}.json";

            if (json.size() > maxPortSize) {
              AzureBlobStorageClient *blobClient = new AzureBlobStorageClient();

              std::string downloadUrl = blobClient->upload(json, filename, containername, containerSasUrl);

              result.set${nameOfUrlOutputPort?cap_first}(downloadUrl);
              interface.getPort${nameOfUrlOutputPort?cap_first}()->setNextValue(result.get${nameOfUrlOutputPort?cap_first}Message());
            } else {
              result.set${nameOfDataOutputPort?cap_first}(input.get${nameOfInputPort?cap_first}().value());
              interface.getPort${nameOfDataOutputPort?cap_first}()->setNextValue(result.get${nameOfDataOutputPort?cap_first}Message());
            }
          }

          return result;
        }
    } // namespace ${packageName}
} // namespace montithings