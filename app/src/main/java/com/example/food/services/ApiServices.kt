package com.example.food.services

import com.example.food.models.*
import retrofit2.Call
import retrofit2.http.*

interface ApiServices {
    @GET("products")
    fun fetchAllProducts() : Call<List<ItemOfList>>

    @GET("users")
    fun fetchAllUsers() : Call<List<ItemOfUserLogin>>

    @POST("users/add")
    fun addUser(@Body newUser : ItemOfUsers) : Call<ItemOfUsers>

    @POST("orders/add")
    fun addOrder(@Body newOrder : ItemOfOrders) : Call<ItemOfOrders>

    @POST("order_product")
    fun addOrderProduct(@Body newOrderProd : ItemsOfOrederProduct) : Call <ItemsOfOrederProduct>

    @GET("orders")
    fun fetchAllOrders() : Call<List<ItemOfOrdersRead>>


    @PUT("users/{id_User}")
    fun updateUser(
            @Path("id_User") id: Int?,
            @Body updateUser: ItemOfUserLogin
    ) : Call<ItemOfUserLogin>

}