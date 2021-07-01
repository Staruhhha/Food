package com.example.food.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemOfOrdersRead (
    val id_Order : Int
) : Parcelable