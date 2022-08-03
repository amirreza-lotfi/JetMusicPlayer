package com.amirreza.musicplayer.feature_home.presentation.components

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.get
import com.amirreza.musicplayer.R
import org.w3c.dom.Text

class HomeActionItem(context: Context, attrs:AttributeSet?) : LinearLayout(context,attrs) {
    init{
        val view = inflate(context, R.layout.view_home_action_item,this)
        attrs?.let {
            val attributes = context.obtainStyledAttributes(attrs,R.styleable.HomeActionItem)
            val imageResource = attributes.getResourceId(R.styleable.HomeActionItem_iconImageSrc,0)
            val title = attributes.getString(R.styleable.HomeActionItem_title)
            val count = attributes.getString(R.styleable.HomeActionItem_countOfElement)

            if(imageResource!=0){
                view.findViewById<ImageView>(R.id.iconImageView).setImageResource(imageResource)
            }
            if (!title.isNullOrBlank()){
                view.findViewById<TextView>(R.id.title).text = title
            }
            if (!count.isNullOrBlank()){
                view.findViewById<TextView>(R.id.numberOfElement).text = "($count)"
            }
        }
    }
}