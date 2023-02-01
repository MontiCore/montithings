#pragma once

#include "cpp-httplib/httplib.h"
#include "base64.h"
#include "hmac_sha256/hmac_sha256.h"
#include <string>
#include <vector>
#include <sstream>

class AzureBlobStorageClient
{
protected:
  bool shouldUpload(std::string json, float maxPortSize);
  std::string upload(std::string json, std::string filename, std::string containername, std::string containerUrl);
  std::string download(std::string downloadUrl, std::string accessKey);
  std::string getUploadUrl(std::string filename, std::string containername, std::string blobServiceSasUrl);
  std::string getDownloadUrl(std::string filename, std::string containername, std::string blobServiceSasUrl);
  std::vector<std::string> split(std::string str, std::string delim);
  std::string getUtcTime();
  std::string getAuthorization(std::string accessKey, std::string utcDateStr, std::string containername, std::string filename);
  std::string getContainerName(std::string downloadUrl);
  std::string getFilename(std::string downloadUrl);
  std::string AzureBlobStorageClient::hmac(std::string key, std::string msg);
};