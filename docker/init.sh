#!/usr/bin/env bash


chef-client --local-mode /home/webserver.rb
java -cp "app/*" org.ra.Server