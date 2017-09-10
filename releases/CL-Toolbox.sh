#!/bin/bash
SCRIPTPATH="$( cd "$(dirname "$0")" ; pwd -P )"
java -Dfile.encoding="UTF-8" -jar "$SCRIPTPATH/CL-Toolbox.jar" $@