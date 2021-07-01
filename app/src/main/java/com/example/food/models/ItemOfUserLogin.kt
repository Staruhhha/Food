package com.example.food.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemOfUserLogin (
        var id_User : Int? = 0,
        var user_Name: String? = null,
        var user_Surname: String? = null,
        var user_TelNumber: String? = null,
        var user_Password: String? = null
) : Parcelable