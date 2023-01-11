# terraform-deployer

## Setup

```bash
# Install dependencies
pip install -r requirements.txt

# Run server
uvicorn app.main:app --reload

# Check API is working
curl -XGET 'http:/localhost:8000'
```

## Build with Docker

```bash
# Build image
docker build -t terraform-deployer .

# Start container
docker run -d --name terraform-deployer -p 8000:8000 terraform-deployer
```

## Credentials

Firstly, login to the Azure CLI using:

```bash
az login
```

Once logged in - it's possible to list the Subscriptions associated with the account via:

```bash
az account list
```

The output (similar to below) will display one or more Subscriptions - with the `id` field being the `subscription_id` field.

```json
[
  {
    "cloudName": "AzureCloud",
    "id": "00000000-0000-0000-0000-000000000000",
    "isDefault": true,
    "name": "PAYG Subscription",
    "state": "Enabled",
    "tenantId": "00000000-0000-0000-0000-000000000000",
    "user": {
      "name": "user@example.com",
      "type": "user"
    }
  }
]
```

Should you have more than one Subscription, you can specify the Subscription to use via the following command:

```bash
az account set --subscription="SUBSCRIPTION_ID"
```

We can now create the Service Principal which will have permissions to manage resources in the specified Subscription using the following command:

```bash
az ad sp create-for-rbac --role="Contributor" --scopes="/subscriptions/SUBSCRIPTION_ID"
```

This command will output 5 values:

```json
{
  "appId": "00000000-0000-0000-0000-000000000000",
  "displayName": "azure-cli-2017-06-05-10-41-15",
  "name": "http://azure-cli-2017-06-05-10-41-15",
  "password": "0000-0000-0000-0000-000000000000",
  "tenant": "00000000-0000-0000-0000-000000000000"
}
```

These values map to the Terraform variables like so:

- `appId` is the `client_id`.
- `password` is the `client_secret`.
- `tenant` is the `tenant_id`.

In order to access the Azure Storage Account programmatically, the following role must be assigned explicitly:

```bash
az role assignment create --role "Storage Blob Data Contributor" --assignee "APP_ID" --subscription "SUBSCRIPTION_ID"
```

## Docs

- API-Docs: http://localhost:8000/docs
- FastAPI: https://fastapi.tiangolo.com/
- Azure Terraform Provider: https://registry.terraform.io/providers/hashicorp/azurerm/latest/docs
- Terraform-Docs: https://developer.hashicorp.com/terraform/docs
