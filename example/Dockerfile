# The Dockerfile is auto-generated with Dokker.
FROM ubuntu:latest
ARG platform
LABEL maintainer="viktor@tiulp.in"
USER root
ENV PYTHON_VERSION="3.9.6"
COPY .python_version /tmp
RUN DEBIAN_FRONTEND=noninteractive apt-get update && \
    apt-get install -y build-essential python3 python3-pip && \
    apt-get autoremove -y && apt-get clean && rm -r /var/cache/apt /var/lib/apt/ && \
    echo ${PYTHON_VERSION}
EXPOSE 8080/tcp
WORKDIR /
ENTRYPOINT ["python3"]
# We did it!
