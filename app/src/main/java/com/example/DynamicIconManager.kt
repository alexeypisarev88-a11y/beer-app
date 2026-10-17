package com.example

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager

object DynamicIconManager {
    fun updateAppIcon(context: Context, isBeerConsumedToday: Boolean) {
        try {
            val packageManager = context.packageManager
            val packageName = context.packageName

            val emptyAlias = ComponentName(packageName, "$packageName.MainActivityAliasEmpty")
            val fullAlias = ComponentName(packageName, "$packageName.MainActivityAliasFull")

            val targetEmptyState = if (isBeerConsumedToday) {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            }

            val targetFullState = if (isBeerConsumedToday) {
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED
            } else {
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED
            }

            val currentEmptyState = packageManager.getComponentEnabledSetting(emptyAlias)
            val currentFullState = packageManager.getComponentEnabledSetting(fullAlias)

            if (currentEmptyState != targetEmptyState) {
                packageManager.setComponentEnabledSetting(
                    emptyAlias,
                    targetEmptyState,
                    PackageManager.DONT_KILL_APP
                )
            }

            if (currentFullState != targetFullState) {
                packageManager.setComponentEnabledSetting(
                    fullAlias,
                    targetFullState,
                    PackageManager.DONT_KILL_APP
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
