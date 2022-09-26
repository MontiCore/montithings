FROM debian:buster

RUN apt-get update && apt-get install -y \
	git \
	g++ \
	ninja-build \
	cmake \
	make \
	mosquitto \
	mosquitto-dev \
	cpio \
    protobuf-compiler \
	libprotobuf-dev \
	python3-protobuf \
	# required by OpenDDS
	curl \
	libxerces-c-dev \
    libssl-dev \
    perl-base \
    perl-modules

RUN apt-get install --reinstall ca-certificates

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

# Switch into our working directory for building opendds
WORKDIR /usr/src/app

# Remove NNG sources
RUN rm -rf /usr/src/app/nng

# build opendds
RUN git clone https://github.com/objectcomputing/OpenDDS.git opendds
WORKDIR /usr/src/app/opendds 
RUN git fetch && git fetch --tags 
RUN git checkout DDS-3.15 
RUN ./configure --static 
RUN make -j$(nproc) 
RUN ldconfig 
RUN . /usr/src/app/opendds/setenv.sh

ENV ACE_ROOT=/usr/src/app/opendds/ACE_wrappers \
    TAO_ROOT=/usr/src/app/opendds/ACE_wrappers/TAO \
    DDS_ROOT=/usr/src/app/opendds \
    CMAKE_PREFIX_PATH=$CMAKE_PREFIX_PATH:/usr/src/app/opendds/cmake \
    MPC_ROOT=/usr/src/app/opendds/OpenDDS/ACE_wrappers/MPC \
	PATH=".:/usr/src/app/opendds/ACE_wrappers/bin:/usr/src/app/opendds/opendds/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"

# Switch into our apps working directory
WORKDIR /usr/src/app

CMD [ "/bin/sh" ]

