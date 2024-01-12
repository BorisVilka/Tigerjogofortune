package com.fortkto.gametigerone

import android.app.Application
import com.onesignal.OneSignal
import io.branch.referral.Branch

class MyApp: Application() {

    override fun onCreate() {
        super.onCreate()
        OneSignal.initWithContext(this)
        OneSignal.setAppId(ONESIGNAL_APP_ID)
        OneSignal.promptForPushNotifications()
        Branch.getAutoInstance(this)
        Branch.enableLogging()
    }

    private val ONESIGNAL_APP_ID = "232a55d9-fa38-46d3-9a2b-6409d1545e80"
}