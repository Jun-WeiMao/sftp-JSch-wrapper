#!/bin/bash

project="sftp-JSch-wrapper"
currentDir=$(pwd)

if [[ "$currentDir" != *"$project"* ]]; then
    echo "Need to execute build.sh at project folder !"
    read -rp "Is '$currentDir' the project folder? " answer
    case $answer in
        [Yy]* ) ;;
        [Nn]* ) echo "exit now." & exit;;
    esac
fi

currentVer=$(less VERSION)

read -erp "Version for this build? " -i "$currentVer" newVer

if [ -f "VERSION" ]; then
    echo "$newVer" > VERSION
fi

echo ""
echo "[START] Gradle clean Ver.$newVer"
gradle clean -Pversion="$newVer"
echo ""

echo "[START] Gradle build Ver.$newVer"
gradle build -Pversion="$newVer"
echo ""

[[ -d "out" ]] || mkdir "out"

mv "build/libs/$project-$currentVer.jar" "out/$project-$currentVer.jar"
