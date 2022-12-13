FROM python:3.10

# Install terraform
ARG TERRAFORM_VERSION=0.13.5
RUN apt-get update -y 
RUN apt-get install unzip wget -y
RUN wget --progress=dot:mega https://releases.hashicorp.com/terraform/${TERRAFORM_VERSION}/terraform_${TERRAFORM_VERSION}_linux_amd64.zip
RUN unzip terraform_${TERRAFORM_VERSION}_linux_amd64.zip
RUN mv terraform /usr/local/bin/ 
RUN chmod +x /usr/local/bin/terraform 
RUN terraform --version

# App setup
WORKDIR /code
COPY ./requirements.txt /code/requirements.txt
RUN pip install --no-cache-dir --upgrade -r /code/requirements.txt
COPY ./app /code/app
ENV PYTHONPATH "${PYTHONPATH}:./app"

EXPOSE 8000

CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]