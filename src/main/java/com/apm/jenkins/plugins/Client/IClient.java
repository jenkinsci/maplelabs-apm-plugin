package com.apm.jenkins.plugins.Client;

import java.util.HashMap;

public interface IClient {
    boolean transmitData(HashMap<String, Object> payload, boolean isEvent);
}