package hu.raven.puppet.model.task.augerat

import com.fasterxml.jackson.annotation.JsonProperty

data class Vehicle_profileBean(
    @JsonProperty("arrival_node")
    var arrival_node: String,

    @JsonProperty("capacity")
    var capacity: String,

    @JsonProperty("departure_node")
    var departure_node: String,

    @JsonProperty("type")
    var type: String,
)