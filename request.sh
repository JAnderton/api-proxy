#!/bin/sh
curl -s --header "content-type: text/xml" -d @proxy/src/test/resources/request.xml http://localhost:8080/proxy
curl -s localhost:8000/proxy/metrics
