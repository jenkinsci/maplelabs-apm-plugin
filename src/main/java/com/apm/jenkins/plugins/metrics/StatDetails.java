package com.apm.jenkins.plugins.metrics;

import java.util.HashMap;

public interface StatDetails {
    void setDetails(Object details);
    HashMap<String, Object> getDetails();
}
