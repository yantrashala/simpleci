FROM jenkins/jenkins:lts

MAINTAINER Nidhi Bansal (nbansal16@sapient.com)

# define env variables
ENV JENKINS_REF="/usr/share/jenkins/ref"

# define JVM options
ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false \
              -Duser.timezone=Europe/Paris
              
USER jenkins
 
# COPY security.groovy /usr/share/jenkins/ref/init.groovy.d/security.groovy

# install jenkins plugins
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt

# copy jenkins shared configuration
COPY init.color.d/ $JENKINS_REF
