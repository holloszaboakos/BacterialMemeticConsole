package hu.raven.puppet.model.dataset.augerat

import com.fasterxml.jackson.annotation.JsonProperty

data class NodeBean(
    @JsonProperty("cx")
    val cx: String,
    @JsonProperty("cy")
    val cy: String,
    @JsonProperty("id")
    val id: String,
    @JsonProperty("type")
    val type: String,
)