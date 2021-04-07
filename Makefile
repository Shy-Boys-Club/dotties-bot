build:
	docker build -t dotties-bot .

run:
	docker run --env-file .env -it dotties-bot 

build-local:
	lein do clean, uberjar

native:
	native-image \
		--no-fallback \
		-jar ./target/dottiesbot-0.1.0-SNAPSHOT-standalone.jar  \
		-H:Name=./target/dottiesbot \
		-H:+ReportExceptionStackTraces
