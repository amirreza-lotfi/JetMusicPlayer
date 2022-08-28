package com.amirreza.musicplayer.general

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.amirreza.musicplayer.R

interface JetView {
    val viewContext: Context?
    val rootView:CoordinatorLayout?

    fun showPermissionAllowedView(allowed:Boolean){
        viewContext?.let { context->
            rootView?.let { root->
                var view = root.findViewById<View>(R.id.view_permission_not_allowed)
                if(view==null){
                    view = LayoutInflater.from(context).inflate(R.layout.view_permission_not_allowed,root,false)
                    root.addView(view)
                }
                view.visibility = if(allowed) View.GONE else View.VISIBLE
            }
        }
    }
}