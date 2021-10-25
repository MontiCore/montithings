FROM montithings/mtcmakedds AS build

FROM ubuntu:groovy

WORKDIR /usr/src/app

COPY entrypoint.sh .
COPY --from=build /usr/src/app/opendds/bin/DCPSInfoRepo .

# Run our binary on container startup
ENTRYPOINT [ "sh", "entrypoint.sh" ]
CMD [ "/bin/sh" ]

