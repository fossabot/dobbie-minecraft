#!/bin/bash

# Read .gitignore first
# You should run this only if you need to set up the server (/op, /whitelist, etc)
# I couldn't find a simple way to execute console commands from gradle

mkdir run
cd run
java -Dfile.encoding=UTF-8 -Dcom.mojang.eula.agree=true -Xmx1024m -jar ../bukkit.jar