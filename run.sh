#!/usr/bin/env bash -eu
set -e
docker-compose stop && docker-compose rm -f && docker-compose -f docker-compose.yml up --no-recreate --abort-on-container-exit
#docker-compose -f docker-compose.yml up
#docker-compose down --remove-orphans
#exitcode=$(docker inspect -f '{{ .State.ExitCode }}' test_app)
#if [[ $exitcode != 0 ]]; then exit $exitcode; fi