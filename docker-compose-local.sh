#!/bin/bash
source ./docker.properties
docker stop $(docker ps -a -q)

docker-compose -f docker-compose.local.yml up -d
