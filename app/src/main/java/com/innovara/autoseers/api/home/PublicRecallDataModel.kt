package com.innovara.autoseers.api.home

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PublicCarYearsResponse(
    @SerialName("Count")
    val count:  Int,
    @SerialName("Message")
    val message: String,
    val results: MutableList<PublicCarYearObject>
)

@Serializable
data class PublicCarYearObject(
    @SerialName("modelYear")
    val modelYear: String
)

@Serializable
data class PublicCarYearMakesResponse(
    @SerialName("Count")
    val count:  Int,
    @SerialName("Message")
    val message: String,
    val results: MutableList<PublicCarYearMakeObject>
)

@Serializable
data class PublicCarYearMakeObject(
    @SerialName("modelYear")
    val modelYear: String,
    val make: String
)

@Serializable
data class PublicCarYearMakeModelsResponse(
    @SerialName("Count")
    val count:  Int,
    @SerialName("Message")
    val message: String,
    val results: MutableList<PublicCarYearMakeModelObject>
)

@Serializable
data class PublicCarYearMakeModelObject(
    @SerialName("modelYear")
    val modelYear: String,
    val make: String,
    val model: String
)