package com.innovara.autoseers.api.home

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.append
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

sealed class PublicCarDataServiceState {
    data class Failed(
        val reason: String
    ): PublicCarDataServiceState()
    data class Loaded(
        val data: List<String>
    ): PublicCarDataServiceState()
    data object Loading: PublicCarDataServiceState()
}



interface PublicRecallDataService {
    suspend fun queryPublicCarYears(): Flow<PublicCarDataServiceState>
    suspend fun queryPublicCarYearMakes(year: String): Flow<PublicCarDataServiceState>
    suspend fun queryPublicCarYearMakeModels(year:
                                             String, make: String): Flow<PublicCarDataServiceState>
}

class PublicRecallDataServiceImpl: PublicRecallDataService {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
            })
        }
    }

    override suspend fun queryPublicCarYears(): Flow<PublicCarDataServiceState> = flow {
        emit(PublicCarDataServiceState.Loading)
        val publicRecallUrl = "https://api.nhtsa.gov/products/vehicle/modelYears?issueType=r"
        try {
            val response = client.get(publicRecallUrl) {
                headers { append(HttpHeaders.Accept, ContentType.Application.Json) }
            }
            println(response.bodyAsText())
            val jsonObject = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            val candidates = jsonObject["candidates"]?.jsonArray ?: emptyList()
            val extractedText = candidates.mapNotNull { candidate ->
                candidate.jsonObject["content"]?.jsonObject?.get("parts")?.jsonArray
                    ?.find { it.jsonObject["text"] != null }?.jsonObject?.get("text")?.jsonPrimitive?.content
            }
            println(extractedText)

            val publicCarYearsResponse = Json.decodeFromString<PublicCarYearsResponse>(extractedText[0])
            if (publicCarYearsResponse.count == 0) {
                emit(PublicCarDataServiceState.Loaded(
                    data = listOf("<data not found>")
                ))
            } else {
                emit(PublicCarDataServiceState.Loaded(
                    data = publicCarYearsResponse.results.map {
                        it.modelYear
                    }
                ))
            }
        } catch (e: Exception) {
            println("error: ${e.localizedMessage}")
            emit(PublicCarDataServiceState.Failed(reason = e.localizedMessage))
        }
    }

    override suspend fun queryPublicCarYearMakes(year: String): Flow<PublicCarDataServiceState> = flow {
        emit(PublicCarDataServiceState.Loading)
        val publicRecallUrl = "https://api.nhtsa.gov/products/vehicle/makes?modelYear=$year&issueType=r"
        try {
            val response = client.get(publicRecallUrl) {
                headers { append(HttpHeaders.Accept, ContentType.Application.Json) }
            }
            println(response.bodyAsText())
            val jsonObject = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            val candidates = jsonObject["candidates"]?.jsonArray ?: emptyList()
            val extractedText = candidates.mapNotNull { candidate ->
                candidate.jsonObject["content"]?.jsonObject?.get("parts")?.jsonArray
                    ?.find { it.jsonObject["text"] != null }?.jsonObject?.get("text")?.jsonPrimitive?.content
            }
            println(extractedText)

            val publicCarYearsResponse = Json.decodeFromString<PublicCarYearMakesResponse>(extractedText[0])
            if (publicCarYearsResponse.count == 0) {
                emit(PublicCarDataServiceState.Loaded(
                    data = listOf("<data not found>")
                ))
            } else {
                emit(PublicCarDataServiceState.Loaded(
                    data = publicCarYearsResponse.results.map {
                        it.make
                    }
                ))
            }
        } catch (e: Exception) {
            println("error: ${e.localizedMessage}")
            emit(PublicCarDataServiceState.Failed(reason = e.localizedMessage))
        }
    }

    override suspend fun queryPublicCarYearMakeModels(year: String, make: String): Flow<PublicCarDataServiceState> = flow {
        emit(PublicCarDataServiceState.Loading)
        val publicRecallUrl = "https://api.nhtsa.gov/products/vehicle/models?modelYear=$year&make=$make&issueType=r"
        try {
            val response = client.get(publicRecallUrl) {
                headers { append(HttpHeaders.Accept, ContentType.Application.Json) }
            }
            println(response.bodyAsText())
            val jsonObject = Json.parseToJsonElement(response.bodyAsText()).jsonObject
            val candidates = jsonObject["candidates"]?.jsonArray ?: emptyList()
            val extractedText = candidates.mapNotNull { candidate ->
                candidate.jsonObject["content"]?.jsonObject?.get("parts")?.jsonArray
                    ?.find { it.jsonObject["text"] != null }?.jsonObject?.get("text")?.jsonPrimitive?.content
            }
            println(extractedText)

            val publicCarYearsResponse = Json.decodeFromString<PublicCarYearMakeModelsResponse>(extractedText[0])
            if (publicCarYearsResponse.count == 0) {
                emit(PublicCarDataServiceState.Loaded(
                    data = listOf("<data not found>")
                ))
            } else {
                emit(PublicCarDataServiceState.Loaded(
                    data = publicCarYearsResponse.results.map {
                        it.model
                    }
                ))
            }
        } catch (e: Exception) {
            println("error: ${e.localizedMessage}")
            emit(PublicCarDataServiceState.Failed(reason = e.localizedMessage))
        }
    }
}