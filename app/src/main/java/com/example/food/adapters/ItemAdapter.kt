package com.example.food.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.food.R
import com.example.food.models.ItemOfList
import com.squareup.picasso.Picasso

class ItemAdapter(
    private val context: Context?,
    private val products: List<ItemOfList>,
    val listener: (ItemOfList) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ImageViewHolder>(){
    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageSrc = view.findViewById<ImageView>(R.id.food_imageview)
        val foodTitle = view.findViewById<TextView>(R.id.food_title)
        val foodPrice = view.findViewById<TextView>(R.id.food_price)

        fun bindView(food: ItemOfList, listener: (ItemOfList) -> Unit) {
            foodTitle.text = food.product_Name
            foodPrice.text = food.product_Price.toString()
            itemView.setOnClickListener { listener(food) }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder =
            ImageViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list, parent, false))

    override fun getItemCount(): Int = products.size

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        var currentItem = products.get(position)
        Picasso.get().load(currentItem.product_ImgUrl).fit().centerInside()
            .placeholder(R.drawable.ic_food)
            .error(R.drawable.ic_launcher_foreground).fit().into(holder.imageSrc)

        holder.bindView(products[position], listener)
    }


}