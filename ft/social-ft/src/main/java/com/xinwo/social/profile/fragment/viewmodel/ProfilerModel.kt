package com.xinwo.social.profile.fragment.viewmodel

import com.google.gson.annotations.SerializedName

class ProfilerModel {
    @SerializedName("name")
     var mName :String = ""
         get() {
             return field
         }
         set(value) {
             field = value
         }

    @SerializedName("id")
    var mId :Int = 0
        get() {
            return field
        }
        set(value) {
            field = value
        }

}