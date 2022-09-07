package org.strongswan.android.data

import android.content.Context
import android.content.RestrictionsManager
import android.util.Log
import androidx.preference.PreferenceManager
import org.strongswan.android.utils.Constants

class RestrictionsVpnProfileManager(context: Context) {

	companion object {
		private val TAG = RestrictionsVpnProfileManager::class.java.simpleName
	}

	private val dataSource = VpnProfileDataSource(context)
	private val pref = PreferenceManager.getDefaultSharedPreferences(context)
	private val restrictionsManager =
		context.getSystemService(Context.RESTRICTIONS_SERVICE) as RestrictionsManager

	private var savedProfile: VpnProfile? = null
	private var restrictionsProfile: VpnProfile? = null

	init {
		dataSource.open()
		savedProfile = getSavedProfile()
		restrictionsProfile = getProfileFromRestrictions()
	}

	fun updateProfile(): VpnProfile? {
		return if (restrictionsProfile?.name == savedProfile?.name) {
			dataSource.close()
			savedProfile
		} else {
			restrictionsProfile?.let { saveNewProfileToDB(it) }
			dataSource.close()
			restrictionsProfile
		}
	}

	private fun getSavedProfile(): VpnProfile? {
		dataSource.open()
		val savedProfileUuid = pref.getString(Constants.PREF_DEFAULT_VPN_PROFILE, null)
		savedProfileUuid?.let {
			val profileFromDB = dataSource.getVpnProfile(savedProfileUuid)
			profileFromDB?.let { return it } ?: return null
		} ?: return null
	}

	private fun getProfileFromRestrictions(): VpnProfile? {
		val appRestrictions = restrictionsManager.applicationRestrictions
		appRestrictions?.let {
			val vpnProfile = VpnProfile().apply {
				val vpnProfile = VpnProfile().apply {
					name = it.getString("vpn_name")
					gateway = it.getString("vpn_server")
					username = it.getString("vpn_login")
					password = it.getString("vpn_password")
					vpnType = VpnType.IKEV2_EAP
				}
			}
			return vpnProfile
		} ?: return null
	}

	private fun saveNewProfileToDB(newProfile: VpnProfile): Boolean {
		savedProfile?.let { dataSource.deleteVpnProfile(savedProfile) }
		dataSource.insertProfile(newProfile)?.let {
			pref.edit().putString(
				Constants.PREF_DEFAULT_VPN_PROFILE,
				newProfile.uuid.toString()
			).apply()
			Log.d(TAG, "DEFAULT_VPN_PROFILE updated to ${newProfile.name}")
			return true
		} ?: return false
	}

}
