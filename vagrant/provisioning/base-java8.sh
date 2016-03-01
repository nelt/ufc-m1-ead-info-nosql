#!/bin/bash

sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get -q install -y software-properties-common python-software-properties debconf-utils

echo "oracle-java8-installer shared/accepted-oracle-license-v1-1 select true" | sudo debconf-set-selections

sudo apt-get -q install -y oracle-java8-installer
sudo apt-get -q install -y oracle-java8-set-default
sudo apt-get -q install -y git

