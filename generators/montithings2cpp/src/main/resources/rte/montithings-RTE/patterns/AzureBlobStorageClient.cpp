/**
 * Upload code is based on:
 * https://www.petecodes.co.uk/uploading-files-to-azure-blob-storage-using-the-rest-api-and-postman/
 *
 * Download code is based on: https://stuartmccoll.github.io/posts/2020-09-21-azure-storage-api/
 */

#include "AzureBlobStorageClient.h"

bool AzureBlobStorageClient::shouldUpload(std::string json, float maxPortSize)
{
  if (json.size() > maxPortSize)
  {
    return true;
  }

  return false;
}

/**
 * Uploads stringified json to blob storage as a file with name filename
 *
 * blobServiceSasUrl:
 * https://pattern2netmin.blob.core.windows.net/?sv=2021-06-08&ss=bf&srt=o&sp=rwdlactfx&se=2024-01-01T16:10:53Z&st=2023-01-31T08:10:53Z&spr=https,http&sig=rixhY%2FZ1KQB0WzjB807Oi9T2YVA64nladtT2kySH05Y%3D
 *
 * containerName: fileuploads
 */
std::string
AzureBlobStorageClient::upload(std::string json, std::string filename, std::string containername,
                               std::string blobServiceSasUrl)
{
  std::vector<std::string> uploadUrlParts = this->getUploadUrlPart(filename, containername, blobServiceSasUrl);
  std::string hostUrl = uploadUrlParts[0];

  std::cout << "Upload url: " << uploadUrlParts[0] << uploadUrlParts[1] << std::endl;

  httplib::Client2 cli(hostUrl.c_str());

  httplib::Headers headers = {
      {"x-ms-blob-type", "BlockBlob"},
  };

  auto res = cli.Put(uploadUrlParts[1].c_str(), headers, json, "text/plain");

  if (res && (res->status >= 200 && res->status <= 299))
  {
    // Todo: Build the return url by ourself
    // https://pattern2netmin.blob.core.windows.net/fileuploads/test.txt
    return this->getDownloadUrl(filename, containername, blobServiceSasUrl);
  }
  else if (res)
  {
    std::cout << "Status: " << res->status << std::endl;
    std::cout << "Body: " << res->body << std::endl;
  }
  else
  {
    std::cout << "Res is not defined" << std::endl;
  }

  throw std::invalid_argument(
      "Could not upload to blob storage as no success http status code received.");
}

/**
 * Downloads file from downloadUrl and returns file content. Usually file content is stringified
 * json
 */
std::string
AzureBlobStorageClient::download(std::string downloadUrl)
{
  std::vector<std::string> downloadUrlParts = this->getDownloadUrlParts(downloadUrl);
  std::string hostUrl = downloadUrlParts[0];

  std::cout << "Download url: " << downloadUrlParts[0] << downloadUrlParts[1] << std::endl;

  httplib::Client2 cli(hostUrl.c_str());

  httplib::Headers headers = {};

  auto res = cli.Get(downloadUrlParts[1].c_str(), headers);

  if (res && (res->status >= 200 && res->status <= 299))
  {
    return res->body;
  }
  else if (res)
  {
    std::cout << "Status: " << res->status << std::endl;
    std::cout << "Body: " << res->body << std::endl;
  }
  else
  {
    std::cout << "Res is not defined" << std::endl;
  }

  throw std::invalid_argument(
      "Could not download from blob storage as no success http status code received.");
}

std::vector<std::string>
AzureBlobStorageClient::getUploadUrlPart(std::string filename, std::string containername,
                                         std::string blobServiceSasUrl)
{
  std::string delim = "/?";
  std::vector<std::string> parts = this->split(blobServiceSasUrl, delim);
  std::string urlParts[2] = {parts[0], "/" + containername + "/" + filename + "?" + parts[1]};
  std::vector<std::string> v(&urlParts[0], &urlParts[0] + 2);
  return v;
}

std::string
AzureBlobStorageClient::getDownloadUrl(std::string filename, std::string containername,
                                       std::string blobServiceSasUrl)
{
  std::string delim = "/?";
  std::vector<std::string> parts = this->split(blobServiceSasUrl, delim);
  return parts[0] + "/" + containername + "/" + filename;
}

std::vector<std::string>
AzureBlobStorageClient::getDownloadUrlParts(std::string url)
{
  std::string delim = "/";
  std::vector<std::string> parts = this->split(url, delim);
  std::string path = "/" + parts[parts.size() - 3] + "/" + parts[parts.size() - 2] + "/" + parts[parts.size() - 1];
  std::string host = this->split(url, path)[0];
  std::string urlParts[2] = {host, path};
  std::vector<std::string> v(&urlParts[0], &urlParts[0] + 2);
  return v;
}

std::vector<std::string>
AzureBlobStorageClient::split(std::string str, std::string delim)
{
  std::vector<std::string> seglist;
  size_t last = 0;
  size_t next = 0;

  while ((next = str.find(delim, last)) != std::string::npos)
  {
    seglist.push_back(str.substr(last, next - last));
    last = next + delim.size();
  }

  seglist.push_back(str.substr(last));

  return seglist;
}
