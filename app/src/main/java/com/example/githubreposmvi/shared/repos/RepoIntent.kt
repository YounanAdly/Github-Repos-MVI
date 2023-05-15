package com.example.githubreposmvi.shared.repos

sealed class RepoIntent {

    data class FetchRepos(val itemNumber : Int) : RepoIntent()
    data class FetchSearch(val searchText : String) : RepoIntent()
}