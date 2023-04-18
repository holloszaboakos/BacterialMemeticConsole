package hu.raven.puppet.model.task.augerat

import com.fasterxml.jackson.annotation.JsonProperty

data class FleetBean(
    @JsonProperty("vehicle_profile")
    var vehicle_profileBean: Vehicle_profileBean,
)