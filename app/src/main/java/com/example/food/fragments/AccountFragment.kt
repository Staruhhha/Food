package com.example.food.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Log.d
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.example.food.LoginActivity
import com.example.food.models.Prevalent
import com.example.food.R
import com.example.food.models.ItemOfUserLogin
import com.example.food.services.ApiServices
import com.example.food.services.ServiceBuilder
import com.google.android.material.textfield.TextInputEditText
import io.paperdb.Paper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.regex.Pattern


class AccountFragment : Fragment() {

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

    private lateinit var exitBtn : Button
    private lateinit var nameSurnam : EditText
    private lateinit var telNumbEd : EditText
    private lateinit var passwordEd : TextInputEditText
    private lateinit var editPasBtn : ImageButton
    private lateinit var applyBtn : Button
    var userList : ArrayList<ItemOfUserLogin> = ArrayList<ItemOfUserLogin>()
    val URL = "https://foodapimpt.ru/api/"
    var editOk : Boolean = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameSurnam.setText("${Paper.book().read<String>(Prevalent.UserSurnameKey)} ${Paper.book().read<String>(Prevalent.UserNameKey)}")
        telNumbEd.setText("${Paper.book().read<String>(Prevalent.UserPhoneKey)}")
        passwordEd.setText("${Paper.book().read<String>(Prevalent.UserPasswordKey)}")


        exitBtn.setOnClickListener {

            Paper.book().destroy()
            Paper.book("cart").destroy()
            startActivity(Intent(context, LoginActivity::class.java))

        }

        getNumbers()


        editPasBtn.setOnClickListener {
            if (editOk == false){
                passwordEd.isEnabled = true
                editOk = true
            } else if (editOk == true) {
                passwordEd.isEnabled = false
            }
        }

        applyBtn.setOnClickListener {

            if (editOk == true){
                var check = true

                getNumbers()

                for (i in userList){

                    if ((i.user_TelNumber == telNumbEd.text.toString()) && (i.user_TelNumber != Paper.book().read(Prevalent.UserPhoneKey))){
                        Toast.makeText(context, "Пользователь с таким номером уже существует", Toast.LENGTH_LONG).show()
                        check = false
                        break
                    }
                }

                if (!PASSWORD_PATTERN.matcher(passwordEd.text).matches()){
                    check = false
                    passwordEd.setError("Пароль должен состоять минимум 6 символов, а также содержать 1 букву латинского алфавита верхнего и нижнего регистра, 1 число, и 1 спец символ ('@','#','\','$','%','^','&','+','=')")
                }

                if ((telNumbEd.length() < 12) || (telNumbEd.text.contains("+7") == false)) {
                    check = false
                    telNumbEd.setError("Телефоный номер должен быть следующего вида: +79123456789")
                }

                if (check){

                    applyChanges()
                    editOk = false
                }
            }else if (editOk == false){

                Toast.makeText(context, "Для сохранения пароля должны быть внесены изменения", Toast.LENGTH_LONG).show()

            }


        }

    }





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? { // Inflate the layout for this fragment

        val accountView: View = inflater.inflate(R.layout.fragment_account, container, false)
        exitBtn = accountView.findViewById<Button>(R.id.exitBtn)
        nameSurnam = accountView.findViewById<EditText>(R.id.nameSurname)
        telNumbEd = accountView.findViewById<EditText>(R.id.telNumberAcc)
        passwordEd = accountView.findViewById<TextInputEditText>(R.id.userPasswordAсс)
        editPasBtn = accountView.findViewById<ImageButton>(R.id.editPassword)
        applyBtn = accountView.findViewById<Button>(R.id.applyBtn)
        return accountView
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

    fun applyChanges(){
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
        val itemOfUserUp = ItemOfUserLogin()
        itemOfUserUp.id_User = idUs
        itemOfUserUp.user_Name = Paper.book().read(Prevalent.UserNameKey)
        itemOfUserUp.user_Surname = Paper.book().read(Prevalent.UserSurnameKey)
        itemOfUserUp.user_TelNumber = Paper.book().read(Prevalent.UserPhoneKey)
        itemOfUserUp.user_Password = passwordEd.text.toString()

        d("itm", "${itemOfUserUp}")




        val destonationServ = ServiceBuilder.buildService(ApiServices::class.java)
        val reqCall = destonationServ.updateUser(itemOfUserUp.id_User, itemOfUserUp)
        d("l1", "$reqCall")
        reqCall?.enqueue(object : Callback<ItemOfUserLogin>{
            override fun onFailure(call: Call<ItemOfUserLogin>, t: Throwable) {
                d("errApp", "ooppss")
            }

            override fun onResponse(call: Call<ItemOfUserLogin>, response: Response<ItemOfUserLogin>) {
                Toast.makeText(context, "Пароль успешно изменен", Toast.LENGTH_LONG).show()
                Paper.book().destroy()
                Paper.book().write(Prevalent.UserIdKey.toString(), itemOfUserUp.id_User)
                Paper.book().write(Prevalent.UserNameKey, itemOfUserUp.user_Name)
                Paper.book().write(Prevalent.UserSurnameKey, itemOfUserUp.user_Surname)
                Paper.book().write(Prevalent.UserPhoneKey, itemOfUserUp.user_TelNumber)
                Paper.book().write(Prevalent.UserPasswordKey, itemOfUserUp.user_Password)
            }

        })
    }

}
