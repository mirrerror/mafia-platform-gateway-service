#!/bin/bash
set -e

HOST="$1"
PORT="$2"

echo "Waiting for $HOST:$PORT to become available..."

while ! nc -z "$HOST" "$PORT"; do
  sleep 2
done

echo "$HOST:$PORT is available. Starting application..."

exec "${@:3}"