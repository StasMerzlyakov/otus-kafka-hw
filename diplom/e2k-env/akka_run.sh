#!/bin/bash

#
# pushd ../akka-app/
# sbt assembly
# popd 
# cp ../akka-app/target/scala-2.13/akka-assembly-0.1.ja
#

pushd akka
java -jar akka-assembly-0.1.jar
popd


