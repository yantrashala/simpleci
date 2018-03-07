# simpleci
Jenkins based CI tool with plugins and configuration

### Hierarchy

Dockerfile: Dockerfile used to build image

plugins.txt: jenkins plugins installed to the docker image

### Usage

Build image:

```shell
docker build -t yantrashala/simpleci .
```

Run image:

```shell
docker run --rm --name simpleci-dev -p 8080:8080 -p 50000:50000 yantrashala/simpleci
```
