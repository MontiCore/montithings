import os

from base64 import b64decode
from pathlib import Path
from typing import Dict
from fastapi import HTTPException

from app.blobstorage import download_blob, upload_blob

from .library.terraform import IsFlagged, Terraform
from .models import TerraformBody, TerraformFileInfo, TerraformCredentials
from .utils import create_empty_file, rm_files_with_extension, read_file_to_base64

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
    1. Terraform apply requested cloud resources
    2. Upload tfstate to blob storage
    """
    try:
        # Remove tf directory
        rm_files_with_extension(_base_dir, ["tf", "tfstate", "tfstate.backup"])

        # Write files to terraform directory
        for file in body.files:
            _write_tf(file)

        # Optionally write state file if provided
        if body.tfstate is not None:
            _write_tfstate(body.tfstate)

        # Apply tf to provision resources
        out = _exec_tf_apply()

        # Upload tfstate to storage account
        upload_blob(
            filepath=os.path.join(_base_dir, _tfstate_filename),
            filename=_tfstate_filename,
            storage_account_name=body.storageAccountName,
            container_name=_containername,
        )

        envvars = _get_env_vars(out)
        tfstate = read_file_to_base64(os.path.join(_base_dir, _tfstate_filename))

        return {"envvars": envvars, "tfstate": tfstate}

    except Exception as e:
        print(str(e))
        raise HTTPException(status_code=500)


def destroy_tf(body: TerraformBody):
    """
    1. Download tfstate from blob storage
    2. Terraform destroy
    """
    try:
        # Remove tf directory
        rm_files_with_extension(_base_dir, ["tf", "tfstate", "tfstate.backup"])

        # Create empty tfstate file
        create_empty_file(os.path.join(_base_dir, _tfstate_filename))

        # Download tfstate from storage account
        download_blob(
            filepath=os.path.join(_base_dir, _tfstate_filename),
            filename=_tfstate_filename,
            storage_account_name=body.storageAccountName,
            container_name=_containername,
        )

        # Write files to terraform directory
        for file in body.files:
            _write_tf(file)

        # Apply tf destroy to de-provision resources
        _exec_tf_destroy()

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


def _write_tf(fileinfo: TerraformFileInfo):
    """
    Write terraform file to terraform directory
    """
    print(f"Write tf file {fileinfo.filename}")
    content = b64decode(fileinfo.filecontent)
    Path(_base_dir, fileinfo.filename + ".tf").write_bytes(content)


def _write_tfstate(tfstate: str):
    """
    Write tfstate file to terraform directory
    """
    print(f"Write tfstate")
    content = b64decode(tfstate)
    Path(_base_dir, _tfstate_filename).write_bytes(content)


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


def _exec_tf_destroy():
    """
    Runs 'terraform destroy' to destroy cloud resources
    """
    print("Destroy tf")
    tf = Terraform(working_dir=_base_dir, is_env_vars_included=True)
    tf.init()
    return_code, _stdout, _stderr = tf.apply(
        destroy=IsFlagged, skip_plan=True, capture_output=True
    )
    if return_code is not None and return_code > 0:
        raise Exception('Error when running "terraform apply -destroy"')
    print("Successfully destroyed all resources from tf files")
