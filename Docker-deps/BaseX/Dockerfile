FROM ubuntu:16.04

ADD BaseX867.tar.xz /

# UTF-8 by default
#
RUN apt-get update -y && \
  apt-get install locales && \
  locale-gen en_US.UTF-8 
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

#
# Pull Zulu OpenJDK binaries from official repository:
#
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 0x219BD9C9 && \
  echo "deb http://repos.azulsystems.com/ubuntu stable main" >> /etc/apt/sources.list.d/zulu.list && \
  apt-get -qqy update && \
  apt-get -qqy install zulu-8=8.23.0.3 && \
  apt-get clean

EXPOSE 8984
CMD ["/basex/bin/basexhttp"]