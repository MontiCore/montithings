// (c) https://github.com/MontiCore/monticore
#include "SinkImpl.h"
#include "patterns/AzureBlobStorageClient.h"
#include <iostream>

namespace montithings
{
  namespace hierarchy
  {

    SinkResult
    SinkImpl::getInitialValues()
    {
      return {};
    }

    SinkResult
    SinkImpl::compute(SinkInput input)
    {
      AzureBlobStorageClient *blobClient = new AzureBlobStorageClient();

      std::string downloadUrl = blobClient->upload(
          "Some filecontent", "filenametest.txt", "fileuploads",
          "https://pattern2netmin.blob.core.windows.net/"
          "?sv=2021-06-08&ss=bf&srt=o&sp=rwdlactfx&se=2024-01-01T16:10:53Z&st=2023-01-31T08:10:53Z&"
          "spr=https,http&sig=rixhY%2FZ1KQB0WzjB807Oi9T2YVA64nladtT2kySH05Y%3D");

      std::cout << "Download url: " << downloadUrl << std::endl;

      std::string filecontent = blobClient->download(downloadUrl, "2eXKm94oazUke1UzLvv3ShmeuctbV5OPiyjk2a+DTv7Sp4VGwxKe0/Z9O0fUqEOkS3ql+6/5EnTg+AStRMD8mg==");

      std::cout << "Filecontent: " << filecontent << std::endl;

      return {};
    }

  }
}