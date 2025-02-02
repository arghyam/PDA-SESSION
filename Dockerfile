FROM gradle:4.10.2-jdk8-alpine

ADD ./src src
ADD ./gradle/wrapper gradle/wrapper
ADD ./build.gradle   build.gradle
ADD ./gradlew gradlew
ADD ./gradlew.bat    gradlew.bat
ADD ./settings.gradle    settings.gradle
COPY ./src/main/resources/templates /etc/templates
RUN gradle clean build -x test

ENTRYPOINT ["java","-jar", "./build/libs/socion-session-0.0.1-SNAPSHOT.jar", "--spring.profiles.active=uat"]

#  This change includes adding the SSL certificate for keycloak to JAVA keystore in order to connect to keycloak.
