FROM alpine

RUN apk add --update-cache \
	git \
	g++ \
	ninja \
	cmake \
	make \
	mosquitto \
	mosquitto-dev

# Switch into our working directory for building nng
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
&& ninja install package dist \
&& mv nng-v1.3.0.sh /usr/src/app/nng.sh \
&& cd /usr/src/app \
&& rm -rf /usr/src/app/nng

# Switch into our apps working directory
WORKDIR /usr/src/app

CMD [ "/bin/sh" ]

