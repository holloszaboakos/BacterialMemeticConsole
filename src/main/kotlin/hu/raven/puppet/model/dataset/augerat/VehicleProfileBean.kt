package hu.raven.puppet.model.dataset.augerat

import com.fasterxml.jackson.annotation.JsonProperty

data class VehicleProfileBean(
    @JsonProperty("arrival_node")
    val arrivalNode: String,
    @JsonProperty("capacity")
    val capacity: String,
    @JsonProperty("departure_node")
    val departureNode: String,
    @JsonProperty("type")
    val type: String,
)