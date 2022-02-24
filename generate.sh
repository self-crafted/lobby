#!/usr/bin/env sh

echo "Extension display name: "
read name
echo "Extension remote repository name: "
read repo

sed -i "s|EXTENSION_DISPLAY_NAME|$name|g" README.md build.gradle.kts settings.gradle.kts src/main/resources/extension.json
sed -i "s|EXTENSION_REPO_NAME|$repo|g" README.md build.gradle.kts settings.gradle.kts src/main/resources/extension.json

lowercasename="$(echo "$name" | tr '[:upper:]' '[:lower:]')"
sed -i "s|package com.github.klainstom;|package com.github.klainstom.$lowercasename;|i" src/main/java/com/github/klainstom/*
mkdir src/main/java/com/github/klainstom/"$lowercasename"/
mv src/main/java/com/github/klainstom/* src/main/java/com/github/klainstom/"$lowercasename"/

rm generate.sh
