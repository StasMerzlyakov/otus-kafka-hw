#!/bin/bash
pushd kafdrop
java --add-opens=java.base/sun.nio.ch=ALL-UNNAMED \
    -jar kafdrop-3.28.0-SNAPSHOT.jar \
    --kafka.brokerConnect=localhost:9092

popd
