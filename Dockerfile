FROM clojure
# Setup and install dependencies
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
COPY project.clj /usr/src/app/
RUN lein deps

# Install git
RUN apt-get -y update
RUN apt-get -y install git
RUN git config --global user.email "matias@shyboys.club"
RUN git config --global user.name "dotties-bot"


# Copy and build project
COPY . /usr/src/app
RUN mv "$(lein uberjar | sed -n 's/^Created \(.*standalone\.jar\)/\1/p')" app-standalone.jar

CMD ["java", "-jar", "app-standalone.jar"]
