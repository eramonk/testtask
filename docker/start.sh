#j!/bin/sh
echo "COMPOSE_PROJECT_NAME=$1" > .env
docker-compose up -d --build
