#!/usr/bin/env bash

case $1 in
  prod)
    CONFIG_FILE=src/main/resources/production.config
    ;;
  beta)
    CONFIG_FILE=src/main/resources/beta.config
    ;;
  dev)
    CONFIG_FILE=src/main/resources/development.config
    ;;
  test)
    CONFIG_FILE=src/main/resources/test.config
    ;;
  *)
    echo 'usage: run-fleet.sh {prod|beta|dev|test}'
    exit 1
    ;;
esac

sbt "run --configFile ${CONFIG_FILE}"
