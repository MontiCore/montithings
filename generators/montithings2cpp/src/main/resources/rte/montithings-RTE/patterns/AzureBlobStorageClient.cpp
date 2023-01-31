#include "AzureBlobStorageClient.h"
#include "cpp-httplib/httplib.h"

bool AzureBlobStorageClient::shouldUpload(std::string json, float maxPortSize)
{
  if (json.size() > maxPortSize)
  {
    return true;
  }

  return false;
}

// Tutorial this code is based on: https://www.petecodes.co.uk/uploading-files-to-azure-blob-storage-using-the-rest-api-and-postman/
//
// blobServiceSasUrl: https://pattern2netmin.blob.core.windows.net/?sv=2021-06-08&ss=bf&srt=o&sp=rwdlactfx&se=2024-01-01T16:10:53Z&st=2023-01-31T08:10:53Z&spr=https,http&sig=rixhY%2FZ1KQB0WzjB807Oi9T2YVA64nladtT2kySH05Y%3D
//
// containerName: fileuploads
//

/**
 * Uploads stringified json to blob storage as a file with name filename
 */
std::string AzureBlobStorageClient::upload(std::string json, std::string filename, std::string containername, std::string blobServiceSasUrl)
{
  std::string uploadUrl = this->getUploadUrl(filename, containername, blobServiceSasUrl);
  httplib::Client2 cli(uploadUrl.c_str());

  httplib::Headers headers = {
      {"x-ms-blob-type", "BlockBlob"},
  };

  auto res = cli.Put("", headers, json, "text/plain");

  if (res && (res->status >= 200 || res->status <= 299))
  {
    // Todo: Build the return url by ourself
    // https://pattern2netmin.blob.core.windows.net/fileuploads/test.txt
    return this->getDownloadUrl(filename, containername, blobServiceSasUrl);
  }

  throw std::invalid_argument("Could not upload to blob storage as no success http status code received.");
}

/**
 * Downloads file from downloadUrl and returns file content. Usually file content is stringified json
 */
std::string AzureBlobStorageClient::download(std::string downloadUrl)
{
  // TODO: Implement download blob
}

std::string AzureBlobStorageClient::getUploadUrl(std::string filename, std::string containername, std::string blobServiceSasUrl)
{
  std::string delim = "/?";
  std::vector<std::string> parts = this->split(blobServiceSasUrl, delim);
  return parts[0] + "/" + containername + "/" + filename + delim + parts[1];
}

std::string AzureBlobStorageClient::getDownloadUrl(std::string filename, std::string containername, std::string blobServiceSasUrl)
{
  std::string delim = "/?";
  std::vector<std::string> parts = this->split(blobServiceSasUrl, delim);
  return parts[0] + "/" + containername + "/" + filename;
}

std::vector<std::string> AzureBlobStorageClient::split(std::string str, std::string delim)
{
  std::vector<std::string> seglist;
  size_t last = 0;
  size_t next = 0;

  while ((next = str.find(delim, last)) != std::string::npos)
  {
    seglist.push_back(str.substr(last, next - last));
    last = next + 1;
  }

  seglist.push_back(str.substr(last));

  return seglist;
}