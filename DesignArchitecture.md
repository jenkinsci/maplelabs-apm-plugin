# Design Architecture

- [Design Architecture](#design-architecture)
  - [Features:](#features)
  - [Working Principle of APM Jenkins Plugin](#working-principle-of-apm-jenkins-plugin)
  - [APM Jenkins Plugin Flow Diagram](#apm-jenkins-plugin-flow-diagram)  
  - [APM Jenkins Plugin Configuration](#apm-jenkins-plugin-configuration)

## **Features:**

  - Metric Collection & reporting 

    The metrics that are collected and reported to snappyflow are:

    1. node metrics - (arch, disk_path, temp_path, node_name, available memory, total memory, no. of executors on each node etc).
    2. job metrics - (job name, status, duration, parent job etc)
    3. Jenkins metrics - (no. of nodes, node status, plugin count, active plugins, pending items in queue, blocked items in queue etc).
    

  - Event collection & reporting 
  
    Below are the events reported to snappyflow:     

    1. Job Events - (Build start, failure, success, etc).
    2. System Events - (System related config change, node status change(offline, temporary offline or online) etc).
    3. Security Events - (user authentication, authentication failure, login, logout, login failures etc). 
    


## Working Principle of APM Jenkins Plugin

APM jenkins plugin has following modules

- Collect Metrics/Events on Jenkins
- Index & Store Data in Snappyflow (Elasticsearch & Kafka)
- Visualize with snappyflow Dashboards
- Alert the users if required.

**Collect Metrics/Events on Jenkins:** 

- APM Jenkins Plugin once installed will be running on Jenkins to collect the metrics at regular intervals.
- Node metrics, job metrics & jenkins metrics (as mentioned above) are collected at regular intervals.
- In addition to the metrics, various events are also collected in the form of metrics and reported on event basis.

**Index & Store Data in Snappyflow (Elasticsearch & Kafka):** 

- Once the user configures project name, instance name(optional), application name along with profile key in jenkins Global configuration, the metrics and events are collected amd will be reported to metrics and log index of snappyflow instance at regular intervals. 
  
<img src="https://github.com/maplelabs/apm-jenkins-plugin/blob/releasev2_dev/images/apmPluginConfiguration.png" alt="APMConfiguration">

**Visualize with snappyflow Dashboards:** 

- Received data can be visualized on snappyflow Dashboards. The dashboard is divided into two panes : 
1. Metrics: This is further bifercated into three panes namely SystemMetrics, JobMetrics and NodeMetrics which display the respective metric information.
2. Events: This is further bifercated into three panes namely SystemEvent, BuildEvent and SecurityEvent which display the respective event information.

**Alert the users if required:**

- At any point when any user alert need to be added,snapyflow Dashboard templates can be customized accordingly.

  
## APM Jenkins plugin Flow Diagram 

<img src="https://github.com/maplelabs/apm-jenkins-plugin/blob/releasev2_dev/images/APMJenkinsPluginFlowChart.png" alt="DetailedFlowAPMJenkinsPlugin">

## APM Jenkins Plugin Configuration

Please check [README.md](README.md) to know more about APM Jenkins Plugin configuration.
