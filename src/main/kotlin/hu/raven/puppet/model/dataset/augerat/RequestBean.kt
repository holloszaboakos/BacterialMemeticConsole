package hu.raven.puppet.model.dataset.augerat

import com.fasterxml.jackson.annotation.JsonProperty

data class RequestBean(
    @JsonProperty("id")
    val id: String,
    @JsonProperty("node")
    val node: String,
    @JsonProperty("quantity")
    val quantity: String,
)