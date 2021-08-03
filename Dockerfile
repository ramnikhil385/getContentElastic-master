FROM docker.repo1.uhc.com/doc360/java8

ENV JAVA_HOME /usr/lib/jvm/java-8-openjdk-amd64

RUN mkdir -p /opt/app && mkdir -p /app_2/doc360/rest/getcontent/logs/archive && mkdir -p /app_2/doc360/rest/getcontent/dfc/shared
RUN apt-get update && apt-get install lsof zip -y
ADD doc360-rest-api-get-content/target/getcontent-0.0.1-SNAPSHOT.jar /opt/app/
ADD configurations/entrypoint.sh /opt/app/entrypoint.sh
ADD configurations/cacerts /opt/app/cacerts
ADD configurations/dfc.keystore /app_2/doc360/rest/getcontent/dfc/shared/dfc.keystore
RUN chmod -R 777 /opt/app && chmod -R 777 /app_2

USER 1010101

ENTRYPOINT ["/bin/bash", "/opt/app/entrypoint.sh"]

EXPOSE 8080
