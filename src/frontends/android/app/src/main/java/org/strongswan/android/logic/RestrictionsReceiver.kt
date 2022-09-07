package org.strongswan.android.logic

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import org.strongswan.android.data.RestrictionsVpnProfileManager

class RestrictionsReceiver : BroadcastReceiver() {

	companion object {
		private val TAG = RestrictionsReceiver::class.java.simpleName
	}

	override fun onReceive(context: Context, intent: Intent?) {
		val restrictionsVpnProfileManager = RestrictionsVpnProfileManager(context)
		val newProfile = restrictionsVpnProfileManager.updateProfile()
		val serviceIntent = Intent(context, CharonVpnService::class.java)
		context.startForegroundService(serviceIntent)
		Log.i(TAG, "profile updated from receiver")
	}
}
