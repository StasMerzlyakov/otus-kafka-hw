#!/bin/bash

docker compose -f kafka1.yml up

docker-compose -f kafka1.yml rm -fsv

