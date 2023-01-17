# **Snappyflow Jenkins PRD**

|**Target release**|Type // to add a target release date|
| :-: | :- |
|**Epic**|Type /Jira to add Jira epics and issues|
|**Document status**|DRAFT|
|**Document owner**|@ mention owner|
|**Designer**|@ designer|
|**Tech lead**|@ lead|
|**Technical writers**|@ writers|
|**QA**||
## **Objective**
This document captures the requirements of snappyflow Jenkins plugin.

It is a Jenkins plugin for automatically forwarding metrics, events, and service checks to a snappyflow account.

This plugin must be able to trouble shoot issues in jenkins using the dashboards and user must be alerted when jenkins is not working as expected. It must also collect information about the nodes and jobs running in a jenkins instance.

This plugin can be installed from the Update Center (found at Manage Jenkins -> Manage Plugins) in local Jenkins installation.
## ![(blue star)](Aspose.Words.9636b9e4-7d28-47e8-8ec7-f4ab8d44c506.002.png) **Success metrics**

|**Goal**|**Metric**|
| :-: | :-: |
|Jenkins plugin has to be in published state|Code to be reviewed and approved by jenkins team|
|Dashboard **(TBD with Deepa)**||
|User must be alerted when jenkins is not working as expected||
|Design Doc||
|End User Guide||
|Documenation for snappyflow website **(TBD with Deepa)**||
|Usecase where jekins monitoring is helpful in debugging jenkins performance issues **(TDB with Deepa)**||
## ![(blue star)](Aspose.Words.9636b9e4-7d28-47e8-8ec7-f4ab8d44c506.003.png) **Assumptions**
TBD
## ![(blue star)](Aspose.Words.9636b9e4-7d28-47e8-8ec7-f4ab8d44c506.004.png) **Milestones**
TBD

![](Aspose.Words.9636b9e4-7d28-47e8-8ec7-f4ab8d44c506.005.png)
## ![(blue star)](Aspose.Words.9636b9e4-7d28-47e8-8ec7-f4ab8d44c506.006.png) **Requirements**

|**Requirement**|**User Story**|**Importance**|**Jira Issue**|**Notes**|||
| :-: | :-: | :-: | :-: | :-: | :-: | :-: |
|This plugin should have an option for the user to configure the |The plugin accepts below inputs from user:<br>Profile key - snappyflow profile key( used to send the collected metrics to target)<br>Project name - Project name under which monitoring data appears<br>Application name - Application name under which monitoring data appears<br>Instance name - instance name used to identify the jenkins instance||||||
|This monitoring plugin has to collect these jenkins attributes |<p>This plugin has to capture the following jenkins attributes:</p><p>instance\_name<br>project\_name<br>application\_name<br>time<br>plugin\_name = jenkins<br>document\_type = systemStats<br>busy\_executors<br>total\_executors<br>launch\_failure<br>offline<br>online<br>temporarily\_offline<br>temporarily\_online<br>num\_config\_changed<br>num\_items\_copied<br>num\_items\_created<br>num\_items\_deleted<br>num\_itmes\_updated<br>num\_items\_location\_changed<br>num\_nodes<br>num\_nodes\_offline<br>num\_node\_online<br>num\_plugins<br>num\_active\_plugins<br>num\_failed\_plugins<br>num\_inactive\_plugins<br>num\_plugin\_with\_update<br>num\_projects<br>queue\_size<br>num\_buildable\_items\_in\_queue<br>num\_pending\_items\_in\_queue<br>num\_struck\_items\_in\_queue<br>num\_blocked\_items\_in\_queue</p>|HIGH|||||
|This monitoring plugin has to collect these node attributes |<p>` `This plugin has to capture the following node attributes:</p><p>instance\_name<br>project\_name<br>application\_name<br>time<br>plugin\_name = jenkins<br>document\_type = nodeStats<br>arch<br>disk\_path<br>temp\_path<br>node\_name<br>status ("online", "offline")<br>num\_executors<br>free\_executors<br>inuse\_executors<br>disk\_available (Bytes)<br>temp\_available (Bytes)<br>memory\_available (Bytes)<br>memory\_total (Bytes)<br>swap\_available (Bytes)<br>swap\_total (Bytes)<br>response\_time (ms)<br>num\_job\_aborted<br>num\_job\_completed<br>num\_job\_started</p>|` `HIGH| | |||
|This monitoring plugin has to collect these job attributes |instance\_name<br>project\_name<br>application\_name<br>time<br>plugin\_name = jenkins<br>document\_type = jobStats<br>name<br>parents<br>result<br>duration (ms)<br>number<br>result\_code (0 = SUCCESS, 1 = FAILURE, 2 = NOT\_BUILD, 3 = UNSTABLE, 4 = ABORTED)|HIGH|||||
|Send the collected attributes to snappy flow or some other app.|Design should be in such a way that with minimal changes the collected attributes should be sent to any other application (not just snappyflow) in future|HIGH|||||
|Dashboard to be updated with this data|||||||
|Need to send alerts to the user based on some errors|||||||
|Get review procedure from jenkins|||||||
|Unit-Test cases|||||||
|End User Documentation|||||||
|Document it per snappy flow documentation|||||||
## ![(blue star)](Aspose.Words.9636b9e4-7d28-47e8-8ec7-f4ab8d44c506.007.png) **User interaction and design**
## ![(question)](Aspose.Words.9636b9e4-7d28-47e8-8ec7-f4ab8d44c506.008.png) **Open Questions**

|**Question**|**Answer**|**Date Answered**|
| :-: | :-: | :-: |
||||
## ![(warning)](Aspose.Words.9636b9e4-7d28-47e8-8ec7-f4ab8d44c506.009.png) **Out of Scope**

