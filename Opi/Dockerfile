FROM tomcat:8.5

MAINTAINER Achim Reiz <achim.reiz@uni-rostock.de>

COPY * /RAWFILES/
RUN jar -cvf /usr/local/tomcat/webapps/ROOT.war -C /RAWFILES .
