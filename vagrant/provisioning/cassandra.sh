#!/bin/bash

echo 'deb http://www.apache.org/dist/cassandra/debian 33x main' | sudo tee -a /etc/apt/sources.list.d/cassandra.list

gpg --keyserver pgp.mit.edu --recv-keys F758CE318D77295D
gpg --export --armor F758CE318D77295D | sudo apt-key add -

gpg --keyserver pgp.mit.edu --recv-keys 2B5C1B00
gpg --export --armor 2B5C1B00 | sudo apt-key add -

gpg --keyserver pgp.mit.edu --recv-keys 0353B12C
gpg --export --armor 0353B12C | sudo apt-key add -

sudo apt-get update
sudo apt-get -q install -y cassandra

sudo /etc/init.d/cassandra stop

IP=$(ifconfig eth1|grep "inet addr:"|awk '{print $2}'|awk -F : '{print $2}')

sudo cp /etc/cassandra/cassandra.yaml /etc/cassandra/cassandra.yaml.orig
sudo sed -i "s/seeds: \"127.0.0.1\"/seeds: \"${IP}\"/g" /etc/cassandra/cassandra.yaml
sudo sed -i "s/listen_address: localhost/listen_address: ${IP}/g" /etc/cassandra/cassandra.yaml
sudo sed -i "s/start_rpc: false/start_rpc: true/g" /etc/cassandra/cassandra.yaml
sudo sed -i "s/rpc_address: localhost/rpc_address: ${IP}/g" /etc/cassandra/cassandra.yaml

sudo /etc/init.d/cassandra start

