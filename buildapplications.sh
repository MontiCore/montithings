#! /bin/bash

# Builds all targets into docker containers
# All subdirectories containing . are considered a component
dirs=$(find applications -mindepth 1 -maxdepth 1 -type d);
for d in $dirs; do
    # ignore hidden directories like .settings
    if [[ -d "${d}" && "${d}" =~ ^applications/\. ]] ; then
        echo "${d} is a hidden folder, ignoring..."
        continue
    fi
    echo "Generating ${d}..."

    cd $d
    docker run --rm -v $PWD:$PWD -v $PWD/.m2:/root/.m2 -w $PWD maven:3-jdk-11 mvn clean install
    
    echo "Building ${d}..."

    registry=$CI_REGISTRY_IMAGE
    imagename=$(basename $d)
    imageref="${registry}/${imagename}"
    
    echo "Registry: ${registry}"
    echo "Image name: ${imagename}"
    echo "Full image ref: ${imageref}"

    docker buildx build --platform linux/amd64,linux/arm64,linux/arm/v7,linux/arm/v6 --output type=image,name=$imageref -t $imageref:latest target/generated-sources --push .


    cd -
done
