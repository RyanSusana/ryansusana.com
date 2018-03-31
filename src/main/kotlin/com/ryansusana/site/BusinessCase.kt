package com.ryansusana.site

data class BusinessCase(override var id: String,val title: String, val description: String, val date: String, val images: List<String>) : Identifiable