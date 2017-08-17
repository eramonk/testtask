#!/usr/bin/env bash

docker exec -i $1 bash <<EOF
chef-client --local-mode /home/$2
exit
EOF