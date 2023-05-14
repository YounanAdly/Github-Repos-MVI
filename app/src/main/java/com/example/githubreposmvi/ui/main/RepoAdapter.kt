package com.example.githubreposmvi.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.githubreposmvi.data.model.Repository
import com.example.githubreposmvi.databinding.ListRepoBinding

class RepoAdapter : RecyclerView.Adapter<RepoViewHolder>()  {
    var repos = mutableListOf<Repository>()

    fun setRepoList(repos: List<Repository>) {
        this.repos = repos.toMutableList()
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListRepoBinding.inflate(inflater, parent, false)
        return RepoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        holder.bind(repos[position])
    }

    override fun getItemCount(): Int {
        return repos.size
    }
}
class RepoViewHolder(val binding: ListRepoBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(data: Repository) {
        binding.repo = data
        binding.executePendingBindings()
    }

}