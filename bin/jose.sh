#!/usr/bin/env bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"

java -jar ${DIR}/jose-jwkset-cli.jar -Dspring.profiles.active=cli -Dspring.main.banner-mode=off $@