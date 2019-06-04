FROM debian:latest

RUN apt-get update \
 && apt-get install -y apt-transport-https \
 && apt-get install -y gnupg2 \
 && apt-get install -y openjdk-8-jdk \
 && echo "deb https://dl.bintray.com/sbt/debian /" | tee -a /etc/apt/sources.list.d/sbt.list \
 && apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823 \
 && apt-get update \
 && apt-get install -y unzip \
 && apt-get install -y sbt

COPY ./ /app/

COPY ./run.sh /bin/run.sh

RUN cd /app \
 && cat build.sbt \
 && sbt clean update dist \
 && unzip ./target/universal/chillbot-srv-1.zip

RUN chmod +x /bin/run.sh

EXPOSE 9200

CMD ["run.sh"]