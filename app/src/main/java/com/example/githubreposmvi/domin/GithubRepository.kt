package com.example.githubreposmvi.domin

import com.example.githubreposmvi.data.`interface`.IGitHubApiService
import com.example.githubreposmvi.shared.RetrofitClient

class GithubRepository {

    private val retrofit = RetrofitClient.getInstance(IGitHubApiService::class.java)

    suspend fun getAllRepos(itemNumber : Int) = retrofit.listRepositories(itemNumber)

    suspend fun getSearchResult(searchText : String) = retrofit.getSearchResult(searchText)


}