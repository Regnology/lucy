# Test base image with google-chrome and driver installed for using Selenium

FROM eclipse-temurin:11-jre-focal


RUN apt-get -y update
RUN apt-get install -y gnupg unzip
RUN wget -q -O - https://dl-ssl.google.com/linux/linux_signing_key.pub | apt-key add -
RUN sh -c 'echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list'

RUN apt-get -y update
RUN apt-get -y install google-chrome-stable

COPY chromeDriverDownloader.sh /tmp
RUN /tmp/chromeDriverDownloader.sh
