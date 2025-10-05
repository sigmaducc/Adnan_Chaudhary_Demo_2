package com.example.matrimony.presentation.main

import android.os.Bundle
import timber.log.Timber
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.matrimony.R
import com.example.matrimony.core.images.ImageLoader
import com.example.matrimony.core.network.NetworkMonitor
import com.example.matrimony.databinding.ActivityMainBinding
import com.example.matrimony.domain.model.MatchDecision
import com.example.matrimony.domain.model.User
import com.google.android.material.snackbar.Snackbar
import android.widget.Toast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MatchesActivity : AppCompatActivity() {

	private val vm: MatchViewModel by viewModels()

    @Inject lateinit var imageLoader: ImageLoader
    @Inject lateinit var networkMonitor: NetworkMonitor

	private lateinit var binding: ActivityMainBinding

    companion object { private const val TAG = "MatchesActivity" }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
        Timber.tag(TAG).d("onCreate")
		binding = ActivityMainBinding.inflate(layoutInflater)
		setContentView(binding.root)

		setSupportActionBar(binding.toolbar)
        Timber.tag(TAG).d("Toolbar set as support action bar")

		val adapter = MatchAdapter(
			imageLoader = imageLoader,
			onAccept = {
                Timber.tag(TAG).d("Accept clicked for user=%s", it.id)
				vm.updateDecision(
					it.id,
					MatchDecision.ACCEPTED
				)
			},
			onDecline = {
                Timber.tag(TAG).d("Decline clicked for user=%s", it.id)
				vm.updateDecision(
					it.id,
					MatchDecision.DECLINED
				)
			}
		)
        Timber.tag(TAG).d("Adapter initialized")

		binding.recyclerView.layoutManager = LinearLayoutManager(this)
		binding.recyclerView.itemAnimator = null
		binding.recyclerView.setHasFixedSize(true)
		adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
		binding.recyclerView.adapter = adapter.withLoadStateFooter(
			footer = MatchLoadStateAdapter { adapter.retry() }
		)

		binding.swipeRefresh.setOnRefreshListener {
            Timber.tag(TAG).d("Swipe-to-refresh triggered")
			adapter.refresh()
		}

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { loadStates ->
                    Timber.tag(TAG).d("LoadState: refresh=%s prepend=%s append=%s", loadStates.refresh, loadStates.prepend, loadStates.append)
                    binding.swipeRefresh.isRefreshing = loadStates.refresh is LoadState.Loading
                    val refreshState = loadStates.refresh
                    if (refreshState is LoadState.Error) {
                        Timber.tag(TAG).w(refreshState.error, "Load error: %s %s", refreshState.error::class.java.simpleName, refreshState.error.message)
                        Snackbar.make(
                            binding.recyclerView,
                            refreshState.error.localizedMessage
                                ?: getString(R.string.error_failed_to_load),
                            Snackbar.LENGTH_INDEFINITE
                        )
						.setAction(getString(R.string.action_retry)) {
                            Timber.tag(TAG).d("Retry clicked")
							adapter.retry()
						}
                        .show()
                    }
                }
            }
        }

		// Observe connectivity and show a brief offline message when disconnected
		lifecycleScope.launch {
			repeatOnLifecycle(Lifecycle.State.STARTED) {
				networkMonitor.isOnline.observe(this@MatchesActivity) { online ->
                    Timber.tag(TAG).d("Connectivity changed: online=%s", online)
					if (online == false) {
						Toast.makeText(
							this@MatchesActivity,
							getString(R.string.error_offline),
							Toast.LENGTH_SHORT
						).show()
					}
				}
			}
		}

        vm.pagedUsers.observe(this@MatchesActivity) { paging: PagingData<User> ->
            Timber.tag(TAG).d("Submitting new PagingData to adapter")
            adapter.submitData(lifecycle, paging)
        }
	}
}