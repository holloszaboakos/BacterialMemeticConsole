package hu.raven.puppet.model.dataset.augerat

import com.fasterxml.jackson.annotation.JsonProperty


data class InstanceBean(
    @JsonProperty("info")
    val infoBean: InfoBean,
    @JsonProperty("network")
    val networkBean: NetworkBean,
    @JsonProperty("fleet")
    val fleetBean: FleetBean,
    @JsonProperty("requests")
    val requestBeanList: List<RequestBean>,
)