#!/bin/sh
curl --header "content-type: text/xml" -d @input.xml http://localhost:8080/ws | xmllint --format -
