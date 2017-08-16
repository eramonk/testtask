#!/usr/bin/env bash
#!/bin/sh
echo "COMPOSE_PROJECT_NAME=$1" > .env
echo "PORT=$2" >> .env

docker-compose up -d --build
