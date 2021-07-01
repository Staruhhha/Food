package com.example.food

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.MenuItem
import androidx.core.os.HandlerCompat.postDelayed
import androidx.fragment.app.Fragment
import com.example.food.fragments.AccountFragment
import com.example.food.fragments.CartFragment
import com.example.food.fragments.MenuFragment
import com.google.android.material.bottomnavigation.BottomNavigationMenu
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.cart_items.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.schedule

class MainActivity : AppCompatActivity() {

    lateinit var menuFragment: Fragment
    lateinit var  cartFragment: Fragment
    lateinit var accountFragment : Fragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        menuFragment = MenuFragment()
        cartFragment = CartFragment()
        accountFragment = AccountFragment()



        val bottomMenu = findViewById<BottomNavigationView>(R.id.bottom_menu)

        bottomMenu.setOnNavigationItemSelectedListener {
            when (it.itemId){
                R.id.food_item -> makeCurrentFragment(menuFragment)
                R.id.cart_item -> makeCurrentFragment(cartFragment)
                R.id.account_item -> makeCurrentFragment(accountFragment)
            }
            true
        }

        makeCurrentFragment(menuFragment)

    }

    private fun makeCurrentFragment(fragment: Fragment) =
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.frames_view, fragment)
            commit()
        }
}