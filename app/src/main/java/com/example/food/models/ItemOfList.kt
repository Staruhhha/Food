package com.example.food.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class ItemOfList(
    val id_Product : String,
    val product_Name: String,
    val product_Description: String,
    val product_Price : Int,
    val product_ImgUrl : String
) : Parcelable