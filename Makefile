INPUT=`cat dotties-add-json.json`
ROLE=${AWS_ROLE}

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

testrun:
	./target/dottiesbot "$(INPUT)"

package:
	rm lambda.zip
	zip lambda.zip bootstrap target/dottiesbot initiator.sh

build-for-aws:
	docker build . -t dottiesbot-native -f Dockerfile-lambda 
	docker create -ti --name dottiesbot-native-container dottiesbot-native bash
	docker cp dottiesbot-native-container:/home/application/lambda.zip ./lambda.zip
	docker rm -f dottiesbot-native-container

deploy-lambda:
	aws lambda create-function --function-name dotties-bot \
	--zip-file fileb://./lambda.zip --handler initiator.handler --runtime provided \
	--role $(ROLE) 
