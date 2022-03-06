package com.sandorln.champion.application

import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.sandorln.champion.view.activity.SplashActivity
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChampionApplication : Application() {}