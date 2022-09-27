package hu.raven.puppet.model.dataset.augerat

import com.fasterxml.jackson.annotation.JsonProperty


data class InstanceBean(
    @JsonProperty("info")
    var infoBean: InfoBean,

    @JsonProperty("network")
    var networkBean: NetworkBean,

    @JsonProperty("fleet")
    var fleetBean: FleetBean,

    @JsonProperty("requests")
    var requestBeanList: List<RequestBean>,
)