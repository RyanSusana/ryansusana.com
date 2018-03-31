package com.ryansusana.site

import com.fasterxml.jackson.annotation.JsonProperty

interface Identifiable {
    @get:JsonProperty("_id")
    var id: String
}
