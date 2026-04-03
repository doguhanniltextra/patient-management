#!/usr/bin/env bash
set -euo pipefail

BOOTSTRAP="${1:-localhost:9092}"

kafka-topics --bootstrap-server "$BOOTSTRAP" --create --if-not-exists --topic lab-order-placed.v1 --partitions 3 --replication-factor 1
kafka-topics --bootstrap-server "$BOOTSTRAP" --create --if-not-exists --topic lab-result-completed.v1 --partitions 3 --replication-factor 1

echo "Topics ensured on $BOOTSTRAP"
