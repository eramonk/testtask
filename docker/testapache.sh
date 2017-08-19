#!/usr/bin/env bash

docker exec -i $1_myapp_1 bash <<EOF
curl localhost
exit
EOF