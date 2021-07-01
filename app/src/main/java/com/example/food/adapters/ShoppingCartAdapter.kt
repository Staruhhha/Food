package com.example.food.adapters

import android.content.Context
import android.util.Log.d
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.food.R
import com.example.food.models.CartItem
import com.example.food.models.ShoppingCart
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import kotlinx.android.synthetic.main.cart_items.view.*


class ShoppingCartAdapter(var cart: Context?, var cartItem: List<CartItem>, val onClickDelete: (Int) -> Unit) :
    RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder>() {

    private var listData = cartItem


   inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {



        fun bindItem(cartItem: CartItem, index : Int){
            Picasso.get().load(cartItem.product!!.product_ImgUrl).fit().into(itemView.image_product_cart)
            itemView.product_name_cart.text = cartItem.product!!.product_Name
            itemView.product_price_cart.text = cartItem.product!!.product_Price.toString()
            var num = cartItem.quantity
            itemView.txtNumbers_cart.text = num.toString()
            itemView.product_price_cart.text = (num*cartItem.product!!.product_Price).toString()
            d("as", "${num}")

            Observable.create(ObservableOnSubscribe<MutableList<CartItem>> {


                itemView.delete_product_cart.setOnClickListener { view ->
                    d("as", "qw ${cartItem.product!!.product_Name}")
                    val item = CartItem(cartItem.product)
                    ShoppingCart.removeItem(item)
                    Toast.makeText(itemView.context, "${cartItem.product!!.product_Name} удален", Toast.LENGTH_LONG).show()
                    onClickDelete(index)
                    it.onNext(ShoppingCart.getCart())

                }



                fun countPrice() {
                    itemView.product_price_cart.setText((num * cartItem.product!!.product_Price).toString())
                }

                fun setText() {
                    itemView.txtNumbers_cart.setText(num.toString()+"")
                    cartItem.quantity = num
                }
                itemView.minus_cart.setOnClickListener {view ->
                    val item = CartItem(cartItem.product)
                    if (num > 1){
                        num--
                        countPrice()
                        setText()
                        ShoppingCart.removeCount(item)
                    }
                    else if (num == 1){
                        itemView.minus_cart.isEnabled = false
                    }

                    it.onNext(ShoppingCart.getCart())
                    notifyDataSetChanged()
                }
                itemView.plus_cart.setOnClickListener { view ->
                    val item = CartItem(cartItem.product)
                    if (num < 99){
                        num++
                        setText()
                        countPrice()
                        ShoppingCart.addCount(item)
                    } else if (num == 99){
                        itemView.plus_cart.isEnabled = false
                    }

                    it.onNext(ShoppingCart.getCart())
                    notifyDataSetChanged()
                }
            }).subscribe()


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layout = LayoutInflater.from(cart).inflate(R.layout.cart_items, parent,false)
        return ViewHolder(layout)
    }

    override fun getItemCount(): Int = listData.size

    override fun onBindViewHolder(holder: ShoppingCartAdapter.ViewHolder, position: Int) {
        holder.bindItem(listData[position], position)

    }

    fun setItem(items: List<CartItem>){
        listData = items
        notifyDataSetChanged()
    }



}

