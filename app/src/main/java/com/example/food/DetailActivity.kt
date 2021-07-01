package com.example.food

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.food.models.CartItem
import com.example.food.models.ItemOfList
import com.example.food.models.ShoppingCart
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    var num : Int = 1

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val product = intent.getParcelableExtra<ItemOfList>("OBJECT_INTENT")
        val title = findViewById<TextView>(R.id.product_Name)
        val description = findViewById<TextView>(R.id.product_Descr)
        val product_img = findViewById<ImageView>(R.id.product_img)
        val product_price = findViewById<TextView>(R.id.price_of_order)
        val add_button = findViewById<Button>(R.id.addToCart)


        title.text = product.product_Name
        description.text = product.product_Description
        product_price.text = product.product_Price.toString()

        Picasso.get().load(product.product_ImgUrl).fit().centerInside()
            .placeholder(R.drawable.ic_food)
            .error(R.drawable.ic_launcher_foreground).into(product_img)

        Observable.create(ObservableOnSubscribe<MutableList<CartItem>> {

            add_button.setOnClickListener { view ->

                val item = CartItem(product)
                ShoppingCart.addItem(item, num)
                Toast.makeText(this, "${product.product_Name} добавлен в корзину", Toast.LENGTH_LONG).show()
                it.onNext(ShoppingCart.getCart())

            }
        }).subscribe{ cart ->
            var quantity = CartItem(product).quantity

            cart.forEach{cartItem ->
                quantity += num
            }

        }
    }

    fun addProd(view: View){
        if (num < 99){
            num++
        }
        setText()
        countPrice()
    }

    private fun countPrice() {
        val product = intent.getParcelableExtra<ItemOfList>("OBJECT_INTENT")
        price_of_order.setText((num * product.product_Price).toString())
    }

    fun remProd(view: View) {
        if (num > 1){
            num--
        }
        setText()
        countPrice()
    }

    private fun setText() {
        txtNumbers.setText(num.toString()+"")
    }


}