#!/usr/bin/env bash

VERSION="$(xmllint --xpath '/project/version/text()' pom.xml)"

echo "--- Compiling Version $VERSION ---"


echo "--- Building ---"
# if it has -v show output
# Only run clean if one of the arguments is 'clean'
mvn clean package

if [ $? -ne 0 ]; then
    echo "--- Build Failed ---"
    exit
fi

echo "--- Removing Old File ---"
rm -rf ~/Servers/minecraft/spigot/dev/plugins/PoI-*

echo "--- Copying New Jar File ---"
cp "./target/PoI-$VERSION.jar" ~/Servers/minecraft/spigot/dev/plugins/PoI-$VERSION.jar

echo "--- Sending Reload ---"
mcrcon -p 12348765 reload >/dev/null 2>&1
echo "--- Done ---"
