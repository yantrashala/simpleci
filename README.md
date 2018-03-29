# simpleci
Jenkins based CI tool with plugins and configuration

## Pre-requisites:
Docker and Docker-compose should be installed on your host machine.

### Hierarchy

Dockerfile: Dockerfile used to build image

plugins.txt: jenkins plugins installed to the docker image

simpleci.conf: sample configuration file that stores the url, username and passowrds of github and bitbucket.

### Usage

Build image:

```shell
docker build -t yantrashala/simpleci:1.0 .
```

Run image:

```shell
docker run --rm --name simpleci-dev -v ~/opt:/opt -p 8080:8080 -p 50000:50000 yantrashala/simpleci:1.0
```

Access 
```shell
  http://<docker IP>:8888/
```

Host Machine Should have docker:

Update below line in your DOCKER configuration file, This is one time activity. 
DOCKER_OPTS="-H tcp://0.0.0.0:2376 -H unix:///var/run/docker.sock"
docker slave plugin reference - https://wiki.jenkins.io/display/JENKINS/Docker+Plugin 
