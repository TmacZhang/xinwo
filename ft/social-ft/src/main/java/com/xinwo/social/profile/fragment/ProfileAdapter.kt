package com.xinwo.social.profile.fragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.atech.staggedrv.StaggedAdapter
import com.xinwo.social.R

class ProfileAdapter(c: Context?) : StaggedAdapter<ProfileModel>(c) {

    override fun getItemCount(): Int {
        return datas.size
    }

    override fun addViewHolder(viewGroup: ViewGroup?, i: Int): RecyclerView.ViewHolder {

        //绑定自定义的viewholder
        val v: View = LayoutInflater.from(c)
            .inflate(R.layout.item_profile, viewGroup, false)
        return ProfileViewHolder(v)
    }

    override fun bindView(viewHolder: RecyclerView.ViewHolder?, i: Int) {
          val feedViewHolder = viewHolder as  ProfileViewHolder
        feedViewHolder.mTextView?.setText("去看周杰伦的演唱会不？")
        feedViewHolder.mImageView?.setImageResource(datas[i].localResorce())
        val layoutParams: ViewGroup.LayoutParams? = feedViewHolder.mImageView?.getLayoutParams()
        layoutParams?.height = datas.get(i).height
        feedViewHolder.mImageView?.setLayoutParams(layoutParams)
    }
}