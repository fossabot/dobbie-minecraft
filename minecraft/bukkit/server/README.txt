Place bukkit.jar in this directory.
You can get one by building Spigot using BuildTools: https://www.spigotmc.org/wiki/buildtools/
Just move here the built jar (something named "spigot-blah-blah.jar") and rename it ("bukkit.jar")

I couldn't find a simple way to execute console commands from gradle.
So in order to setup a server (e.g. execute /op, /whitelist, etc), run start_server.[bat/sh] from this directory.

To start a Bukkit server from gradle run:
./gradlew :minecraft:bukkit:startServer
