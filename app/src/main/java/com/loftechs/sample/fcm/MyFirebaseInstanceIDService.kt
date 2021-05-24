package com.loftechs.sample.fcm

import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.messaging.FirebaseMessaging
import timber.log.Timber

class MyFirebaseInstanceIDService : FirebaseInstanceIdService() {

    companion object {
        private val TAG = MyFirebaseInstanceIDService::class.java.simpleName
    }

    override fun onTokenRefresh() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener {
            if (it.isComplete) {
                FCMTokenHelper.performUpdate()
                Timber.tag(TAG).i("onTokenRefresh ++ token: $it")
            }
        }
    }
}