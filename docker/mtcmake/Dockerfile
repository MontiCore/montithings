# (c) https://github.com/MontiCore/monticore 
FROM alpine

RUN apk add --update-cache --force-overwrite \
	git \
	g++ \
	ninja \
	cmake \
	make \
	mosquitto \
	mosquitto-dev \
	protobuf-dev \
	py3-protobuf \
	libpq-dev \
	libressl-dev

# Switch into our working directory for building NNG
WORKDIR /usr/src/app

# Build NNG
RUN git clone https://github.com/nanomsg/nng.git
RUN cd /usr/src/app/nng \
&& git fetch && git fetch --tags \
&& git checkout v1.3.0 \
&& mkdir nngbuild \
&& cd nngbuild \
&& export CMAKE_BUILD_WITH_INSTALL_RPATH=1 \
&& cmake -G Ninja .. \
&& ninja \
&& ninja install \
&& cd /usr/src/app \
&& rm -rf /usr/src/app/nng

# Switch into our apps working directory
WORKDIR /usr/src/app

CMD [ "/bin/sh" ]
