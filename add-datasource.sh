#!/bin/bash

$JBOSS_HOME/bin/jboss-cli.sh --connect <<EOF
batch
deploy ${JBOSS_HOME}/postgresql.jar
data-source add --jndi-name=java:jboss/jdbc/movies --name=Movies --connection-url=jdbc:postgresql://${SERVICE_HOST}:${SERVICE_PORT}/movies --driver-name=postgresql.jar --user-name=${DB_USERNAME} --password=${DB_PASSWORD}
run-batch
exit
EOF