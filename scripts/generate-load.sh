#/usr/bin/env bash
while true
do
  curl -v -X POST -d 1001 http://localhost:8080/numbers -H "Content-Type: application/json"
  curl -v -X POST -d 1002 http://localhost:8080/numbers -H "Content-Type: application/json"
  curl -v -X POST -d 1003 http://localhost:8080/numbers -H "Content-Type: application/json"
  curl -v -X POST -d 1004 http://localhost:8080/numbers -H "Content-Type: application/json"
  curl -v -X POST -d 1005 http://localhost:8080/numbers -H "Content-Type: application/json"
  curl -v -X DELETE http://localhost:8080/numbers
  sleep 1
done
