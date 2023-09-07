#!/bin/bash

read -s -p "Enter your password : " pswd

echo $pswd | sudo -Sk apt update
echo $pswd | sudo -Sk apt install openjdk-17-jdk openjdk-17-jre -y

if [[ `java -version  2>&1 | awk '/version/ {print $3}'` != '"17.0.7"' ]];
then
    echo "Java 17.0.7 is not installed";
    exit 0
else
    echo "JAVA is installed"
fi

wget `curl -s https://maven.apache.org/download.cgi | grep -o 'https://.*apache-maven-[0-9]\.[0-9]\.[0-9]-bin\.tar\.gz' | head -n 1 | cut -d"\"" -f1` -P /tmp
echo $pswd | sudo -Sk tar xf /tmp/apache-maven-*.tar.gz -C /opt
echo $pswd | sudo -Sk ln -s /opt/apache-maven-* /opt/maven

echo $pswd | sudo -Sk bash -c 'cat > /etc/profile.d/maven.sh <<EOL
export JAVA_HOME=\$(readlink -f /usr/bin/javac | sed "s:/bin/javac::")
export MAVEN_HOME=/opt/maven
export PATH=\$JAVA_HOME/bin:\$MAVEN_HOME/bin:\$PATH
EOL'

source /etc/profile.d/maven.sh

echo $pswd | sudo -S apt install ca-certificates
curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | sudo tee \
  /usr/share/keyrings/jenkins-keyring.asc > /dev/null
echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null
echo $pswd | sudo -Sk apt update
echo $pswd | sudo -Sk apt install jenkins
