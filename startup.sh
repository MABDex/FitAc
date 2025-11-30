#!/bin/bash

# in den Ordner wechseln, in dem dieses Script liegt
cd "$(dirname "$0")"

PORT=${PORT:-8080}

echo "Starte McpSpring-Server auf Port 8899 ..."
java -jar McpSpring-Server-0.0.1-SNAPSHOT.jar --server.port=8899 &
sleep 180

echo "Starte McpSpring-client auf Port 8066 ..."
java -jar McpSpring-client-0.0.1-SNAPSHOT.jar --server.port=8066 &
sleep 180

echo "Starte ChatbotAlge auf Port 8070 ..."
java -jar ChatbotAlge-0.0.1-SNAPSHOT.jar --server.port=8070 &
sleep 20

echo "Starte FrontEnd auf Port ${PORT} ..."
java -jar FrontEnd-0.0.1-SNAPSHOT.jar --server.port=${PORT}
