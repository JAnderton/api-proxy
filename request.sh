#!/bin/sh
curl -s --header "content-type: text/xml" -d @proxy/src/main/resources/request.xml http://localhost:8000/proxy
echo
curl -s http://localhost:8000/proxy/metrics
