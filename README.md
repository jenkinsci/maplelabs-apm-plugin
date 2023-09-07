# snappyflow-jenkins-plugin
Jenkins monitoring plugin will provide ability to monitor the Jenkins operations.
It will give high-level overview of how the jobs are performing and their current status.

Monitoring and observability in our Production environment helps us to know that our system runs well, our environment is stable, and in case of issue – to root-cause and remediate quickly and efficiently.
This helps reduce the Mean Time to Recovery, which is a crucial metric for software teams.

This plugin helps in acheiving the same and aims at automatically forwarding metrics, service checks to APM Snappyflow(Elasticsearch & Kafka).
It takes four simple steps to gain observability into Jenkins CI/CD pipeline.

- Step 1: Collect Data on Jenkins 
- Step 2: Index & Store Data in Snappyflow (Elasticsearch & Kafka)
- Step 3: Visualize with snappyflow Dashboards
- Step 4: Alert the users

# Plugin user interface
To configure your snappyflow-jenkins-Plugin, navigate to the Manage Jenkins -> Configure System page on your Jenkins installation.  
Once there, scroll down to find the SnappyflowPlugin section:

![Configure SnappyFlow](https://user-images.githubusercontent.com/12271765/224924743-38e9a3d8-a062-435a-97c0-bda3143dd2a6.png)

# Contribution
This plugin is in development phase currently.  
If you are willing to contribute, follow below link  
[Contribution Details](https://github.com/snappyflow/snappyflow-jenkins-plugin/blob/releasev1_dev/Contribution.md)

