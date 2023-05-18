# Design Architecture

- [Design Architecture](#design-architecture)
  - [List of features:](#list-of-features)
  - [Working Principle of APM Jenkins Plugin](#working-principle-of-apm-jenkins-plugin)
  - [APM Jenkins Plugin Flow Diagram](#apm-jenkins-plugin-flow-diagram)
  - [APM Jenkins Plugin Architecture](#apm-jenkins-plugin-architecture)  
  - [APM Jenkins Plugin Configuration](#apm-jenkins-plugin-configuration)

## **List of features:**

  - Metric Collection & reporting 

    Various metrics are collected and reported to snappyflow. 
    Few supported metrics are,

    1. node metrics - (arch, disk_path, temp_path, node_name, available memory, total memory, no. of executors on each node etc).
    2. job metrics - (job name, status, duration, parent job etc)
    3. Jenkins metrics - (no. of nodes, node status, plugin count, active plugins, pending items in queue, blocked items in queue etc).
    

  - Event reporting 
  
    Below events are reported to snappyflow.     

    1. Job Events - (Build start, failure, success, checkout etc).
    2. System Events - (Change in files or system related config change, user details etc)
    3. Security Events - (user authentication, authentication failure, login, logout, login failures etc). 
    
    **Note: To support sending of events to snappyflow, sfagent needs to be installed on the machine where master jenkins instance is running.**

<img src="https://github.com/maplelabs/opensearch-scaling-manager/blob/master/images/ScalingManagerArchitecture.png" alt="ScalingManagerArchitecture">

## Working Principle of APM Jenkins Plugin

APM jenkins plugin has following modules

- Collect Metrics/Events on Jenkins
- Index & Store Data in Snappyflow (Elasticsearch & Kafka)
- Visualize with snappyflow Dashboards
- Alert the users if required.

**Collect Metrics/Events on Jenkins:** 

- APM Jenkins Plugin once installed will be running on Jenkins to collect the metrics at regular intervals.
- Node metrics, job metrics & jenkins metrics (as mentioned above) are collected at regular intervals.
- In addition to the metrics, various events are also collected in the form of logs by sfagent.

**Index & Store Data in Snappyflow (Elasticsearch & Kafka):** 

- Once the user configures project name, instance name, application name along with profile key in jenkins Global configuration, the collected metrics
  will be reported to specific snappyflow instance at regular intervals. 
- Once the metrics are received by snappyflow, they are indexed into ES/kafka on snappyflow at regular intervals.
- sfagent takes care of parsing the event logs and pushing them to snappyflow at regular intervals.
- Once the events are recevied as logs, snappyflow takes care of storing them as well.

**Visualize with snappyflow Dashboards:** 

- Received data can be visualized on snappyflow Dashboards as required.
- Custom dashboards/templates need to be designed for monitoring jenkins metrics & events.

**Alert the users if required:**

- At any point when any user alert need to be added,snapyflow Dashboard templates can be customized accordingly.

  
## APM Jenkins plugin Flow Diagram 

<img src="https://github.com/maplelabs/apm-jenkins-plugin/blob/releasev1_dev/images/APMPluginFlowChart.png" alt="DetailedFlowAPMJenkinsPlugin">



## Scaling Manager Architecture

1. Scaling Manager is deployed in all the nodes in cluster. Lets say cluster has 3 nodes. Now resource utilization went high and there is a need of new node in cluster.
2. When a new node is added to the cluster ansible scripts will run in new node and it will install Scaling Manger, OpenSearch, All the necessary details which is needed and the new node details will be added to the available nodes list in order to monitor it

<img src="https://github.com/maplelabs/opensearch-scaling-manager/blob/master/images/BasicFlowScalingManager.png" alt="BasicFlowScalingManager">

## Scaling Manager Configuration

Please check [config file](https://github.com/maplelabs/opensearch-scaling-manager/blob/master/docs/Config.md) to know more about scaling manager configuration
