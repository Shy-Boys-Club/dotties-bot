build:
	docker build -t dotties-bot .

run:
	docker run --env-file .env -it dotties-bot 

native:
	make build-local && make build-native 

build-local:
	lein do clean, uberjar

build-native:
	native-image \
	--report-unsupported-elements-at-runtime \
	--no-server \
	--allow-incomplete-classpath \
	--initialize-at-build-time \
	--initialize-at-run-time=org.httpkit.client.ClientSslEngineFactory\$$SSLHolder \
	--enable-url-protocols=http,https \
	-jar ./target/dottiesbot-0.1.0-SNAPSHOT-standalone.jar  \
	-H:Name=./target/dottiesbot \


build-DottiesBotFunction:
	cp target/dottiesbot $(ARTIFACTS_DIR)/bootstrap

run-sam:
	sudo sam build 
	sudo sam local invoke -e dotties-add-json.json

full-test:
	make native
	sudo make run-sam 
