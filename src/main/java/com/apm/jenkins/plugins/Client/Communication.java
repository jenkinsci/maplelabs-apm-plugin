package com.apm.jenkins.plugins.Client;

import java.util.HashMap;

public interface Communication {
    boolean transmit(HashMap<String, Object> payload);
}
