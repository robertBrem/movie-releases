FROM jboss/keycloak-adapter-wildfly:2.4.0.Final

MAINTAINER Robert Brem <brem_robert@hotmail.com>

ENV DEPLOYMENT_DIR ${JBOSS_HOME}/standalone/deployments/

WORKDIR ${JBOSS_HOME}
RUN curl -O https://jdbc.postgresql.org/download/postgresql-9.4.1212.jar
RUN mv postgresql-9.4.1212.jar postgresql.jar
USER root
ADD start.sh ${JBOSS_HOME}
RUN chown jboss:jboss start.sh
ADD add-datasource.sh ${JBOSS_HOME}
RUN chown jboss:jboss add-datasource.sh
USER jboss

ADD target/releases.war ${JBOSS_HOME}

CMD ${JBOSS_HOME}/start.sh