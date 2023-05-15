package com.example.githubreposmvi.ui.main

import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.pedant.SweetAlert.SweetAlertDialog
import com.example.githubreposmvi.BR
import com.example.githubreposmvi.R
import com.example.githubreposmvi.data.model.Repository
import com.example.githubreposmvi.shared.repos.RepoIntent
import com.example.githubreposmvi.shared.repos.RepoState
import com.example.githubreposmvi.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private var mAdapter: RepoAdapter = RepoAdapter()
    private var mList = mutableListOf<Repository>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.setVariable(BR._all, viewModel)
        binding.lifecycleOwner = this
        binding.executePendingBindings()

        binding.repoRecycler.adapter = mAdapter
        binding.repoRecycler.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            val decoration =
                DividerItemDecoration(this@MainActivity, LinearLayoutManager.VERTICAL)
            addItemDecoration(decoration)
        }

        binding.repoRecycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    if (binding.search.text?.isEmpty() == true) {
                        lifecycleScope.launch {
                            viewModel.repoIntent.send(RepoIntent.FetchRepos(mList[mList.size - 1].id))
                        }
                    }
                }
            }
        })

        binding.search.afterTextChangedDelayed {
            if (binding.search.text?.isEmpty() == true) {
                mAdapter.setRepoList(mList)
            } else {
                lifecycleScope.launch {
                    viewModel.repoIntent.send(RepoIntent.FetchSearch(it))
                }
            }
        }


        fetchRepo()
    }

    private fun fetchRepo() {
        lifecycleScope.launch {
            viewModel.repoIntent.send(RepoIntent.FetchRepos(0))

            viewModel.state.collect {
                when (it) {
                    is RepoState.Error -> errorMessage(it.errorMessage)
                    is RepoState.Idle -> Log.d(TAG, "IdleState: ")
                    is RepoState.Loading -> handleLoadingState()
                    is RepoState.RepoData -> {
                        resetUI()
                        mList.addAll(it.repo)
                        mAdapter.setRepoList(mList)
                    }
                    is RepoState.SearchData -> {
                        resetUI()
                        mAdapter.setRepoList(it.repo.items)
                    }
                }
            }
        }
    }

    private fun errorMessage(message: String) {
        SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE).setTitleText("Error")
            .setContentText(message).show()
    }

    private fun handleLoadingState() {
        // Disable user interaction and show a loading indicator
        binding.root.isEnabled = false
        binding.progressBar.visibility = View.VISIBLE
        binding.overlayView.visibility = View.VISIBLE
    }

    private fun resetUI() {
        binding.root.isEnabled = true
        binding.progressBar.visibility = View.GONE
        binding.overlayView.visibility = View.GONE
    }

    private fun TextView.afterTextChangedDelayed(afterTextChanged: (String) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            var timer: CountDownTimer? = null

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(editable: Editable?) {
                timer?.cancel()
                timer = object : CountDownTimer(1000, 1500) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        afterTextChanged.invoke(editable.toString())
                    }
                }.start()
            }
        })
    }

    companion object {
        private const val TAG = "MainActivity"
    }

}