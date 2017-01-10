#!/bin/sh
curl -s --header "content-type: text/xml" -d @src/test/resources/input.xml http://localhost:8080/proxy | xmllint --format -
