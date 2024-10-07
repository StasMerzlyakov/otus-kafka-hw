#!/bin/bash

# download and unpack
wget -N https://downloads.apache.org/kafka/3.8.0/kafka_2.12-3.8.0.tgz
if [[ ! -d "./kafka" ]]; then
	tar xvfz kafka_2.12-3.8.0.tgz && mv ./kafka_2.12-3.8.0 ./kafka
fi
