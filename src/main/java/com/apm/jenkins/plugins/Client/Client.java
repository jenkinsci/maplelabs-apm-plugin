package com.apm.jenkins.plugins.Client;

import java.util.HashMap;

public interface Client {
    boolean transmitData(HashMap<String, Object> payload);
}