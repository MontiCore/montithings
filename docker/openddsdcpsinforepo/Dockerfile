FROM ubuntu:groovy AS build

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

# Switch into our working directory for building opendds
WORKDIR /usr/src/app

# build opendds
RUN git clone https://github.com/objectcomputing/OpenDDS.git opendds
RUN cd /usr/src/app/opendds \
&& ./configure --static \
&& make 

FROM ubuntu:groovy

WORKDIR /usr/src/app

COPY entrypoint.sh .
COPY --from=build /usr/src/app/opendds/bin/DCPSInfoRepo .

# Run our binary on container startup
ENTRYPOINT [ "sh", "entrypoint.sh" ]
CMD [ "/bin/sh" ]

