package io.jenkins.plugins.Metrics.interfaces;

import java.util.HashMap;

public interface IPublishMetrics {
    HashMap<String, Object> collectMetrics(Object details);
}
