package com.example.githubreposmvi.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.githubreposmvi.data.repos.RepoIntent
import com.example.githubreposmvi.data.repos.RepoState
import com.example.githubreposmvi.domin.GithubRepository
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    val repoIntent = Channel<RepoIntent>(Channel.UNLIMITED)
    private val _state = MutableStateFlow<RepoState>(RepoState.Idle)
    val state: StateFlow<RepoState> get() = _state


    init {
        getRepoData()
    }

    private fun getRepoData() {
        viewModelScope.launch {
            repoIntent.consumeAsFlow().collect() {
                when (it) {
                    is RepoIntent.FetchRepos -> fetchRepos(it.itemNumber)
                    is RepoIntent.FetchSearch -> fetchSearch(it.searchText)
                }
            }
        }
    }

    private fun fetchRepos(itemNumber: Int) {
        viewModelScope.launch {
            _state.value = RepoState.Loading
            _state.value = try {
                RepoState.RepoData(GithubRepository().getAllRepos(itemNumber = itemNumber))
            } catch (e: Exception) {
                RepoState.Error(e.message.toString())
            }
        }
    }

    private fun fetchSearch(searchText: String) {
        viewModelScope.launch {
            _state.value = RepoState.Loading
            _state.value = try {
                RepoState.SearchData(GithubRepository().getSearchResult(searchText = searchText))
            } catch (e: Exception) {
                RepoState.Error(e.message.toString())
            }
        }
    }


}