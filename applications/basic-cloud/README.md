<!-- (c) https://github.com/MontiCore/monticore -->

# Basic Cloud

This example shows how to integrate Montithings with the cloud. The `Example` component contains two subcomponents. The `Source` component produces values, the `Rest` component consumes these values as well as translates the text `Hallo` from German to English using Azure Translation Service.

Each component may define required cloud services via `Terraform`. To do so, a `<componentName>.tf` file with the same name as the component using those resources is placed in the `hwc` directory. In this application the `Example` component defines a `Example.tf` file requesting the Azure Translation Service.

Every Terraform output defined in the Terraform file is fed back into the Docker container as an environment variable on deployment. For example, the before mentioned translation service requires an API access key. This key is only available after the service was requested and before the component is written. Thus, the output `primarykey` key is defined in the Terraform file and the `RestImpl.cpp` expects a `stdenv` with the name `primarykey` at runtime.

To deploy the application use the target provider `AZURECLOUD`.
