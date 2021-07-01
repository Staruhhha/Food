package com.example.food

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.food.services.ApiServices
import com.example.food.services.ServiceBuilder
import com.example.food.models.ItemOfUserLogin
import com.example.food.models.ItemOfUsers
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern

class RegistrationActivity : AppCompatActivity() {

    val PASSWORD_PATTERN : Pattern =
            Pattern.compile("^" +
                            //"(?=.*[0-9])" +
                            "(?=.*[a-z])" +
                            "(?=.*[A-Z])" +
                            //"(?=.*[a-zA-Z])" +
                            "(?=.*[@#$%^&+=])" +
                            "(?=\\S+$)" +
                            ".{6,}" +
                            "$")

    private lateinit var registrationBtn : Button
    private lateinit var userSurname : EditText
    private lateinit var userName : EditText
    private lateinit var userRegTelNumber : EditText
    private lateinit var userRegPassword : TextInputEditText
    private lateinit var userRepassword : TextInputEditText
    var userList : ArrayList<ItemOfUserLogin> = ArrayList<ItemOfUserLogin>()
    val URL = "https://foodapimpt.ru/api/"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        registrationBtn = findViewById<Button>(R.id.registrationBtn)
        userSurname = findViewById<EditText>(R.id.userSurname)
        userName = findViewById<EditText>(R.id.userName)
        userRegTelNumber = findViewById<EditText>(R.id.userRegTelnumb)
        userRegPassword = findViewById<TextInputEditText>(R.id.userRegPassword)
        userRepassword = findViewById<TextInputEditText>(R.id.userRepassword)

        registrationBtn.setOnClickListener {
            var check = true

            getNumbers()

            for (i in userList){

                if ((i.user_TelNumber == userRegTelNumber.text.toString())){
                    Toast.makeText(this, "Пользователь с таким номером уже существует", Toast.LENGTH_LONG).show()
                    check = false
                    break
                }

            }

            if (userSurname.length() == 0){
                check = false
                userSurname.setError("Поле 'Фамилия' - обязательно для заполнения")
            }

            if (userName.length() == 0){
                check = false
                userName.setError("Поле 'Имя' - обязательно для заполнения")
            }

            if (!PASSWORD_PATTERN.matcher(userRegPassword.text).matches()){
                check = false
                userRegPassword.setError("Пароль должен состоять минимум 6 символов, а также содержать 1 букву латинского алфавита верхнего и нижнего регистра, 1 число, и 1 спец символ ('@','#','\','$','%','^','&','+','=')")
            }

            if(!userRegPassword.getText().toString().equals(userRepassword.getText().toString())){
                check = false
                userRepassword.setError("Пароли не совпадают")
            }

            if ((userRegTelNumber.length() < 12) || (userRegTelNumber.text.contains("+7") == false)) {
                check = false
                userRegTelNumber.setError("Телефоный номер должен быть следующего вида: +79123456789")
            }

            d("aw", "aw: ${userRegPassword.text}")
            d("as","aw: ${userRepassword.text}")
            if(check){

                val newUser = ItemOfUsers()

                newUser.user_Name = userName.text.toString()
                newUser.user_Surname = userSurname.text.toString()
                newUser.user_TelNumber = userRegTelNumber.text.toString()
                newUser.user_Password = userRegPassword.text.toString()

                val apiServices = ServiceBuilder.buildService(ApiServices::class.java)
                val requsetCall = apiServices.addUser(newUser)

                requsetCall.enqueue(object : Callback<ItemOfUsers>{
                    override fun onFailure(call: Call<ItemOfUsers>, t: Throwable) {
                        Toast.makeText(this@RegistrationActivity, "Произошла ошибка со стороны сервера", Toast.LENGTH_LONG).show()
                    }

                    override fun onResponse(call: Call<ItemOfUsers>, response: Response<ItemOfUsers>) {
                            Toast.makeText(this@RegistrationActivity, "Успешная регистрация", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(applicationContext, LoginActivity::class.java))
                            finish()
                    }

                })

            }

        }
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