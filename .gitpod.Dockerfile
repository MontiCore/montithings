FROM gitpod/workspace-full

# Switch into our working directory for building NNG
WORKDIR /home/gitpod/

# Build NNG
RUN git clone https://github.com/nanomsg/nng.git
RUN cd /home/gitpod/nng \
&& git fetch && git fetch --tags \
&& git checkout v1.3.0 \
&& mkdir nngbuild \
&& cd nngbuild \
&& export CMAKE_BUILD_WITH_INSTALL_RPATH=1 \
&& cmake -G Ninja .. \
&& ninja \
&& sudo ninja install package dist \
&& mv nng-v1.3.0.sh /home/gitpod/nng.sh
