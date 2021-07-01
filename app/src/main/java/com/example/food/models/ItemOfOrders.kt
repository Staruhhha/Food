package com.example.food.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ItemOfOrders (
    var order_Adress : String? = null,
    var user_Id : String? = null,
    var payment_Id : Int? = 0
    ):Parcelable