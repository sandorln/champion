package com.sandorln.champion.view.patchnote

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.databinding.DataBindingUtil
import com.sandorln.champion.R
import com.sandorln.champion.databinding.ActivityChampionPatchNoteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChampionPatchNoteActivity : ComponentActivity() {

    private lateinit var binding: ActivityChampionPatchNoteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_champion_patch_note)
    }
}