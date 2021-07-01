package com.example.food.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemsOfOrederProduct (
    var order_Id : Int = 0,
    var product_Id : Int = 0,
    var count : Int = 0
):Parcelable