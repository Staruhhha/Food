package com.example.food

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.util.Log
import android.util.Log.d
import android.widget.Toast
import com.example.food.services.ApiServices
import com.example.food.models.ItemOfUserLogin
import com.example.food.models.Prevalent
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class   SplashScreenActivity : AppCompatActivity() {

    private val handler = Handler()

    val URL = "https://foodapimpt.ru/api/"
    var userList : ArrayList<ItemOfUserLogin> = ArrayList<ItemOfUserLogin>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        allowAccess()

    }

    private fun allowAccess() {

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
                for (i in 0..response.body()!!.size - 1){
                    //d("as","tel : ${response.body()!![i].user_Surname}")
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

    private val runnable = Runnable {
        if (!isFinishing){

            Paper.init(this@SplashScreenActivity)

            val userPhoneString : String? = Paper.book().read(Prevalent.UserPhoneKey)
            val userPasswordString : String? = Paper.book().read(Prevalent.UserPasswordKey)


            if (userPhoneString != null && userPasswordString != null){

                d("as","as : ${userPhoneString}")

                if(!TextUtils.isEmpty(userPhoneString) && !TextUtils.isEmpty(userPasswordString)){

                    allowAccess()
                    d("as","as : ${userPhoneString}")
                    for (i in userList){
                        d("as","tel : ${i.user_TelNumber}")
                        if ((i.user_TelNumber == userPhoneString) && (i.user_Password == userPasswordString)){
                            startActivity(Intent(applicationContext, MainActivity::class.java))
                            finish()
                            Toast.makeText(this, "Добро пожаловать", Toast.LENGTH_LONG).show()
                            break

                        }
                    }


                }
            }else{
                    d("as","as : ${userPhoneString}")
                    startActivity(Intent(applicationContext, LoginActivity::class.java))
                    finish()
            }


        }
    }

    override fun onResume() {
        super.onResume()
        handler.postDelayed(runnable, 2000)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(runnable)


    }


}