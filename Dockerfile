FROM jenkins/jenkins:lts-alpine

MAINTAINER YANTRASHALA

# define env variables
ENV JENKINS_REF="/usr/share/jenkins/ref/init.groovy.d/"

# define JVM options
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false \
              -Duser.timezone=Europe/Paris
              
USER jenkins
 
# install jenkins plugins
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

# copy jenkins shared configuration
COPY init.groovy.d/ $JENKINS_REF
