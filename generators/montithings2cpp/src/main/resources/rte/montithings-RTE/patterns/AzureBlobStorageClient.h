#pragma once

#include <string>
#include <vector>
#include <sstream>

class AzureBlobStorageClient
{
protected:
  bool shouldUpload(std::string json, float maxPortSize);
  std::string upload(std::string json, std::string filename, std::string containername, std::string containerUrl);
  std::string download(std::string downloadUrl);
  std::string getUploadUrl(std::string filename, std::string containername, std::string blobServiceSasUrl);
  std::string getDownloadUrl(std::string filename, std::string containername, std::string blobServiceSasUrl);
  std::vector<std::string> split(std::string str, std::string delim);
};