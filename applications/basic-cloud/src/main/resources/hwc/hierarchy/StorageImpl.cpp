// (c) https://github.com/MontiCore/monticore
#include "StorageImpl.h"
#include <iostream>
#include <was/storage_account.h> 
#include <was/blob.h>

namespace montithings {
namespace hierarchy {

StorageResult
StorageImpl::getInitialValues ()
{
  return {};
}

StorageResult
StorageImpl::compute (StorageInput input)
{
    using namespace azure::storage;

    // Initialize storage account
    cloud_storage_account storage_account = cloud_storage_account::parse(_XPLATSTR("DefaultEndpointsProtocol=https;AccountName=montithings;AccountKey=gPt9YsGjMO7DR1nl2XDpd209ErKs9eQSn195ww6rw6JVayz0AiWHrWLfu1qI12tWFlMSinHNaaJPhIO6DXVDiQ==;EndpointSuffix=core.windows.net"));

    // Create a blob container
    cloud_blob_client blob_client = storage_account.create_cloud_blob_client();
    cloud_blob_container container = blob_client.get_container_reference(_XPLATSTR("montithings"));

    // Return value is true if the container did not exist and was successfully created.
    container.create_if_not_exists();

    // List blobs in the blob container
    continuation_token token;
    do
    {
        list_blob_item_segment result = container.list_blobs_segmented(token);
        for (auto& item : result.results())
        {
            if (item.is_blob())
            {
                ucout << _XPLATSTR("Blob: ") << item.as_blob().uri().primary_uri().to_string() << std::endl;

                // Download to folder of binary
                item.as_blob().download_to_file(item.as_blob().name());
            }
            else
            {
                ucout << _XPLATSTR("Directory: ") << item.as_directory().uri().primary_uri().to_string() << std::endl;
            }
        }
        token = result.continuation_token();
    }
    while (!token.empty());

  return {};
}

}}