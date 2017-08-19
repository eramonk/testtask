#!/usr/bin/env bash

java -cp "*" org.ra.Server
chef-client --local-mode /home/webserver.rb