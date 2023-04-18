package hu.raven.puppet.model.task.augerat

import com.fasterxml.jackson.annotation.JsonProperty

data class NodeBean(
    @JsonProperty("cx")
    var cx: String,

    @JsonProperty("cy")
    var cy: String,

    @JsonProperty("id")
    var id: String,

    @JsonProperty("type")
    var type: String,
)