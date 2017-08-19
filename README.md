# todo

запуск кластера контейнеров из директории docker/

./start.sh {prefix name} {PORT}

управление кластером

docker-compose -p {prefix name} stop

docker-compose -p {prefix name} start

docker-compose -p {prefix name} down (остановить и удалить)

docker-compose -p {prefix name} down -v (остановить и удалить вместе с данными)

test elastic => http://localhost:{PORT}/testelastic

test redis => http://localhost:{PORT}/testredis,

test redis with count => http://localhost:{PORT}/retest

в контейнер {prefix name}_myapp_1 биндится папка с рецептом webserver.rb который устанавливает и запускает
сервер Apache. Рецепт запускается при создании контейнера скриптом init.sh.

test apache =>

./docker/testapache.sh {prefix name}

применение рецепта внутри запущенного контейнера

./chef/chef-client.sh {prefix name}_myapp_1 {recipe}










