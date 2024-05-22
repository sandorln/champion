package com.sandorln.champion.view.patchnote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.sandorln.champion.R
import com.sandorln.champion.databinding.ActivityChampionPatchNoteBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChampionPatchNoteActivity : ComponentActivity() {
    private val championPatchNoteViewModel: ChampionPatchNoteViewModel by viewModels()

    private lateinit var binding: ActivityChampionPatchNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_champion_patch_note)

        initView()
        initObserver()
    }

    private fun initView() {

    }

    private fun initObserver() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                championPatchNoteViewModel
                    .championList
                    .collectLatest { championList ->
                        Glide.with(binding.imgIcon)
                            .load(championList.firstOrNull()?.image ?: "")
                            .into(binding.imgIcon)
                    }
            }
        }
    }
}