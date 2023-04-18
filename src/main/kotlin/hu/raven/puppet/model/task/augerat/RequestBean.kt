package hu.raven.puppet.model.task.augerat

import com.fasterxml.jackson.annotation.JsonProperty

data class RequestBean(
    @JsonProperty("id")
    var id: String,

    @JsonProperty("node")
    var node: String,

    @JsonProperty("quantity")
    var quantity: String,
)