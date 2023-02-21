#pragma once

#define CPPHTTPLIB_OPENSSL_SUPPORT
#include "cpp-base64/base64.h"
#include "cpp-httplib/httplib.h"
#include <iomanip>
#include <iostream>
#include <sstream>
#include <string>
#include <vector>

class AzureBlobStorageClient
{
public:
  bool shouldUpload(std::string json, float maxPortSize);
  std::string upload(std::string json, std::string filename, std::string containername,
                     std::string containerUrl);
  std::string download(std::string downloadUrl);
  std::vector<std::string> getUploadUrlPart(std::string filename, std::string containername,
                                            std::string blobServiceSasUrl);
  std::string getDownloadUrl(std::string filename, std::string containername,
                             std::string blobServiceSasUrl);
  std::vector<std::string> getDownloadUrlParts(std::string url);
  std::vector<std::string> split(std::string str, std::string delim);
};
