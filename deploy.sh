#! /bin/bash
set -e

FAT_JAR=target/scala-2.11/scalex-assembly-3.0-SNAPSHOT.jar dest=~ec2-user/scalex-assembly-3.0-SNAPSHOT.jar

sbt assembly

java -Dscala.usejavacp=true -jar $FAT_JAR index \
    --name scalex --version 3.0.0 --directory src/main/scala

ansible-playbook ansible/update.yml "$@"
