package com.example.dynatrace9;

import java.util.Map;

public class Constant {
    public static final String DYNATRACE_API_TOKEN = "API_Token";
    public static final String ENTITY_SERVICE_URL = "https://macys-qa.live.dynatrace.com/api/v2/entities";
    public static final String METRICS_URL = "https://macys-qa.live.dynatrace.com/api/v2/metrics/query";
    public static final String PROBLEMS_URL = "https://macys-qa.live.dynatrace.com/api/v2/problems";
    public static final String TOPIC_NAME = "DYNATRACE_DATA_TOPIC";
    public static final String PROJECT_ID = "<place_holder_for_project_id>";
    public static final String DATASET_ID = "<data_set>";
    public static final String TABLE_ID = "<table_id>";
    public static final String DYNATRACE_CONSUMER_GROUP_ID = "dynatrace_consumer_group";

    // Parameters for specific Dynatrace API calls
    public static final Map<String, String> HOST_PARAMS = Map.of(
            "entitySelector", "type(\"host\")",
            "fields", "+lastSeenTms,+properties.gceProjectId,+properties.gceNumericProjectId,+properties.state",
            "pageSize", "500"
    );

    public static final Map<String, String> HOST_TAGS_PARAMS = Map.of(
            "entitySelector", "type(\"host\")",
            "fields", "tags",
            "pageSize", "500"
    );

    public static final Map<String, String> HOST_TO_PARAMS = Map.of(
            "entitySelector", "type(\"host\")",
            "fields", "+toRelationships.runsOnHost",
            "pageSize", "500"
    );

    public static final Map<String, String> KUBERNETES_DATA_PARAMS = Map.of(
            "entitySelector", "type(\"KUBERNETES_CLUSTER\")",
            "fields", "+properties.kubernetesClusterId",
            "pageSize", "500"
    );

    public static final Map<String, String> KUBERNETES_DATA_TAGS_PARAMS = Map.of(
            "entitySelector", "type(\"KUBERNETES_CLUSTER\")",
            "fields", "tags",
            "pageSize", "500"
    );

    public static final Map<String, String> KUBERNETES_DATA_FROM_PARAMS = Map.of(
            "entitySelector", "type(\"KUBERNETES_CLUSTER\")",
            "fields",  "+fromRelationships.isClusterOfService, +fromRelationships.isClusterOfCa, "
                    + "+fromRelationships.isClusterOfCai, +fromRelationships.isClusterOfKubernetesSvc, "
                    + "+fromRelationships.isClusterOfNode",

            "pageSize", "500"
    );

    public static final Map<String, String> KUBERNETES_DATA_TO_PARAMS = Map.of(
            "entitySelector", "type(\"KUBERNETES_CLUSTER\")",
            "fields", "+toRelationships.isCgiOfCluster",
            "pageSize", "500"
    );

    public static final Map<String, String> KUBERNETES_WORKLOAD_PARAMS = Map.of(
            "metricSelector", "builtin:kubernetes.workloads",
            "from", "now-5m",
            "resolution", "Inf"
    );

    public static final Map<String, String> NODES_PARAMS = Map.of(
            "entitySelector", "type(\"KUBERNETES_NODE\")",
            "fields", "+lastSeenTms,+toRelationships.isClusterOfNode,+properties.resourceCreationTimestamp,+toRelationships.runsOn",
            "pageSize", "500"
    );

    public static final Map<String, String> PODS_PARAMS = Map.of(
            "metricSelector", "(builtin:kubernetes.node.pods):names",
            "from", "now-5m",
            "resolution", "Inf"
    );

    public static final Map<String, String> PROBLEMS_PARAMS = Map.of(
            "from", "now-6h",
            "pageSize", "500"
    );

    public static final Map<String, String> REQUEST_COUNT_PARAMS = Map.of(
            "metricSelector", "builtin:service.requestCount.total",
            "from", "now-1h",
            "resolution", "1m"
    );

    public static final Map<String, String> RESPONSE_TIME_PARAMS = Map.of(
            "metricSelector", "builtin:service.response.time",
            "from", "now-1h",
            "resolution", "1m"
    );

    public static final Map<String, String> SERVICE_DATA_PARAMS = Map.of(
            "entitySelector", "type(\"service\")",
            "fields", "+lastSeenTms, +properties.serviceType, +properties.webApplicationId",
            "pageSize", "500"
    );

    public static final Map<String, String> SERVICE_TAGS_PARAMS = Map.of(
            "entitySelector", "type(\"service\")",
            "fields", "tags",
            "pageSize", "500"
    );

    public static final Map<String, String> SERVICE_FROM_PARAMS = Map.of(
            "entitySelector", "type(\"service\")",
            "fields", "+fromRelationships.isServiceOf, +fromRelationships.runsOn, +fromRelationships.runsOnHost, +fromRelationships.calls",
            "pageSize", "500"
    );

    public static final Map<String, String> SERVICE_TO_PARAMS = Map.of(
            "entitySelector", "type(\"service\")",
            "fields", "+toRelationships.isGroupOf,+toRelationships.isInstanceOf, +toRelationships.isNamespaceOfService,+toRelationships.isServiceMethodOfService",
            "pageSize", "500"
    );

    public static final Map<String, String> SLO_PARAMS = Map.of(
            "metricSelector","100 * builtin:service.errors.server.successCount:filter(in(\"dt.entity.service\","
                    + "entitySelector(\"type(~SERVICE~)\"))):splitBy(\"dt.entity.service\") / "
                    + "builtin:service.requestCount.server:filter(in(\"dt.entity.service\","
                    + "entitySelector(\"type(~SERVICE~)\"))):splitBy(\"dt.entity.service\")",
            "from", "now-1h",
            "resolution", "1m"
    );


}

