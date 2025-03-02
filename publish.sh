#!/bin/bash

# Script to publish everything in a specific order
./gradlew publish-1.20.1-forge
./gradlew publish-1.20.1-fabric
./gradlew publish-1.21.1-forge
./gradlew publish-1.21.1-neoforge
./gradlew publish-1.21.1-fabric
./gradlew publish-1.21.4-forge
./gradlew publish-1.21.4-neoforge
./gradlew publish-1.21.4-fabric