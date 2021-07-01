package com.example.food

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.widget.*
import com.example.food.models.*
import com.example.food.services.ApiServices
import com.example.food.services.ServiceBuilder
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class  MakingOrderActivity : AppCompatActivity() {

    private lateinit var makeOrder : Button
    private lateinit var streetTxt : EditText
    private lateinit var homeTxt : EditText
    private lateinit var corpTxt : EditText
    private lateinit var flatTxt : EditText
    private lateinit var cashRdBtn : RadioButton
    private lateinit var cardRdBtn : RadioButton
    private var listForOrder : ArrayList<ItemOfOrdersRead> = ArrayList<ItemOfOrdersRead>()
    var userList : ArrayList<ItemOfUserLogin> = ArrayList<ItemOfUserLogin>()
    val URL = "https://foodapimpt.ru/api/"



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_making_order)



        makeOrder = findViewById<Button>(R.id.makeOrder)
        streetTxt = findViewById<EditText>(R.id.street)
        homeTxt = findViewById<EditText>(R.id.home)
        corpTxt = findViewById<EditText>(R.id.corp)
        flatTxt = findViewById<EditText>(R.id.flat)
        cashRdBtn = findViewById<RadioButton>(R.id.cash)
        cardRdBtn = findViewById<RadioButton>(R.id.card)

        Paper.init(this@MakingOrderActivity)

        getNumbers()

        makeOrder.setOnClickListener {
            var check = true

            if(streetTxt.text.length == 0){
                streetTxt.setError("Поле 'Улица' - обязательно для заполнения")
                check = false
            }

            if (homeTxt.text.length == 0){
                homeTxt.setError("Поле 'Дом' - обязательно для заполнения")
                check = false
            }

            if (flatTxt.text.length == 0){
                flatTxt.setError("Поле 'Квартира' - обязательно для заполнения")
                check = false
            }

            if (check){

                var idUs : Int? = 0
                val userTelCurrent : String = Paper.book().read(Prevalent.UserPhoneKey)
                d("listUser", "$userList")
                for (i  in userList){

                    if(i.user_TelNumber == userTelCurrent){
                        idUs = i.id_User
                        d("idUserCheck", "${idUs}")
                        break
                    }
                }

                val newOrder = ItemOfOrders()

                var fullAdress = ""
                var paymentUse = 0
                val userIdString : String? = idUs.toString()
                d("id", "$userIdString")
                if (corpTxt.text.length == 0) {
                    fullAdress = "${streetTxt.text} ${homeTxt.text}-${flatTxt.text}"
                }else if (corpTxt.text.length != 0){
                    fullAdress = "${streetTxt.text} ${homeTxt.text}-${corpTxt.text}-${flatTxt.text}"
                }

                if ((cashRdBtn.isChecked == true) && (cardRdBtn.isChecked == false)) paymentUse = 1
                else if ((cardRdBtn.isChecked == true) && (cashRdBtn.isChecked == false) ) paymentUse = 2

                newOrder.order_Adress = fullAdress
                newOrder.user_Id = userIdString
                newOrder.payment_Id = paymentUse

                d("red1", "${newOrder.order_Adress}")
                d("red2", "${newOrder.user_Id}")
                d("red3", "${newOrder.payment_Id}")

                val apiServices = ServiceBuilder.buildService(ApiServices::class.java)
                val requsetCall = apiServices.addOrder(newOrder)

                requsetCall.enqueue(object : retrofit2.Callback<ItemOfOrders>{
                    override fun onFailure(call: Call<ItemOfOrders>, t: Throwable) {
                        Toast.makeText(this@MakingOrderActivity, "Произошла ошибка со стороны сервера", Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(
                        call: Call<ItemOfOrders>,
                        response: Response<ItemOfOrders>) {
                        Toast.makeText(this@MakingOrderActivity, "Заказ успешно оформлен", Toast.LENGTH_LONG).show()
                        d("red", "заказ добавлен")
                        getOrdersFromServ()
                    }

                })


            }

        }

    }


    fun getOrdersFromServ(){
        val retrofit = Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val api = retrofit.create(ApiServices::class.java)
        api.fetchAllOrders().enqueue(object : Callback<List<ItemOfOrdersRead>> {
            override fun onFailure(call: Call<List<ItemOfOrdersRead>>, e: Throwable) {
                Log.e("Error", e.toString())
            }

            override fun onResponse(
                    call: Call<List<ItemOfOrdersRead>>,
                    response: Response<List<ItemOfOrdersRead>>
            ) {

                val itemList = arrayOfNulls<String>(response.body()!!.size)
                d("last", "${response.body()!!.size}")
                for (i in 0..response.body()!!.size - 1){

                    listForOrder.add(
                            ItemOfOrdersRead(
                                    response.body()!![i].id_Order
                            )
                    )

                }
                d("lastOrd", "${listForOrder.last().id_Order}")
                addOrderInServ()
            }

        })



    }


    fun addOrderInServ(){

        val cartOP = ShoppingCart.getCart()
        val targetItemOP = ItemsOfOrederProduct()

        for (i in 0..cartOP.size - 1){


            targetItemOP.order_Id = listForOrder.last().id_Order
            targetItemOP.product_Id = cartOP[i].product!!.id_Product.toInt()
            targetItemOP.count = cartOP[i].quantity

            d("okOrd", "${targetItemOP.order_Id}")
            d("okid", "${targetItemOP.product_Id}")

            val apiServicesOP = ServiceBuilder.buildService(ApiServices::class.java)
            val requsetCallOP = apiServicesOP.addOrderProduct(targetItemOP)


            requsetCallOP.enqueue(object : retrofit2.Callback<ItemsOfOrederProduct>{
                override fun onFailure(
                        call: Call<ItemsOfOrederProduct>,
                        t: Throwable
                ) {
                    d("err", "error")
                }

                override fun onResponse(
                        call: Call<ItemsOfOrederProduct>,
                        response: Response<ItemsOfOrederProduct>
                ) {
                    d("ok1111", "${targetItemOP.product_Id}")

                }

            })
        }
        Toast.makeText(this@MakingOrderActivity, "Заказ оформлен", Toast.LENGTH_SHORT).show()
        Paper.book("cart").destroy()
        startActivity(Intent(applicationContext, MainActivity::class.java))
        finish()

    }

    fun getNumbers(){
        val retrofit = Retrofit.Builder()
                .baseUrl(URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        val api = retrofit.create(ApiServices::class.java)
        api.fetchAllUsers().enqueue(object : Callback<List<ItemOfUserLogin>> {
            override fun onFailure(call: Call<List<ItemOfUserLogin>>, e: Throwable) {
                Log.e("Error", e.toString())
            }

            override fun onResponse(
                    call: Call<List<ItemOfUserLogin>>,
                    response: Response<List<ItemOfUserLogin>>
            ) {
                val itemList = arrayOfNulls<String>(response.body()!!.size)
                for (i in 0..response.body()!!.size - 1){
                    //d("as","tel : ${response.body()!![i].user_Surname}")
                    itemList[i] = response.body()!![i].user_TelNumber
                    userList.add(
                            ItemOfUserLogin(
                                    response.body()!![i].id_User,
                                    response.body()!![i].user_Name,
                                    response.body()!![i].user_Surname,
                                    response.body()!![i].user_TelNumber,
                                    response.body()!![i].user_Password
                            )
                    )
                }

            }

        })
    }

}