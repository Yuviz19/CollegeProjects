#!/bin/bash
cd "$(dirname "$0")"

export _JAVA_AWT_WM_NONREPARENTING=1

rm -rf bin
mkdir -p bin
javac -cp "lib/*" -d bin $(find src -name "*.java") || exit 1
echo "Compiled OK. Starting app..."
java -cp "bin:lib/*" Main
