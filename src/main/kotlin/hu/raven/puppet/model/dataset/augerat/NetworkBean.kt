package hu.raven.puppet.model.dataset.augerat

import com.fasterxml.jackson.annotation.JsonProperty

data class NetworkBean(
    @JsonProperty("decimals")
    var decimals: String,

    @JsonProperty("euclidean")
    var euclidean: Boolean,

    @JsonProperty("nodes")
    var nodeBeanList: List<NodeBean>,
)