#!/bin/bash

MAVEN_VERSION=3.3.9
IDEA_VERSION=15.0.2

echo "downloading maven version $MAVEN_VERSION..."
wget -nv http://apache.mindstudios.com/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.tar.gz && echo  "done."
echo "setting up maven..."
sudo mkdir -p /opt/apache-maven
sudo tar xvf apache-maven-$MAVEN_VERSION-bin.tar.gz -C /opt/apache-maven/
sudo ln -s /opt/apache-maven/apache-maven-$MAVEN_VERSION /opt/apache-maven/current
sudo echo 'export PATH=/opt/apache-maven/current/bin:$PATH' > /etc/profile.d/maven.sh
sudo rm apache-maven-$MAVEN_VERSION-bin.tar.gz
echo "maven setted up."

echo "installing X dependencies for idea."
sudo apt-get install -y xauth libxrender1 libxtst6 libxi6

echo "downloading idea version $IDEA_VERSION..."
wget -nv https://download.jetbrains.com/idea/ideaIC-$IDEA_VERSION.tar.gz && echo "done."
echo "setting up idea..."
sudo mkdir -p /opt/idea
sudo tar xvf ideaIC-$IDEA_VERSION.tar.gz -C /opt/idea/
IDEA_INSTALL=$(find /opt/idea/ -maxdepth 1 -type d -name 'idea-IC*'| head -n1)
sudo ln -s $IDEA_INSTALL /opt/idea/current
sudo echo 'export PATH=/opt/idea/current/bin/:$PATH' > /etc/profile.d/idea.sh
sudo rm ideaIC-$IDEA_VERSION.tar.gz
echo "idea setted up."

sudo apt-get install -y git

