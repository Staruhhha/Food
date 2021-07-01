package com.example.food.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.food.*
import com.example.food.adapters.ItemAdapter
import com.example.food.services.ApiServices
import com.example.food.models.ItemOfList
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MenuFragment : Fragment() {

    lateinit var recyclerView: RecyclerView
    lateinit var progress_Bar: ProgressBar
    val URL = "https://foodapimpt.ru/api/"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val retrofit = Retrofit.Builder()
            .baseUrl(URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(ApiServices::class.java)
        api.fetchAllProducts().enqueue(object : retrofit2.Callback<List<ItemOfList>> {
            override fun onFailure(call: retrofit2.Call<List<ItemOfList>>, e: Throwable) {
                Log.e("Error", e.toString())
            }

            override fun onResponse(
                call: retrofit2.Call<List<ItemOfList>>,
                response: retrofit2.Response<List<ItemOfList>>
            ) {
                activity?.runOnUiThread {
                    showData(response.body()!!)
                }
                progress_Bar.visibility = View.INVISIBLE
            }

        })
    }

    private fun showData(products: List<ItemOfList>) {



        recyclerView.layoutManager =LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = ItemAdapter(context, products) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("OBJECT_INTENT", it)
            startActivity(intent)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val menuView: View = inflater.inflate(R.layout.fragment_menu, container, false)
        recyclerView = menuView.findViewById(R.id.menu_recyclerview)
        progress_Bar = menuView.findViewById(R.id.progressBar)
        progress_Bar.visibility = View.VISIBLE
        return menuView
    }

}