package com.example.food.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.food.MakingOrderActivity
import com.example.food.R
import com.example.food.adapters.ShoppingCartAdapter
import com.example.food.models.CartItem
import com.example.food.models.ShoppingCart


class CartFragment : Fragment() {

    private lateinit var totalPriceTV: TextView
    private lateinit var cartRv : RecyclerView
    private lateinit var nextBtn : Button
    private lateinit var dltBtn : ImageButton
    private lateinit var adapter: ShoppingCartAdapter
    private lateinit var crtFrg : CartFragment
    private lateinit var cartView: View
    private lateinit var data : MutableList<CartItem>
    var totalPrice: Double? = 0.0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        data = ShoppingCart.getCart() as MutableList<CartItem>

        adapter = ShoppingCartAdapter(context,data){index->deleteItem(index)}
        adapter.notifyDataSetChanged()
        cartRv.adapter = adapter
        cartRv.layoutManager = LinearLayoutManager(activity)

        nextBtn.setOnClickListener {
            val count = ShoppingCart.getShoppingCartSize()

            if (count < 1){
                Toast.makeText(context, "Ваша корзина пуста", Toast.LENGTH_LONG).show()
            }else if (count >= 1){
                startActivity(Intent(context, MakingOrderActivity::class.java))
            }
        }

        Thread(Runnable {
            // performing some dummy time taking operation
            var i=0;
            while(i<Int.MAX_VALUE){
                i++
                activity?.runOnUiThread(java.lang.Runnable {
                    countPrice()

                })
                Thread.sleep(1000)
            }

            // try to touch View of UI thread

        }).start()

        countPrice()


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        cartView = inflater.inflate(R.layout.fragment_cart, container, false)
        crtFrg = CartFragment()
        val cartItem = inflater.inflate(R.layout.cart_items,container,false)
        totalPriceTV = cartView.findViewById<TextView>(R.id.total_price)
        cartRv = cartView.findViewById<RecyclerView>(R.id.cart_rv)
        nextBtn = cartView.findViewById<Button>(R.id.next_btn)
        dltBtn = cartItem.findViewById<ImageButton>(R.id.delete_product_cart)
        return cartView
    }


    fun deleteItem(index: Int){
        data.removeAt(index)
        adapter.setItem(data)
    }

    fun countPrice(){
        totalPrice = ShoppingCart.getCart()
                .fold(0.toDouble()) {acc, cartItem -> acc + cartItem.quantity.times(cartItem.product!!.product_Price!!.toDouble()) }
        totalPriceTV.text   = "Сумма заказа: $totalPrice руб."
    }


}