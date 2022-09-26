# (c) https://github.com/MontiCore/monticore
FROM alpine

RUN apk add --update-cache \
	git \
	g++ \
	ninja \
	cmake \
	make \
	mosquitto \
	mosquitto-dev \
	protobuf-dev \
	py3-protobuf

# Switch into our working directory for building NNG
WORKDIR /usr/src/app

# Build NNG
RUN git clone https://github.com/nanomsg/nng.git
WORKDIR /usr/src/app/nng 
RUN git fetch 
RUN git fetch --tags 
RUN git checkout v1.3.0 
RUN mkdir nngbuild 
WORKDIR /usr/src/app/nng/nngbuild 
RUN export CMAKE_BUILD_WITH_INSTALL_RPATH=1 
RUN cmake -G Ninja .. 
RUN ninja
RUN ninja install package dist 
RUN mv nng-v1.3.0.sh /usr/src/app/nng.sh 

# Switch into our apps working directory
WORKDIR cd /usr/src/app 

# Remove NNG sources
RUN rm -rf /usr/src/app/nng

CMD [ "/bin/sh" ]
