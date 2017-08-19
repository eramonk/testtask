# todo

запуск связки контейнеров из директории docker/

./start.sh {prefix name} {PORT}

команды управлея связками

docker-compose -p {prefix name} stop

docker-compose -p {prefix name} start

docker-compose -p {prefix name} down (остановить и удалить)

test elastic => http://localhost:{PORT}/testelastic

test redis => http://localhost:{PORT}/testredis,

test redis with count => http://localhost:{PORT}/retest

в контейнер {prefix name}_myapp_1 биндится папка с рецептом webserver.rb который устанавливает и запускает
сервер Apache. Рецепт запускается при создании контейнера с помощью скрипта init.sh.

команда применения рецепта внутри запущенного контейнера

./chef/chef-client.sh {prefix name}_myapp_1 {recipe}

test apache => http://localhost:8088/








