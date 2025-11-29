#!/bin/bash

# in den Ordner wechseln, in dem dieses Script liegt
cd "$(dirname "$0")"

PORT=${PORT:-8080}

echo "Starte McpSpring-Server auf Port 8081 ..."
java -jar McpSpring-Server-0.0.1-SNAPSHOT.jar --server.port=8081 &
sleep 20

echo "Starte McpSpring-client auf Port 8082 ..."
java -jar McpSpring-client-0.0.1-SNAPSHOT.jar --server.port=8082 &
sleep 20

echo "Starte ChatbotAlge auf Port 8083 ..."
java -jar ChatbotAlge-0.0.1-SNAPSHOT.jar --server.port=8083 &
sleep 20

echo "Starte FrontEnd auf Port ${PORT} ..."
java -jar FrontEnd-0.0.1-SNAPSHOT.jar --server.port=${PORT}
