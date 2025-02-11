#!/bin/bash
source ./docker.properties

export PROFILE=docker
export BROWSER=${BROWSER:-chrome}
export PREFIX="${IMAGE_PREFIX}"
export ALLURE_DOCKER_API=http://allure:5050/
export HEAD_COMMIT_MESSAGE="local build"
export FRONT_VERSION="2.1.0"
export COMPOSE_PROFILES=test
export ARCH=$(uname -m)

echo "### BROWSER: $BROWSER ###"
if [ "$1" = "firefox" ]; then
  export BROWSER="firefox"
fi

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

compose_images=$(grep -E 'image:|build:' docker-compose.yml | grep 'niffler' | sed -E 's/.*image:\s*"?([^"]+)"?/\1/' | sed -E 's|.*/||')

dockerfile_services=$(grep -B 1 'build:' docker-compose.yml | grep 'image:' | sed -E 's/.*image:\s*"?([^"]+)"?/\1/' | sed -E 's|.*/||')

echo "### Docker compose images: $compose_images ###"
echo "### Images that build within dockerfile: $dockerfile_services ###"

if [ ! -z "$docker_containers" ]; then
  docker stop $docker_containers
  docker rm $docker_containers
fi

for image in $compose_images; do
  if ! echo "$docker_images" | grep -q "$image"; then
    echo "### Image $image is not found ###"

    if echo "$dockerfile_services" | grep -q "$image"; then
      echo "### image $image build with docker file ###"
      continue
    else
      echo "### image $image build with jib task ###"

      MODULE_NAME=$(echo "$image" | sed -E 's|-docker||' | sed -E 's|:[^:]*$||')
      ./gradlew ":$MODULE_NAME:clean"
      ./gradlew ":$MODULE_NAME:jibDockerBuild"
    fi
  else
    echo "###  $image is existed ###"
  fi
done

if [ "$BROWSER" = "firefox" ]; then
  docker pull selenoid/vnc_firefox:125.0
else
  docker pull selenoid/vnc_chrome:127.0
fi

docker compose up -d
docker ps -a
