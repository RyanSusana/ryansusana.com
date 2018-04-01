package com.ryansusana.site

import com.fasterxml.jackson.annotation.JsonCreator

data class BusinessCase  (override var id: String, val title: String, val description: Description, val link: Link, val date: String, val images: List<String>) : Identifiable
data class Link(val location: String, val linkText: String)
data class Description(val short: String, val long: String)
