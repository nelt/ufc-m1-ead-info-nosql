#!/bin/bash

HADOOP_VERSION=2.7.2

if [ ! -f /vagrant/hadoop-${HADOOP_VERSION}.tar.gz ]; then
    echo "downloading hadoop ${HADOOP_VERSION}, this can take a while..."
    wget -q http://apache.crihan.fr/dist/hadoop/common/hadoop-${HADOOP_VERSION}/hadoop-${HADOOP_VERSION}.tar.gz
    echo "...done."
    sudo mv hadoop-${HADOOP_VERSION}.tar.gz /vagrant/hadoop-${HADOOP_VERSION}.tar.gz
fi

tar zxf /vagrant/hadoop-${HADOOP_VERSION}.tar.gz

sudo mv hadoop-${HADOOP_VERSION} /opt/hadoop
sudo chown -R vagrant /opt/hadoop

echo 'export PATH=$PATH:/opt/hadoop/bin' | sudo tee -a /etc/profile.d/hadoop.sh
echo 'export PATH=$PATH:/opt/hadoop/sbin' | sudo tee -a /etc/profile.d/hadoop.sh

sudo cp /opt/hadoop/etc/hadoop/hadoop-env.sh /opt/hadoop/etc/hadoop/hadoop-env.sh.orig
sudo sed -i 's/export JAVA_HOME.*$/export JAVA_HOME=\/usr\/lib\/jvm\/java-8-oracle/g' /opt/hadoop/etc/hadoop/hadoop-env.sh

sudo mv /opt/hadoop/etc/hadoop/core-site.xml /opt/hadoop/etc/hadoop/core-site.xml.orig
sudo mv /opt/hadoop/etc/hadoop/hdfs-site.xml /opt/hadoop/etc/hadoop/hdfs-site.xml.orig
sudo mv /opt/hadoop/etc/hadoop/yarn-site.xml /opt/hadoop/etc/hadoop/yarn-site.xml.orig

sudo cp /vagrant/hadoop/core-site.xml /opt/hadoop/etc/hadoop/core-site.xml
sudo cp /vagrant/hadoop/hdfs-site.xml /opt/hadoop/etc/hadoop/hdfs-site.xml
sudo cp /vagrant/hadoop/mapred-site.xml /opt/hadoop/etc/hadoop/mapred-site.xml
sudo cp /vagrant/hadoop/yarn-site.xml /opt/hadoop/etc/hadoop/yarn-site.xml

IP=$(ifconfig eth1|grep "inet addr:"|awk '{print $2}'|awk -F : '{print $2}')
sudo sed -i "s/HADOOPIP/${IP}/g" /opt/hadoop/etc/hadoop/core-site.xml

#   StrictHostKeyChecking ask
sudo cp /etc/ssh/ssh_config /etc/ssh/ssh_config.orig
sudo sed -i "s/#   StrictHostKeyChecking ask/StrictHostKeyChecking no/g" /etc/ssh/ssh_config

ssh-keygen -t dsa -P '' -f ~/.ssh/id_dsa
cat ~/.ssh/id_dsa.pub >> ~/.ssh/authorized_keys
chmod 0600 ~/.ssh/authorized_keys

. /etc/profile.d/hadoop.sh

hdfs namenode -format
start-dfs.sh
hdfs dfs -mkdir /user
hdfs dfs -mkdir /user/vagrant
start-yarn.sh


#echo "#!/bin/sh -e" | sudo tee /etc/rc.local
#echo "su - vagrant -c \"start-dfs.sh\"" | sudo tee -a /etc/rc.local
#echo "su - vagrant -c \"start-yarn.sh\"" | sudo tee -a /etc/rc.local


