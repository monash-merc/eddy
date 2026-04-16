FROM tomcat:9-jdk21-openjdk
LABEL org.opencontainers.image.authors="Simon Yu <xiaoming.yu@monash.edu>"

ENV PATH $PATH:$CATALINA_HOME/bin

RUN adduser --disabled-password --gecos '' tomcat


EXPOSE 8080
EXPOSE 8009

RUN rm -rf $CATALINA_HOME/webapps
VOLUME "$CATALINA_HOME/webapps"

# Set timezone
ARG TIMEZONE
ENV TZ $TIMEZONE
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone
# Launch Tomcat
CMD ["/usr/local/tomcat/bin/catalina.sh", "run"]
