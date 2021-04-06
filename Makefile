build:
	docker build -t dotties-bot .

run:
	docker run --env-file .env -it dotties-bot 
