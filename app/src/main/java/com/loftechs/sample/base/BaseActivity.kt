package com.loftechs.sample.base

import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.loftechs.sample.R
import com.loftechs.sample.common.event.KeyActionEvent
import com.loftechs.sample.utils.PermissionUtil.bluetoothPerms
import com.loftechs.sample.utils.PermissionUtil.voicePerms
import org.greenrobot.eventbus.EventBus
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks

open class BaseActivity : FragmentActivity(), PermissionCallbacks {
    companion object {
        private const val ALL_PERMISSION = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        methodRequiresPermission()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val count = supportFragmentManager.backStackEntryCount
        if (count == 0) {
            super.onBackPressed()
        } else {
            supportFragmentManager.popBackStack()
        }
    }

    fun initFragment(fragment: Fragment, intentBundle: Bundle?) {
        fragment.arguments = intentBundle
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commitNowAllowingStateLoss()
    }

    @AfterPermissionGranted(ALL_PERMISSION)
    private fun methodRequiresPermission() {
        var permissions = voicePerms
        if (Build.VERSION.SDK_INT >= 31) {
            permissions = voicePerms.plus(bluetoothPerms)
        }
        if (!EasyPermissions.hasPermissions(this, *permissions)) {
            EasyPermissions.requestPermissions(
                this, "permission",
                ALL_PERMISSION, *permissions
            )
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {}
    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {}

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        EventBus.getDefault().post(KeyActionEvent(keyCode))
        return super.onKeyDown(keyCode, event)
    }

}