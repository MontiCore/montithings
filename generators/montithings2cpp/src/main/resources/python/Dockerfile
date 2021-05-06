# (c) https://github.com/MontiCore/monticore

#Usage example:
#docker build -t pythontest --build-arg PYTHONFILE=hwc/hierarchy/SourceSensorPort.py --build-arg MTPYTHON=python/montithingsconnector.py -f python/Dockerfile .

FROM python:3.7-alpine as base

FROM base as builder
RUN mkdir /install
WORKDIR /install
ADD python/requirements.txt /requirements.txt
RUN pip install --prefix="/install" -r /requirements.txt

FROM base
ARG MTPYTHON
ARG PYTHONFILE
COPY --from=builder /install /usr/local
ADD ${MTPYTHON} /app/montithingsconnector.py
ADD ${PYTHONFILE} /app/script.py

WORKDIR /app
CMD ["python", "script.py"]
