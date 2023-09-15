package com.apm.jenkins.plugins.Client;

import java.util.HashMap;

public interface IClient {
    boolean transmitMetricData(HashMap<String, Object> payload);
    boolean transmitEventData(HashMap<String, Object> payload);
}