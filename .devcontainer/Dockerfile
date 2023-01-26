FROM ubuntu

# install prerequisites
RUN apt-get update
RUN apt-get -y install git
RUN apt -y install sudo
RUN sudo apt-get -y install curl

# set file encoding correctly
RUN sudo apt -y install locales && locale-gen en_US.UTF-8
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8


# install montithings
RUN git clone https://github.com/julius-gummersbach/montithings.git # todo use official url
WORKDIR "/montithings"
RUN SKIPDOCKER=1 SKIP_MVN=1 ./installLinux.sh
RUN mvn clean install -Dexec.skip -Dmaven.test.skip=true
