#!/bin/bash

sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv EA312927

echo "deb http://repo.mongodb.org/apt/ubuntu trusty/mongodb-org/3.2 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-3.2.list

sudo apt-get -q update
sudo apt-get -q install -y mongodb-org

IP=$(ifconfig eth1|grep "inet addr:"|awk '{print $2}'|awk -F : '{print $2}')
sudo sed -i "s/bindIp: 127.0.0.1/bindIp: ${IP}/g" /etc/mongod.conf

sudo sudo service mongod restart

