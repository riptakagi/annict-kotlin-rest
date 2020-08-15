package jp.annict.rest.v1.services

import com.google.gson.reflect.TypeToken
import jp.annict.rest.bases.BaseAnnictClient
import jp.annict.rest.bases.BaseService
import jp.annict.rest.interfaces.AnnictClient
import jp.annict.rest.interfaces.RequestQuery
import jp.annict.rest.interfaces.ResponseData
import jp.annict.rest.utils.JsonUtil
import jp.annict.rest.v1.enums.Order
import jp.annict.rest.v1.models.Activity
import jp.annict.rest.v1.models.Cast
import okhttp3.HttpUrl
import okhttp3.Request
import okhttp3.Response

data class CastsRequestQuery(val fields           : Array<String>?,
                             val filter_ids       : Array<Long>?,
                             val filter_work_id   : Long?,
                             val page             : Long?,
                             val per_page         : Long?,
                             val sort_id          : Order?,
                             val sort_sort_number : Order?) : RequestQuery {

    override fun url(builder: HttpUrl.Builder): HttpUrl {
        return builder.apply {
            addPathSegment("casts")

            if(fields != null && fields.isNotEmpty()) { addQueryParameter("fields", fields.joinToString(separator = ",")) }
            if(filter_ids != null && filter_ids.isNotEmpty()) { addQueryParameter("filter_ids", filter_ids.joinToString(separator = ",")) }
            if(filter_work_id != null) { addQueryParameter("filter_work_id", filter_work_id.toString()) }
            if(page != null) { addQueryParameter("page", page.toString()) }
            if(per_page != null) { addQueryParameter("per_page", per_page.toString()) }
            if(sort_id != null) { addQueryParameter("sort_id", sort_id.name) }
            if(sort_sort_number != null) { addQueryParameter("sort_sort_number", sort_sort_number.name) }

        }.build()
    }
}

data class CastsResponseData(val casts: Array<Cast>?,
                             val total_count: Long?,
                             val next_page: Long?,
                             val prev_page: Long?) : ResponseData<CastsResponseData> {

    constructor() : this(null, null, null, null)

    override fun toDataClass(response: Response): CastsResponseData {
        response.apply { JsonUtil.JSON_PARSER.parse(body?.string()).asJsonObject.apply { return CastsResponseData (
            JsonUtil.GSON.fromJson(getAsJsonArray("casts"), object : TypeToken<Array<Cast>>() {}.type),
            if (get("total_count").isJsonNull) null else get("total_count").asLong,
            if (get("next_page").isJsonNull) null else get("next_page").asLong,
            if (get("prev_page").isJsonNull) null else get("prev_page").asLong
        ) }
        }
    }
}

class CastsService(client: AnnictClient) : BaseService(client) {

    fun get(query: CastsRequestQuery) : CastsResponseData {
        this.client.apply {
            return CastsResponseData().toDataClass(request(Request.Builder().url(query.url(getUrlBuilder()))))
        }
    }
}