# Dapr Demo

## build sequence

* ./application/build.sh - application container
* ./build.sh - docker compose infrastructure

## service invocation

```
docker compose exec -i shell curl -XPOST http://service-a:8080/entrypoint

docker compose exec -i shell mysql -u dapr -h mysql --password=dapr --skip_ssl -e 'select * from dapr_demo.ping_log'
```
