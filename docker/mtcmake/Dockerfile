FROM debian:buster

RUN apt-get update && apt-get install -y --no-install-recommends \
	git \
	g++ \
	ninja-build \
	cmake \
	make \
	mosquitto \
	mosquitto-dev

RUN apt-get install --reinstall ca-certificates

# Switch into our working directory for building nng
WORKDIR /usr/src/app

# build nng
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

