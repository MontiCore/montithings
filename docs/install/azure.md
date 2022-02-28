## Installation (using Microsoft Azure)

### Is this the right installation type for you?
In case you do not want to install MontiThings on your own machine, you can try
MontiThings in a virtual machine provided by Microsoft Azure.
**Note: Costs may be incurred in the process! Use at your own responsibility!**
This guide is based on the guide from the
[Microsoft Azure Docs][azure-terraform-docs], but adapted for MontiThings.

### Prerequisites
* [Microsoft Azure CLI][azure-cli]
* [Terraform][terraform-cli]
* An SSH key (by default it is expected at `~/.ssh/id_rsa.pub`)

### Installation
First you need to log into Azure and initialize Terraform to make sure
everything is correctly installed and setup:
```
az login
terraform init
```

Then you can plan your deployment, i.e. dry-run it and get a preview of what
Terraform will actually do.
These will be used by the virtual machine to download and install MontiThings.
```
terraform plan -out terraform_azure.tfplan
```

If your SSH key is not at `~/.ssh/id_rsa`, please provide its location as an
argument.
```
terraform plan -out terraform_azure.tfplan -var 'rsa_key_location=/path/to/id_rsa'
```

Here, make sure that you're happy with all the services Terraform will install.
If you want to know more about the individual services, refer to the excellent
documentation from the [Microsoft Azure Docs][azure-terraform-docs].

If you're happy, deploy the virtual machine by calling:
```
terraform apply terraform_azure.tfplan
```

You will see how Terraform first instantiates the virtual machine and then
installs MontiThings on this machine.
At the end, the script shows you the virtual machine's IP.
In case you forget it, you can find out the virtual machine's IP address by calling:
```
az vm show --resource-group montithingsResourceGroup --name montithings -d --query [publicIps] -o tsv
```

To connect to the machine, call:
```
ssh azureuser@20.30.40.50
              ^---------^
    replace this with the IP that was output by the script
```

After the installation you can use MontiThings as if it was installed using
a native installation.
For example, you can follow the "Building and Running Your First Application"
tutorial below.

When you are done, you can instruct Terraform to destroy all resources so that
no further costs are incurred:
```
terraform destroy
```

Double check that everything was correctly deleted in your Azure account just to
make sure no further costs are incurred.

[se-rwth]: http://www.se-rwth.de
[montiarc]: https://www.se-rwth.de/topics/Software-Architecture.php
[nng]: https://github.com/nanomsg/nng#quick-start
[nng-1.3]: https://github.com/nanomsg/nng/archive/v1.3.0.zip
[docker]: https://www.docker.com/products/docker-desktop
[visualstudio]: https://visualstudio.microsoft.com/vs/community/
[mosquitto]: https://mosquitto.org/download/
[opendds]: https://opendds.org/downloads.html
[elf]: https://en.wikipedia.org/wiki/Executable_and_Linkable_Format
[mach-o]: https://en.wikipedia.org/wiki/Mach-O
[portable-executable]: https://en.wikipedia.org/wiki/Portable_Executable
[password]: https://git.rwth-aachen.de/profile/password/edit
[clion]: https://www.jetbrains.com/clion
[azure-cli]: https://docs.microsoft.com/en-us/cli/azure/install-azure-cli
[terraform-cli]: https://www.terraform.io/downloads.html
[azure-terraform-docs]: https://docs.microsoft.com/en-us/azure/developer/terraform/create-linux-virtual-machine-with-infrastructure
[python]: https://www.python.org/
[pip]: https://pypi.org/project/pip/
[paho-mqtt]: https://pypi.org/project/paho-mqtt/