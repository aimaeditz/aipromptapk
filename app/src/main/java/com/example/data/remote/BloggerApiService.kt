package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Url

@JsonClass(generateAdapter = true)
data class BloggerFeedResponse(
    @Json(name = "feed") val feed: FeedData?
)

@JsonClass(generateAdapter = true)
data class FeedData(
    @Json(name = "entry") val entries: List<FeedEntry>? = emptyList()
)

@JsonClass(generateAdapter = true)
data class FeedEntry(
    @Json(name = "id") val id: TextContainer?,
    @Json(name = "title") val title: TextContainer?,
    @Json(name = "content") val content: TextContainer?,
    @Json(name = "published") val published: TextContainer?,
    @Json(name = "link") val link: List<FeedLink>? = emptyList(),
    @Json(name = "category") val categories: List<CategoryContainer>? = emptyList()
)

@JsonClass(generateAdapter = true)
data class TextContainer(
    @Json(name = "\$t") val value: String? = ""
)

@JsonClass(generateAdapter = true)
data class FeedLink(
    @Json(name = "rel") val rel: String? = "",
    @Json(name = "href") val href: String? = ""
)

@JsonClass(generateAdapter = true)
data class CategoryContainer(
    @Json(name = "term") val term: String? = ""
)

interface BloggerApiService {
    @GET
    suspend fun getBloggerFeed(@Url url: String): BloggerFeedResponse
}
