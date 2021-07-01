package com.example.food.models

import android.content.Context
import android.util.Log.d
import io.paperdb.Paper

class ShoppingCart {


    companion object{



        fun addItem(cartItem: CartItem, quantityOf : Int){

            val cart = ShoppingCart.getCart()

            val targetItem = cart.singleOrNull{ it.product!!.id_Product == cartItem.product!!.id_Product}
            if (targetItem == null){
                cartItem.quantity = quantityOf
                cart.add(cartItem)
            }else{
                targetItem.quantity += quantityOf
            }
            ShoppingCart.saveCart(cart)
        }

        fun removeItem(cartItem: CartItem){
            val cart = ShoppingCart.getCart()

            val targetItem = cart.singleOrNull{ it.product!!.id_Product == cartItem.product!!.id_Product}
            if (targetItem != null){

                cart.remove(targetItem)
            }
            ShoppingCart.saveCart(cart)
        }



        fun addCount(cartItem: CartItem){
            val cart = ShoppingCart.getCart()

            val targetItem = cart.singleOrNull{ it.product!!.id_Product == cartItem.product!!.id_Product}
            if (targetItem !=null){
                targetItem.quantity++
            }
            ShoppingCart.saveCart(cart)
        }

        fun removeCount(cartItem: CartItem){
            val cart = ShoppingCart.getCart()

            val targetItem = cart.singleOrNull{ it.product!!.id_Product == cartItem.product!!.id_Product}
            if (targetItem !=null){
                targetItem.quantity--
                d("as", "qq: ${targetItem.quantity}")
            }
            ShoppingCart.saveCart(cart)
        }

        fun saveCart(cart: MutableList<CartItem>) {
            Paper.book("cart").write("cart", cart)
        }

        fun getCart() : MutableList<CartItem> {
            return Paper.book("cart").read("cart", mutableListOf())
        }


        fun getShoppingCartSize() : Int {
            var cartSize = 0
            ShoppingCart.getCart().forEach {
                cartSize += it.quantity
            }
            return cartSize
        }

    }

}