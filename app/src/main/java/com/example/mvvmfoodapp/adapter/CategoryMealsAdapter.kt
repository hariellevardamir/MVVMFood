package com.example.mvvmfoodapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mvvmfoodapp.databinding.MealItemBinding
import com.example.mvvmfoodapp.pojo.Category
import com.example.mvvmfoodapp.pojo.MealsByCategory

class CategoryMealsAdapter : RecyclerView.Adapter<CategoryMealsAdapter.CategoryMealsViewHolder>() {

    private var mealsList = ArrayList<MealsByCategory>()
    var onItemClick: ((MealsByCategory) -> Unit)? = null

    inner class CategoryMealsViewHolder(val binding: MealItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    fun setMealsList(mealsList: List<MealsByCategory>) {
        this.mealsList = mealsList as ArrayList<MealsByCategory>
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryMealsViewHolder {
        return CategoryMealsViewHolder(MealItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun getItemCount(): Int {
        return mealsList.size
    }

    override fun onBindViewHolder(holder: CategoryMealsViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(mealsList[position].strMealThumb)
            .into(holder.binding.imgMeal)
        holder.binding.tvMealName.text = mealsList[position].strMeal
        holder.binding.constraintMealItem.setOnClickListener {
            //Toast.makeText(it.context, "${mealsList[position].strMeal}", Toast.LENGTH_SHORT).show()
            onItemClick!!.invoke(mealsList[position])
        }

    }

}