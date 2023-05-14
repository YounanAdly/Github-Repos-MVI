package com.example.githubreposmvi.data.`interface`

import com.example.githubreposmvi.data.model.Repository
import com.example.githubreposmvi.data.model.Search
import com.example.githubreposmvi.shared.ConstantLinks
import retrofit2.http.GET
import retrofit2.http.Query

interface IGitHubApiService {

    @GET(ConstantLinks.GetRepos)
    suspend fun listRepositories(
        @Query("since") page: Int
    ): List<Repository>

    @GET(ConstantLinks.SearchParam)
    suspend fun getSearchResult(
        @Query("q") searchText: String,
    ): Search

}