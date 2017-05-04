#!/usr/bin/env bash

VERSION=${1:-"1.0.0"}
SLACK_TOKEN=$2
echo "VERSION: $VERSION"
echo "SLACK_TOKEN: ${SLACK_TOKEN}"

docker stop movie-releases && docker rm movie-releases

docker run --name movie-releases -d \
--net movies \
-p 9080:8080 \
-e REALM_NAME=battleapp-local \
-e AUTH_SERVER_URL=http://keycloak:8080/auth \
-e SERVICE_HOST=postgres-movies \
-e SERVICE_PORT=5432 \
-e DB_USERNAME=postgres \
-e DB_PASSWORD=postgres \
-e SLACK_TOKEN=${SLACK_TOKEN} \
disruptor.ninja:30500/robertbrem/movie-releases:$VERSION
