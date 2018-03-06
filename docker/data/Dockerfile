FROM ubuntu:16.04

# RUN apt-get update \
  # && apt-get install -y build-essential curl zsh git

RUN useradd --create-home --shell /bin/bash --groups sudo jenkins

USER jenkins

RUN mkdir /home/jenkins/data

VOLUME /home/jenkins/data
