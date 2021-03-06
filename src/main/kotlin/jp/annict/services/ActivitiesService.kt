package jp.annict.services

import jp.annict.client.AnnictClient
import jp.annict.enums.Order
import jp.annict.exception.AnnictError
import jp.annict.models.Activity
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response

data class ActivitiesGetRequestQuery (
    val fields           : Array<String>? =null,
    val filter_user_id       : Long? =null,
    val filter_username   : String? =null,
    val page             : Long? =null,
    val per_page         : Long? =null,
    val sort_id          : Order? =null
) {

    internal fun url(builder: HttpUrl.Builder) : HttpUrl {
        return builder.apply {
            addPathSegment("activities")

            if(fields != null && fields.isNotEmpty()) { addQueryParameter("fields", fields.joinToString(separator = ",")) }
            if(filter_user_id != null) { addQueryParameter("filter_user_id", filter_user_id.toString()) }
            if(filter_username != null && filter_username.isNotEmpty()) { addQueryParameter("filter_username", filter_username) }
            if(page != null) { addQueryParameter("page", page.toString()) }
            if(per_page != null) { addQueryParameter("per_page", per_page.toString()) }
            if(sort_id != null) { addQueryParameter("sort_id", sort_id.toString()) }

        }.build()
    }
}

@Serializable
data class ActivitiesGetResponseData (
    val activities: Array<Activity>? = null,
    val total_count: Long? = null,
    val next_page: Long? = null,
    val prev_page: Long? = null
)  {

    constructor() : this(null, null, null, null)

    internal fun parse(response: Response): ActivitiesGetResponseData? {
         response.apply {
             if(response.code != 200) {
                 return throw AnnictError(response.message)
             }
             return body?.string()?.let { Json { isLenient = true }.decodeFromString<ActivitiesGetResponseData>(it) }
         }
    }
}

class ActivitiesService(val client: AnnictClient) {

    internal fun get(query: ActivitiesGetRequestQuery) : ActivitiesGetResponseData? {
        this.client.apply { return ActivitiesGetResponseData()
            .parse(request(Request.Builder().url(query.url(getUrlBuilder())))) }
    }
}