package com.loftechs.sample.component

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import com.loftechs.sample.R

class ProgressDialog(context: Context) : AlertDialog(context, R.style.Custom_Theme_Dialog) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.progressbar_dialog)
    }

    override fun show() {
        setCancelable(false)
        super.show()
    }
}