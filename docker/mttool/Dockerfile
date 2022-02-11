# (c) https://github.com/MontiCore/monticore
FROM ubuntu:latest

ARG DEBIAN_FRONTEND=noninteractive

RUN apt-get update && apt-get install -y \
    wget \
    git \
    sudo \
    software-properties-common  \
    g++ \
    make \
    cmake \
    ninja-build  \
    mosquitto-dev \
    libmosquitto-dev \
    mosquitto-clients \
    mosquitto \
    curl \
    python3 \
    python3-pip \
&& add-apt-repository -y ppa:openjdk-r/ppa \
&& apt-get install -y \
    maven \
    openjdk-11-jdk

# Install Liquidprompt
RUN wget https://raw.githubusercontent.com/dockcross/dockcross/master/imagefiles/install-liquidprompt-binary.sh \
&& chmod +x install-liquidprompt-binary.sh \
&& ./install-liquidprompt-binary.sh \
&& rm install-liquidprompt-binary.sh

# Install MontiThings
ADD ./cli/target/cli-*-cli.jar /root/.montithings/jar/
ADD ./cli/montithings /root/.montithings/montithings
RUN chmod +x $HOME/.montithings/montithings \
&& echo 'export PATH=$PATH:$HOME/.montithings/' >> $HOME/.bashrc

ENTRYPOINT [ "/bin/bash" ]
