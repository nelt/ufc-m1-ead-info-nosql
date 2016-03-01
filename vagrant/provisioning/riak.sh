#!/bin/bash

sudo apt-get -q install -y libpam0g-dev libssl0.9.8
wget -q http://s3.amazonaws.com/downloads.basho.com/riak/2.1/2.1.1/ubuntu/precise/riak_2.1.1-1_amd64.deb
sudo dpkg -i riak_2.1.1-1_amd64.deb

sudo echo '* soft nofile 65536' > /etc/security/limits.d/riak.conf
sudo echo '* hard nofile 65536' >> /etc/security/limits.d/riak.conf

IP=$(ifconfig eth1|grep "inet addr:"|awk '{print $2}'|awk -F : '{print $2}')

sudo cp /etc/riak/riak.conf /etc/riak/riak.conf.orig
sudo sed -i "s/127.0.0.1:8098/${IP}:8098/g" /etc/riak/riak.conf
sudo sed -i "s/127.0.0.1:8087/${IP}:8087/g" /etc/riak/riak.conf

sudo reboot
