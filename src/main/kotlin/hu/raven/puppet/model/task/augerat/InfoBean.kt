package hu.raven.puppet.model.task.augerat

import com.fasterxml.jackson.annotation.JsonProperty

data class InfoBean(
    @JsonProperty("dataset")
    var dataset: String,

    @JsonProperty("name")
    var name: String,
)