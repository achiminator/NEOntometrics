FROM maven:3.8.4-jdk-11 as maven_builder
COPY . /RAWFILES/
WORKDIR /RAWFILES
RUN mvn clean package

FROM tomcat:8.5
COPY --from=maven_builder /RAWFILES/target/ROOT.war /usr/local/tomcat/webapps/ROOT.war
