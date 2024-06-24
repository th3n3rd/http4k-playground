#!/bin/bash

trap 'exit' INT TERM ERR
trap 'jobs -p | xargs -r kill' EXIT

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" &> /dev/null && pwd)"

echo "Navigating to the root of the project"
cd "$SCRIPT_DIR/.."

echo "Running tests and packaging the application"
./mvnw clean package

echo "Running local smoke test"
java -jar target/GuessTheSecret-0.0.1-SNAPSHOT.jar &
APP_URI=http://localhost:9000 ./mvnw test -Dtest=SmokeTest