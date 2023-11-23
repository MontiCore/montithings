# (c) https://github.com/MontiCore/monticore
import os

from base64 import b64decode
from pathlib import Path
from typing import Dict
from fastapi import HTTPException

from app.blobstorage import download_blob, upload_blob

from .library.terraform import IsFlagged, Terraform
from .models import TerraformBody, TerraformFileInfo, TerraformCredentials
from .utils import (
    create_empty_file,
    get_file_size,
    rm_files_with_extension,
    read_file_to_base64,
)

_base_dir = "app/terraform"
_tfstate_filename = "terraform.tfstate"
_containername = "tfstate"


def set_env(credentials: TerraformCredentials):
    """
    Set required environment variables to authenticate against Azure.
    For more info refer to: https://registry.terraform.io/providers/hashicorp/azurerm/latest/docs/guides/service_principal_client_secret
    """
    print("Set azure credentials as env vars")

    # Requires for terraform auth
    os.environ["ARM_CLIENT_ID"] = credentials.clientId
    os.environ["ARM_CLIENT_SECRET"] = credentials.clientSecret
    os.environ["ARM_SUBSCRIPTION_ID"] = credentials.subscriptionId
    os.environ["ARM_TENANT_ID"] = credentials.tenantId

    # Required for storage account auth
    os.environ["AZURE_CLIENT_ID"] = credentials.clientId
    os.environ["AZURE_CLIENT_SECRET"] = credentials.clientSecret
    os.environ["AZURE_SUBSCRIPTION_ID"] = credentials.subscriptionId
    os.environ["AZURE_TENANT_ID"] = credentials.tenantId


def apply_tf(body: TerraformBody):
    """
    1. Download tfstate from blob storage (if exists)
    2. Terraform apply requested cloud resources
    3. Upload tfstate to blob storage
    """
    try:
        if len(body.files) <= 0:
            print("Nothing to apply. Return immediately")
            return {"envvars": {}, "tfstate": ""}

        # Remove tf directory
        rm_files_with_extension(_base_dir, ["tf", "tfstate", "tfstate.backup"])

        tf_state = os.path.join(_base_dir, _tfstate_filename)

        # Create empty tfstate file
        create_empty_file(tf_state)

        # Download tfstate from storage account
        download_blob(
            filepath=tf_state,
            filename=_tfstate_filename,
            storage_account_name=body.storageAccountName,
            container_name=_containername,
        )

        # Remove empty tfstate
        if get_file_size(tf_state) == 0:
            rm_files_with_extension(_base_dir, ["tfstate"])

        # Write files to terraform directory
        for file in body.files:
            _write_tf(file)

        # Apply tf to provision resources
        out = _exec_tf_apply()

        # Upload tfstate to storage account, if base.tf and thus storage account exist
        if _has_base_tf(body):
            upload_blob(
                filepath=tf_state,
                filename=_tfstate_filename,
                storage_account_name=body.storageAccountName,
                container_name=_containername,
            )

        envvars = _get_env_vars(out)
        tfstate = read_file_to_base64(tf_state)

        return {"envvars": envvars, "tfstate": tfstate}

    except Exception as e:
        print(str(e))
        raise HTTPException(status_code=500)


def _get_env_vars(tfout: str | Dict[str, str] | Dict[str, Dict[str, str]] | None):
    """
    Parses terraform output into key-value pairs i.e.
    {'test': {'sensitive': False, 'type': 'string', 'value': 'rg-terraform'}} becomes
    {'test': 'rg-terraform'}
    """
    if tfout is None or isinstance(tfout, str):
        return {}

    env_dict: Dict[str, str] = {}

    for key in tfout.keys():
        val = tfout[key]

        if isinstance(val, str):
            env_dict[key] = val
        else:
            env_dict[key] = val["value"]

    return env_dict


def _has_base_tf(body: TerraformBody):
    """
    Returns, if file with filename 'base.tf' is in body
    """
    for fileinfo in body.files:
        if fileinfo.filename == "base.tf":
            return True
    return False


def _write_tf(fileinfo: TerraformFileInfo):
    """
    Write terraform file to terraform directory
    """
    print(f"Write tf file {fileinfo.filename}")
    content = b64decode(fileinfo.filecontent)
    Path(_base_dir, fileinfo.filename + ".tf").write_bytes(content)


def _exec_tf_apply():
    """
    Runs 'terraform apply  -auto-approve' to provision cloud resources
    """
    print("Apply tf")
    tf = Terraform(working_dir=_base_dir, is_env_vars_included=True)
    tf.init()
    return_code, _stdout, _stderr = tf.apply(skip_plan=True, capture_output=True)
    if return_code is not None and return_code > 0:
        raise Exception('Error when running "terraform apply"')
    out = tf.output()
    print("Successfully applied all tf files")
    return out
