#!/bin/bash
source ./docker.properties
export PROFILE=docker
export BROWSER=chrome
export PREFIX="${IMAGE_PREFIX}"
export ALLURE_DOCKER_API=http://allure:5050/
export HEAD_COMMIT_MESSAGE="local build"
export FRONT_VERSION="2.1.0"
export COMPOSE_PROFILES=test
export ARCH=$(uname -m)

echo '### BROWSER ###'
echo $BROWSER

echo '### Java version ###'
java --version

if [[ "$1" = "gql" ]]; then
  export FRONT="niffler-ng-gql-client"
else
  export FRONT="niffler-ng-client"
fi

docker compose down

docker_containers=$(docker ps -a -q)
docker_images=$(docker images --format '{{.Repository}}:{{.Tag}}' | grep 'niffler')

if [ ! -z "$docker_containers" ]; then
  echo "### Stop containers: $docker_containers ###"
  docker stop $docker_containers
  docker rm $docker_containers
fi

if [ ! -z "$docker_images" ]; then
  echo "### Remove images: $docker_images ###"
  docker rmi $docker_images
fi

bash ./gradlew clean
bash ./gradlew jibDockerBuild -x :niffler-e-2-e-tests:test

if [ "$1" = "firefox" ]; then
docker pull selenoid/vnc_firefox:125.0
else
docker pull selenoid/vnc_chrome:125.0
fi

docker compose up -d
docker ps -a
