package com.example.matrimony.presentation.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.matrimony.R
import com.example.matrimony.domain.model.MatchDecision
import com.example.matrimony.domain.model.User
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MatchesActivity : AppCompatActivity() {

    private val vm: MatchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: MaterialToolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val swipe: SwipeRefreshLayout = findViewById(R.id.swipeRefresh)
        val recycler: RecyclerView = findViewById(R.id.recyclerView)

        val adapter = MatchAdapter(
            onAccept = {
                vm.updateDecision(
                    it.id,
                    MatchDecision.ACCEPTED
                )
            },
            onDecline = {
                vm.updateDecision(
                    it.id,
                    MatchDecision.DECLINED
                )
            }
        )

        recycler.layoutManager = LinearLayoutManager(this)
        recycler.itemAnimator = null
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        recycler.adapter = adapter.withLoadStateFooter(
            footer = MatchLoadStateAdapter { adapter.retry() }
        )

        swipe.setOnRefreshListener { adapter.refresh() }

        lifecycleScope.launchWhenStarted {
            adapter.loadStateFlow.collectLatest { loadStates ->
                swipe.isRefreshing = loadStates.refresh is LoadState.Loading
                val refreshState = loadStates.refresh
                if (refreshState is LoadState.Error) {
                    Snackbar.make(
                        recycler,
                        refreshState.error.localizedMessage ?: "Failed to load.",
                        Snackbar.LENGTH_INDEFINITE
                    )
                    .setAction("Retry") { adapter.retry() }
                    .show()
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            vm.pagedUsers.collectLatest { paging: PagingData<User> ->
                adapter.submitData(paging)
            }
        }
    }
}