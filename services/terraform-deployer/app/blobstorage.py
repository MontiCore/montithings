from azure.identity import DefaultAzureCredential
from azure.storage.blob import BlobServiceClient


def upload_blob(
    filepath: str, filename: str, storage_account_name: str, container_name: str
):
    """
    Uploads blob to container of storage account
    :param str filepath: Path where file is stored on local machine
    :param str filename: File name used in Azure Blob Storage
    :param str storage_account_url: https://<storageaccountname>.blob.core.windows.net
    :param str container_name: Contains multiple blobs
    """
    print("Authenticating to Azure blob")
    default_credential = DefaultAzureCredential()

    storage_account_url = _get_storage_account_url(storage_account_name)

    print("Create new blob service client for url", storage_account_url)
    blob_service_client = BlobServiceClient(
        storage_account_url, credential=default_credential
    )

    try:
        print("Create new container with name", container_name)
        blob_service_client.create_container(container_name)
    except Exception:
        # It's okay to fail here, if container already exists
        pass

    blob_client = blob_service_client.get_blob_client(
        container=container_name, blob=filename
    )

    print("Uploading to Azure Storage as blob:" + filename)

    with open(file=filepath, mode="rb") as data:
        blob_client.upload_blob(data, overwrite=True)


def download_blob(
    filepath: str, filename: str, storage_account_name: str, container_name: str
):
    """
    Downloads blob from container of storage account by writing blob content to file at filepath
    :param str filepath: Path where file is stored on local machine
    :param str filename: File name used in Azure Blob Storage
    :param str storage_account_url: https://<storageaccountname>.blob.core.windows.net
    :param str container_name: Contains multiple blobs
    """
    print("Authenticating to Azure blob")
    default_credential = DefaultAzureCredential()

    storage_account_url = _get_storage_account_url(storage_account_name)

    print("Create new blob service client for url", storage_account_url)
    blob_service_client = BlobServiceClient(
        storage_account_url, credential=default_credential
    )

    try:
        container_client = blob_service_client.get_container_client(
            container=container_name
        )

        print("Downloading blob to:" + filepath)

        with open(file=filepath, mode="wb") as download_file:
            download_file.write(container_client.download_blob(filename).readall())
    except Exception:
        # It's okay to fail here, if tfstate does not exist
        pass


def _get_storage_account_url(storage_account_name: str):
    return f"https://{storage_account_name}.blob.core.windows.net"
