package com.fragile.utils.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fragile.kioku2.scrobbling.ScrobblerService;


public class BootCompletedIntentReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent pushIntent = new Intent(context, ScrobblerService.class);
            context.startService(pushIntent);
        }
    }
}