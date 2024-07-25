#!/bin/bash

# Esegui il comando mvn clean install
echo "Executing mvn clean install..."
mvn clean install
if [ $? -ne 0 ]; then
  echo "Error during mvn clean install"
  exit 1
fi

# Esegui il comando docker build
echo "Building Docker image..."
docker build --no-cache -t iori0d/edge-gate2 .
if [ $? -ne 0 ]; then
  echo "Error during docker build"
  exit 1
fi

# Esegui il comando docker push
echo "Pushing Docker image..."
docker push iori0d/edge-gate2
if [ $? -ne 0 ]; then
  echo "Error during docker push"
  exit 1
fi

echo "Done!"
