package hu.raven.puppet.model.task.augerat

import com.fasterxml.jackson.annotation.JsonProperty

data class NetworkBean(
    @JsonProperty("decimals")
    val decimals: String,
    @JsonProperty("euclidean")
    val euclidean: Boolean,
    @JsonProperty("nodes")
    val nodeBeanList: List<NodeBean>,
)