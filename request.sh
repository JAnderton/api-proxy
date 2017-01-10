#!/bin/sh
curl -s http://localhost:8000/proxy | xmllint --format -
echo
curl -s http://localhost:8000/proxy/metrics
