package hu.raven.puppet.model.dataset.augerat

import com.fasterxml.jackson.annotation.JsonProperty

data class InfoBean(
    @JsonProperty("dataset")
    val dataset: String,

    @JsonProperty("name")
    val name: String,
)