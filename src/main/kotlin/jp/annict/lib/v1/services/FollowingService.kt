package jp.annict.lib.v1.services

import jp.annict.lib.v1.models.User

import com.google.gson.reflect.TypeToken
import jp.annict.lib.bases.BaseService
import jp.annict.lib.interfaces.AnnictClient
import jp.annict.lib.interfaces.RequestQuery
import jp.annict.lib.interfaces.ResponseData
import jp.annict.lib.utils.JsonUtil
import jp.annict.lib.v1.enums.Order
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response

data class FollowingRequestQuery (
    val fields           : Array<String>?,
    val filter_user_id       : Long?,
    val filter_username   : String?,
    val page             : Long?,
    val per_page         : Long?,
    val sort_id          : Order?
) : RequestQuery {

    override fun url(builder: HttpUrl.Builder) : HttpUrl {
        return builder.apply {
            addPathSegment("following")

            if(fields != null && fields.isNotEmpty()) { addQueryParameter("fields", fields.joinToString(separator = ",")) }
            if(filter_user_id != null) { addQueryParameter("filter_user_id", filter_user_id.toString()) }
            if(filter_username != null && filter_username.isNotEmpty()) { addQueryParameter("filter_username", filter_username) }
            if(page != null) { addQueryParameter("page", page.toString()) }
            if(per_page != null) { addQueryParameter("per_page", per_page.toString()) }
            if(sort_id != null) { addQueryParameter("sort_id", sort_id.name) }

        }.build()
    }
}

data class FollowingResponseData(
    val users: Array<User>?,
    val total_count: Long?,
    val next_page: Long?,
    val prev_page: Long?
) : ResponseData<FollowingResponseData> {

    constructor() : this(null, null, null, null)

    override fun toDataClass(response: Response): FollowingResponseData {
        response.apply {
            JsonUtil.JSON_PARSER.parse(body?.string()).asJsonObject.apply {
                return FollowingResponseData(
                    JsonUtil.GSON.fromJson(getAsJsonArray("users"), object : TypeToken<Array<User>>() {}.type),
                    if (get("total_count").isJsonNull) null else get("total_count").asLong,
                    if (get("next_page").isJsonNull) null else get("next_page").asLong,
                    if (get("prev_page").isJsonNull) null else get("prev_page").asLong
                )
            }
        }
    }
}

class FollowingService(client: AnnictClient) : BaseService(client) {

    fun get(query: FollowingRequestQuery): FollowingResponseData {
        this.client.apply {
            return FollowingResponseData().toDataClass(request(Request.Builder().url(query.url(getUrlBuilder()))))
        }

    }
}