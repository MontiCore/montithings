from typing import List, Union

from pydantic import BaseModel


class TerraformFileInfo(BaseModel):
    filename: str
    filecontent: str  # base64 encoded string


# Ref: https://registry.terraform.io/providers/hashicorp/azurerm/latest/docs/guides/service_principal_client_secret
class TerraformCredentials(BaseModel):
    clientId: str
    clientSecret: str
    subscriptionId: str
    tenantId: str


class TerraformBody(BaseModel):
    files: List[TerraformFileInfo]
    credentials: TerraformCredentials
    storageAccountName: str
    tfstate: Union[str, None] = None  # optional base64 encoded state file
