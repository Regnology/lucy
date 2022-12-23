#!/bin/sh

CHROMEDRIVER_PATH="/app/cache/selenium/chromedriver"
echo "Download the appropriate driver for google-chrome"
mkdir -p ${CHROMEDRIVER_PATH} &&
CHROMEVER=$(google-chrome --product-version | grep -o "[0-9]*\.[0-9]*\.[0-9]*") &&
DRIVERVER=$(curl -s "https://chromedriver.storage.googleapis.com/LATEST_RELEASE_$CHROMEVER") &&
wget -q --continue -P /tmp "http://chromedriver.storage.googleapis.com/$DRIVERVER/chromedriver_linux64.zip" &&
unzip /tmp/chromedriver_linux64.zip -d ${CHROMEDRIVER_PATH} &&
echo "The driver has been downloaded!" && echo "Google Chrome version: ${CHROMEVER}" && echo "Driver version: ${DRIVERVER}"