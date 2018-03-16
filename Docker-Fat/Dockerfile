FROM 9.0.6-jre8-alpine

MAINTAINER brandon.barker@cornell.edu

ADD BaseXTemplate/BaseX.war $CATALINA_HOME/webapps/
ADD Source/target/ced2ar-web.war $CATALINA_HOME/webapps/

WORKDIR $CATALINA_HOME/webapps/

RUN jar xvf BaseX.war && jar xvf ced2ar-web.war
ENV CED2AR_HOME=$CATALINA_HOME/webapps/ced2ar-web


# For BaseX:
EXPOSE 8984