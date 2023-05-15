# **Snappyflow Jenkins PRD**

|**Target release**|Type // to add a target release date|
| :-: | :- |
|**Document status**|Ready To Review|
|**Document owner**| Deepa-Martin-ML/Bhagavad-Geetha-ML|
|**Designer**|@ designer|
|**Tech lead**|@ lead|
|**Technical writers**|@ writers|
|**QA**||

## **Objective**
This document captures the requirements of snappyflow Jenkins plugin.

It is a Jenkins plugin for automatically forwarding metrics, events, and service checks to a snappyflow account.

This plugin must be able to trouble shoot issues in jenkins using the dashboards and user must be alerted when jenkins is not working as expected. It must also collect information about the nodes and jobs running in a jenkins instance.

This plugin can be installed from the Update Center (found at Manage Jenkins -> Manage Plugins) in local Jenkins installation.
## **Tasks & Milestones**

|**Task**|**Acceptance Rule**|**MileStone**|
|:-|:-|:-|
|PRD has to be in published state|Review & Approval by SF team|**1**|
|Usecase where jekins monitoring is helpful in debugging jenkins performance issues|Review & Approval by SF team|**1**|
|Design Doc|Review & Approval by SF team|**1**|
|Jenkins plugin has to be in published state|Review & Approval by SF team|**1**|
|Dashboard|Template Review & Approval by snappyflow team by SF team|**2**|
|User must be alerted when jenkins is not working as expected|Details of alerts Review & Approval by SF team|**2**|
|Documenation for snappyflow website|Review & Approval by SF team|**2**|
|Jenkins plugin has to be in published state| Code Review & Approval by Jenkins team|**3**|
|End User Guide|Review & Approval by SF team|**3**|

##  **Assumptions**
NA

## **Requirements**
|**ID**|**Requirement**|**Description**|**User Story**|**GitHub Issue ID**|**Importance**|**Notes**|
| :- |:-| :- | :- | :- | :- | :- |
|1|This plugin should have an option for the user to configure the profile key and other parameters required for forwarding the metrics to specific snappyflow instance.|The plugin accepts below inputs from user:<br>Profile key - snappyflow profile key( used to send the collected metrics to target)<br>Project name - Project name under which monitoring data appears<br>Application name - Application name under which monitoring data appears<br>Instance name - instance name used to identify the jenkins instance|Based on these values, plugin should decode the profile key and build request URL to push the metrics|https://github.com/maplelabs/apm-jenkins-plugin/issues/9 ||**Specific to Snappyflow**|
|2|Design Document|||https://github.com/maplelabs/apm-jenkins-plugin/issues/2 |HIGH||
|3|This monitoring plugin has to collect these jenkins attributes.|This plugin has to capture the following jenkins attributes:<p>instance\_name<br>project\_name<br>application\_name<br>time<br>plugin\_name = jenkins<br>document\_type = systemStats<br>busy\_executors<br>total\_executors<br>launch\_failure<br>offline<br>online<br>temporarily\_offline<br>temporarily\_online<br>num\_config\_changed<br>num\_items\_copied<br>num\_items\_created<br>num\_items\_deleted<br>num\_itmes\_updated<br>num\_items\_location\_changed<br>num\_nodes<br>num\_nodes\_offline<br>num\_node\_online<br>num\_plugins<br>num\_active\_plugins<br>num\_failed\_plugins<br>num\_inactive\_plugins<br>num\_plugin\_with\_update<br>num\_projects<br>queue\_size<br>num\_buildable\_items\_in\_queue<br>num\_pending\_items\_in\_queue<br>num\_struck\_items\_in\_queue<br>num\_blocked\_items\_in\_queue</p>|1. Monitoring Build Status (The end-user may want to monitor the build status of their Jenkins jobs in real-time, including when a job is running, when it completes, and its outcome (success or failure))<br>2. View Job History: The end-user may want to view the job history of a particular Jenkins job, including the build history and any build artifacts that were generated.<br>3. Monitor Resource Utilization (The end-user may want to monitor the resource utilization of Jenkins, including CPU and memory usage, to identify any performance bottlenecks.)||HIGH||
|4|This monitoring plugin has to collect these node attributes.|This plugin has to capture the following node attributes:<p>instance\_name<br>project\_name<br>application\_name<br>time<br>plugin\_name = jenkins<br>document\_type = nodeStats<br>arch<br>disk\_path<br>temp\_path<br>node\_name<br>status ("online", "offline")<br>num\_executors<br>free\_executors<br>inuse\_executors<br>disk\_available (Bytes)<br>temp\_available (Bytes)<br>memory\_available (Bytes)<br>memory\_total (Bytes)<br>swap\_available (Bytes)<br>swap\_total (Bytes)<br>response\_time (ms)<br>num\_job\_aborted<br>num\_job\_completed<br>num\_job\_started</p>| 1. Node health (memory, CPU, disk usage). if the nodes are idle since a long time, it can be removed/alerted to the user.<br>2. Utilization of the particular node, over a period of time gives user some inputs on the need for a new node or removal of exisintg node etc.|https://github.com/maplelabs/apm-jenkins-plugin/issues/18 |HIGH||
|5|This monitoring plugin has to collect these job events mentioned in Description| instance\_name<br>project\_name<br>application\_name<br>time<br>plugin\_name = jenkins<br>document\_type = jobStats<br>name<br>parents<br>result<br>duration (ms)<br>number<br>result\_code (0 = SUCCESS, 1 = FAILURE, 2 = NOT\_BUILD, 3 = UNSTABLE, 4 = ABORTED) |1. job build events( Details of job/project status (started/success/failure) on specific node.)<br>2.	job duration: (Last 4 hours average job duration, time b/w last successful job and current failed job etc). <br> 3.Alert the user on the job status, when a job succeeds/fails.|https://github.com/maplelabs/apm-jenkins-plugin/issues/19 |HIGH||
|6| Send the collected attributes to snappy flow or some other app at regular intervals.|**Need to check what should be the interval**|Design should be in such a way that with minimal changes the collected attributes should be sent to any other application (not just snappyflow) in future|https://github.com/maplelabs/apm-jenkins-plugin/issues/10 |HIGH||
|7|Send the collected logs to snappy flow using SFAgent.||Need to explore more on where jenkins logs are stored and how to retrieve them|https://github.com/maplelabs/apm-jenkins-plugin/issues/20 |Low|**This can be considered later**|
|8|Snappyflow Dashboard to be updated with this data|Template needs to be designed and reviewed by snappyflow team.|||||
|9|Need to send alerts to the user based on some errors|1. User can be alerted with Job execution status.<br>2. User can be alerted on node status change<br>3. User can be alerted if any node's CPU or memory reaches 80% or more.||||
|10|Get review procedure from jenkins & make it ready for Jenkins team review|||https://github.com/maplelabs/apm-jenkins-plugin/issues/7 |||
|11|Unit-Test plan|||https://github.com/maplelabs/apm-jenkins-plugin/issues/6 |||
|12|End User Documentation|||||
|13|Document it per snappy flow documentation|||||

## **User interaction and design**
Design must be flexible to add new targets in future. First deliverable must support sending metric to Snappyflow targets.
## **Open Questions**

|**Question**|**Answer**|**Date Answered**|
| :-: | :-: | :-: |
|1. How to prevent Snappyflow Security key being exposed in Opensouce code?| It can be stored in go binary and can be retreived in real time by using some system APIs. **Deepa to share more inputs on this**||
|2. How do we send logs to snappyflow? If we are using SFAgent, just give a URL for installing SFAgent in documentation?| Yes, we do it this way for now|11/05/2023|
|3.How do we send events to snappyflow?| For now, we can send events also as metric|11/05/23|

## **Out of Scope**
NA

