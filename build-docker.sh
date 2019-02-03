#!/bin/sh

# -v "$(pwd)/..:/work" -v "$HOME/.m2":/root/.m2

docker build --pull -t framstag/semtrail:latest .
