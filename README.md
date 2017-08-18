# todo

запуск связки контейнеров из директории /todo/docker/ 

./start.sh {prefix name} {PORT}

команды управлея связками

docker-compose -p {prefix name} stop
docker-compose -p {prefix name} start
docker-compose -p {prefix name} down (остановить и удалить)

test elastic => http://localhost:{PORT}/testelastic
test redis => http://localhost:{PORT}/testredis,
test redis with count => http://localhost:{PORT}/retest


команда применения рецепта внутри контейнера

./chef/chef-client.sh {prefix name}_myapp_1 webserver.rb

в контейнер {prefix name}_myapp_1 биндится папка с рецептом webserver.rb которы который устанавливает и запускает
сервер Apache.

test apache => http://localhost:8088/








