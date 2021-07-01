package com.example.food

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.util.Log.d
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import com.example.food.services.ApiServices
import com.example.food.models.ItemOfUserLogin
import com.example.food.models.Prevalent
import com.example.food.services.ServiceBuilder
import com.google.android.material.textfield.TextInputEditText
import io.paperdb.Paper
import kotlinx.android.synthetic.main.apply_code_swap_pass.view.*
import kotlinx.android.synthetic.main.apply_password_change_finaly.view.*
import kotlinx.android.synthetic.main.forgot_password_number_enter.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

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

    val URL = "https://foodapimpt.ru/api/"
    private lateinit var loginBtn : Button
    private lateinit var telNumberEditText : EditText
    private lateinit var passwordEditText : TextInputEditText
    private lateinit var registrBtn : Button
    private lateinit var forgotLbl : TextView
    private var randomCode : Int = 0
    private var numberForRecover : String = ""
    var userList : ArrayList<ItemOfUserLogin> = ArrayList<ItemOfUserLogin>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        loginBtn = findViewById<Button>(R.id.loginBtn)
        telNumberEditText = findViewById<EditText>(R.id.userTelnumb)
        passwordEditText = findViewById<TextInputEditText>(R.id.userPassword)
        registrBtn = findViewById<Button>(R.id.registrBtn)
        forgotLbl = findViewById<TextView>(R.id.forgotPswrd)

        getNumbers()

        forgotLbl.setOnClickListener {

            val forgotPswrdNumbDialog = LayoutInflater.from(this@LoginActivity).inflate(R.layout.forgot_password_number_enter, null)
            val mBuilder = AlertDialog.Builder(this@LoginActivity)
                    .setView(forgotPswrdNumbDialog)
                    .setTitle("Восстановление пароля")
            val mAlertDialog = mBuilder.show()

            getNumbers()

            forgotPswrdNumbDialog.applyNumber.setOnClickListener {
                var check = true

                getNumbers()

                d("usersChange", "${userList}")

                d("usersChange2", "${forgotPswrdNumbDialog.dialogForgottenNumber.text}")

                var count = 0
                for (i in userList){

                    d("usersChange3", "${i.user_TelNumber}")

                    if ((i.user_TelNumber == forgotPswrdNumbDialog.dialogForgottenNumber.text.toString())){
                        count++
                        break
                    }
                }
                if(count == 0){
                    check = false
                    Toast.makeText(this, "Пользователя с таким номером не существует", Toast.LENGTH_LONG).show()
                }

                if ((forgotPswrdNumbDialog.dialogForgottenNumber.length() < 12) || (forgotPswrdNumbDialog.dialogForgottenNumber.text.contains("+7") == false)) {
                    check = false
                    forgotPswrdNumbDialog.dialogForgottenNumber.setError("Телефоный номер должен быть следующего вида: +79123456789")
                }

                if (check){

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(this,
                                arrayOf(Manifest.permission.SEND_SMS),
                                111)
                    }else{
                        numberForRecover = forgotPswrdNumbDialog.dialogForgottenNumber.text.toString()
                        randomCode = (1000..9999).random()

                        val smsManager = SmsManager.getDefault() as SmsManager
                        smsManager.sendTextMessage("${numberForRecover}", null, "${randomCode}", null, null)
                        mAlertDialog.dismiss()
                        val forgotPswrdCodeDialog = LayoutInflater.from(this@LoginActivity).inflate(R.layout.apply_code_swap_pass, null)
                        val mBuilderCode = AlertDialog.Builder(this@LoginActivity)
                                .setView(forgotPswrdCodeDialog)
                                .setTitle("Восстановление пароля")
                        val mAlertDialogCode = mBuilderCode.show()
                        forgotPswrdCodeDialog.cancelDialogCode.setOnClickListener {
                            mAlertDialogCode.dismiss()
                        }
                        forgotPswrdCodeDialog.applyCode.setOnClickListener {
                            if (randomCode.toString() == forgotPswrdCodeDialog.dialogForgottenNumberCode.text.toString()){

                                mAlertDialogCode.dismiss()
                                val changePswrdDialog = LayoutInflater.from(this@LoginActivity).inflate(R.layout.apply_password_change_finaly, null)
                                val mBuilderFinally = AlertDialog.Builder(this@LoginActivity)
                                        .setView(changePswrdDialog)
                                        .setTitle("Восстановление пароля")
                                val mAlertDialogFinally = mBuilderFinally.show()
                                changePswrdDialog.applyPswdChange.setOnClickListener {
                                    var checkPswd = true

                                    if (!PASSWORD_PATTERN.matcher(changePswrdDialog.userChangePassword.text.toString()).matches()){
                                        checkPswd = false
                                        changePswrdDialog.userChangePassword.setError("Пароль должен состоять минимум 6 символов, а также содержать 1 букву латинского алфавита верхнего и нижнего регистра, 1 число, и 1 спец символ ('@','#','\','$','%','^','&','+','=')")
                                    }

                                    if (checkPswd){
                                        var idUs : Int? = 0
                                        var userTelCurrent : String = numberForRecover
                                        var userName : String = ""
                                        var userSur : String = ""
                                        d("listUser", "$userList")
                                        for (i  in userList){

                                            if(i.user_TelNumber == userTelCurrent){
                                                idUs = i.id_User
                                                userName = i.user_Name.toString()
                                                userSur = i.user_Surname.toString()
                                                d("idUserCheck", "${idUs}")
                                                break
                                            }
                                        }
                                        val itemOfUserUp = ItemOfUserLogin()
                                        itemOfUserUp.id_User = idUs
                                        itemOfUserUp.user_Name = userName
                                        itemOfUserUp.user_Surname = userSur
                                        itemOfUserUp.user_TelNumber = userTelCurrent
                                        itemOfUserUp.user_Password = changePswrdDialog.userChangePassword.text.toString()

                                        d("itm", "${itemOfUserUp}")

                                        val destonationServ = ServiceBuilder.buildService(ApiServices::class.java)
                                        val reqCall = destonationServ.updateUser(itemOfUserUp.id_User, itemOfUserUp)
                                        d("l1", "$reqCall")
                                        reqCall?.enqueue(object : Callback<ItemOfUserLogin>{
                                            override fun onFailure(call: Call<ItemOfUserLogin>, t: Throwable) {
                                                d("errApp", "ooppss")
                                            }

                                            override fun onResponse(call: Call<ItemOfUserLogin>, response: Response<ItemOfUserLogin>) {
                                                Toast.makeText(this@LoginActivity, "Пароль успешно изменен", Toast.LENGTH_LONG).show()
                                                mAlertDialogFinally.dismiss()
                                                getNumbers()
                                            }

                                        })
                                    }
                                }

                            } else if (randomCode.toString() != forgotPswrdCodeDialog.dialogForgottenNumberCode.text.toString()) {
                                forgotPswrdCodeDialog.dialogForgottenNumberCode.setError("Введенный код не совпадает с отправленным")
                            }

                        }

                    }







                }
            }

            forgotPswrdNumbDialog.cancelDialogNumber.setOnClickListener {
                mAlertDialog.dismiss()
            }
        }

        Paper.init(this@LoginActivity)

        loginBtn.setOnClickListener {

            var count = 0

                for (i in userList){

                    if ((i.user_TelNumber == telNumberEditText.text.toString()) && (i.user_Password == passwordEditText.text.toString())){
                        Paper.book().write(Prevalent.UserIdKey.toString(),i.id_User)
                        Paper.book().write(Prevalent.UserNameKey,i.user_Name)
                        Paper.book().write(Prevalent.UserSurnameKey,i.user_Surname)
                        Paper.book().write(Prevalent.UserPhoneKey,i.user_TelNumber)
                        Paper.book().write(Prevalent.UserNameKey,i.user_Name)
                        Paper.book().write(Prevalent.UserPasswordKey, i.user_Password)
                        //d("check", "${Paper.book().read<Int>(Prevalent.UserIdKey.toString())}")
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        finish()
                        Toast.makeText(this, "Успешная авторизация", Toast.LENGTH_LONG).show()
                        count++
                        break

                    }
                }
                if(count == 0){
                    Toast.makeText(this, "Неверный номер телефона или пароль", Toast.LENGTH_LONG).show()
                }




        }

        registrBtn.setOnClickListener {

            startActivity(Intent(applicationContext, RegistrationActivity::class.java))
            finish()
        }


    }


    private fun receiveMsg() {
        TODO("Not yet implemented")
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