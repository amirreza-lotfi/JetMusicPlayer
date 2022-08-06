package com.amirreza.musicplayer.general

import android.content.Context
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.Fragment

open class JetFragment : Fragment(), JetView {

    override val viewContext: Context
        get() = requireContext()

    override val rootView: CoordinatorLayout
        get() = view as CoordinatorLayout
}