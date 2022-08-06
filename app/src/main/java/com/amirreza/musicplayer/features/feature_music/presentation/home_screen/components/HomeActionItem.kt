package com.amirreza.musicplayer.features.feature_music.presentation.home_screen.components

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.amirreza.musicplayer.R

class HomeActionItem(context: Context, attrs:AttributeSet?) : LinearLayout(context,attrs) {
    private val view = inflate(context, R.layout.view_home_action_item,this)

    fun setItemCount(count:Int){
        view.findViewById<TextView>(R.id.numberOfElement).text = "($count)"
    }

    init{
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