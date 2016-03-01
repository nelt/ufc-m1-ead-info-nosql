#!/bin/bash

sudo wget -qO - https://packages.elastic.co/GPG-KEY-elasticsearch | sudo apt-key add -
echo "deb http://packages.elastic.co/elasticsearch/2.x/debian stable main" | sudo tee -a /etc/apt/sources.list.d/elasticsearch.list
sudo apt-get update
sudo apt-get install elasticsearch

IP=$(ifconfig eth1|grep "inet addr:"|awk '{print $2}'|awk -F : '{print $2}')
sudo sed -i "s/# network.host: 192.168.0.1/network.host: ${IP}/g" /etc/elasticsearch/elasticsearch.yml

sudo systemctl restart elasticsearch
sudo systemctl enable elasticsearch


