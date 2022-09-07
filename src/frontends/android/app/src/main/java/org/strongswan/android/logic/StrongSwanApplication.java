/*
 * Copyright (C) 2014 Tobias Brunner
 *
 * Copyright (C) secunet Security Networks AG
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.  See <http://www.fsf.org/copyleft/gpl.txt>.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * for more details.
 */

package org.strongswan.android.logic;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import org.strongswan.android.security.LocalCertificateKeyStoreProvider;

import java.security.Security;

public class StrongSwanApplication extends Application {

	private static final String TAG = StrongSwanApplication.class.getSimpleName();
	private static Application application;

	/*
	 * The libraries are extracted to /data/data/org.strongswan.android/...
	 * during installation.  On newer releases most are loaded in JNI_OnLoad.
	 */
	static {
		System.loadLibrary("androidbridge");
		Security.addProvider(new LocalCertificateKeyStoreProvider());
	}

	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
		registerReceiver();
	}

	/**
	 * Returns the current application context
	 *
	 * @return context
	 */
	public static Context getContext() {
		return StrongSwanApplication.application.getApplicationContext();
	}

	private void registerReceiver() {
		BroadcastReceiver restrictionsReceiver = new RestrictionsReceiver();
		IntentFilter restrictionsFilter =
			new IntentFilter(Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED);
		registerReceiver(restrictionsReceiver, restrictionsFilter);
		Log.d(TAG, "registerReceiver");
	}

}
