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

import java.security.Security;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.strongswan.android.security.LocalCertificateKeyStoreProvider;
import org.strongswan.android.ui.MainActivity;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.core.os.HandlerCompat;

public class StrongSwanApplication extends Application
{

	private static final String TAG = StrongSwanApplication.class.getSimpleName();
	private static Context mContext;
	private final ExecutorService mExecutorService = Executors.newFixedThreadPool(4);
	private final Handler mMainHandler = HandlerCompat.createAsync(Looper.getMainLooper());

	static {
		Security.addProvider(new LocalCertificateKeyStoreProvider());
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		StrongSwanApplication.mContext = getApplicationContext();
		registerReceiver();
	}

	/**
	 * Returns the current application context
	 * @return context
	 */
	public static Context getContext()
	{
		return StrongSwanApplication.mContext;
	}

	/**
	 * Returns a thread pool to run tasks in separate threads
	 * @return thread pool
	 */
	public Executor getExecutor()
	{
		return mExecutorService;
	}

	/**
	 * Returns a handler to execute stuff by the main thread.
	 * @return handler
	 */
	public Handler getHandler()
	{
		return mMainHandler;
	}

	/*
	 * The libraries are extracted to /data/data/org.strongswan.android/...
	 * during installation.  On newer releases most are loaded in JNI_OnLoad.
	 */
	static
	{
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2)
		{
			System.loadLibrary("strongswan");

			if (MainActivity.USE_BYOD)
			{
				System.loadLibrary("tpmtss");
				System.loadLibrary("tncif");
				System.loadLibrary("tnccs");
				System.loadLibrary("imcv");
			}

			System.loadLibrary("charon");
			System.loadLibrary("ipsec");
		}
		System.loadLibrary("androidbridge");
	}

	private void registerReceiver() {
		BroadcastReceiver restrictionsReceiver = new RestrictionsReceiver();
		IntentFilter restrictionsFilter =
			new IntentFilter(Intent.ACTION_APPLICATION_RESTRICTIONS_CHANGED);
		registerReceiver(restrictionsReceiver, restrictionsFilter);
		Log.i(TAG, "registerReceiver");
	}

}
