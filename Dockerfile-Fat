FROM tomcat:8.0.50-jre8-alpine
#FROM tomcat:9.0.6-jre8-alpine

MAINTAINER brandon.barker@cornell.edu

ADD BaseXTemplate/BaseX.war $CATALINA_HOME/webapps/
ADD Source/target/ced2ar-web.war $CATALINA_HOME/webapps/
ADD https://github.com/ncrncornell/ced2ardata2ddi/releases/download/1.2.0/ced2ardata2ddi.war $CATALINA_HOME/webapps/
ADD Docker-deps/deploy_and_run_tomcat.sh /usr/local/bin/

WORKDIR $CATALINA_HOME/webapps/

# RUN \ 
#  mkdir BaseX && \
#  unzip BaseX.war -d./BaseX && \
#  mkdir ced2ar-web && \
#  unzip ced2ar-web.war -d ./

ENV CED2AR_HOME=$CATALINA_HOME/webapps/ced2ar-web


# For BaseX:
EXPOSE 8984

#
# TODO: run a script (Docker-deps/CED2AR/run-ced2ar.sh) that creates a modified jar from
# volume-mounted config files
#

ENTRYPOINT /usr/local/bin/deploy_and_run_tomcat.sh