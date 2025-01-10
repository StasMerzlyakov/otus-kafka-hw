#!/bin/bash

# pushd
# ../../test_app
# ./gradlew buildJar
# popd
#../../test_app/build/libs/test_app-1.0.jar ./

pushd testapp
java -jar test_app-1.0.jar

popd


