package com.example.githubreposmvi.data.repos

import com.example.githubreposmvi.data.model.Repository
import com.example.githubreposmvi.data.model.Search


sealed class RepoState {
    object Idle : RepoState()
    object Loading : RepoState()
    data class RepoData(val repo: List<Repository>) : RepoState()
    data class SearchData(val repo: Search) : RepoState()
    data class Error(val errorMessage: String) : RepoState()
}
