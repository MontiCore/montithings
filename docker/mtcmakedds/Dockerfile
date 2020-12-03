FROM ubuntu:groovy

RUN apt-get update && apt-get install -y \
	git \
	g++ \
	ninja-build \
	cmake \
	make \
	mosquitto \
	mosquitto-dev \
	# required by OpenDDS
	curl \
	libxerces-c-dev \
    libssl-dev \
    perl-base \
    perl-modules

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

# Switch into our working directory for building opendds
WORKDIR /usr/src/app

# build opendds
RUN git clone https://github.com/objectcomputing/OpenDDS.git opendds
RUN cd /usr/src/app/opendds \
&& ./configure --static \
&& make \
&& ldconfig \
&& . /usr/src/app/opendds/setenv.sh

ENV ACE_ROOT=/usr/src/app/opendds/ACE_wrappers \
    TAO_ROOT=/usr/src/app/opendds/ACE_wrappers/TAO \
    DDS_ROOT=/usr/src/app/opendds \
    CMAKE_PREFIX_PATH=$CMAKE_PREFIX_PATH:/usr/src/app/opendds/cmake \
    MPC_ROOT=/usr/src/app/opendds/OpenDDS/ACE_wrappers/MPC \
	PATH=".:/usr/src/app/opendds/ACE_wrappers/bin:/usr/src/app/opendds/opendds/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"

# Switch into our apps working directory
WORKDIR /usr/src/app

CMD [ "/bin/sh" ]

