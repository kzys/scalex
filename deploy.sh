#! /bin/bash
set -e
sbt assembly
ansible-playbook ansible/update.yml "$@"
