INPUT=`cat dotties-add-json.json`

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
	-H:+ReportExceptionStackTraces

build-DottiesBotFunction:
	cp bootstrap $(ARTIFACTS_DIR)/bootstrap
	cp target/dottiesbot $(ARTIFACTS_DIR)/dottiesbot

run-sam:
	sam build 
	sam local invoke -e dotties-add-json.json --region=eu-north-1

full-test:
	make native
	make run-sam 

deploy-sam:
	 sam package --template-file template.yml --s3-bucket dotties-bot-lambda --output-template-file out.yaml
	 sam deploy --template-file ./out.yaml --stack-name dotties-bot

testrun:
	./target/dottiesbot "$(INPUT)"
