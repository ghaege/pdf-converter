build:
	docker build --target pdf-converter . -t ghaege/pdf-converter

push:
	docker push ghaege/pdf-converter

start: stop
	docker run --name pdf-converter -m 512m --rm -p 8100:8100 ghaege/pdf-converter

stop:
	docker stop --name pdf-converter > /dev/null 2>&1 || true
