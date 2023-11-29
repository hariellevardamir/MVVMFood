package com.example.mvvmfoodapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mvvmfoodapp.R
import com.example.mvvmfoodapp.adapter.CategoryMealsAdapter
import com.example.mvvmfoodapp.databinding.ActivityCategoryMealsBinding
import com.example.mvvmfoodapp.fragments.HomeFragment
import com.example.mvvmfoodapp.pojo.Category
import com.example.mvvmfoodapp.pojo.Meal
import com.example.mvvmfoodapp.pojo.MealsByCategory
import com.example.mvvmfoodapp.pojo.MealsByCategoryList
import com.example.mvvmfoodapp.viewModel.CategoryMealsViewModel

class CategoryMealsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryMealsBinding
    private lateinit var categoryMealsMvvm: CategoryMealsViewModel
    private lateinit var categoryName: String
    private lateinit var categoryMealsAdapter: CategoryMealsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryMealsBinding.inflate(layoutInflater)
        setTheme(R.style.Base_Theme_MVVMFoodApp)
        setContentView(binding.root)

        getInformationFromIntent()
        prepareRecyclerView()

        categoryMealsMvvm = ViewModelProvider(this)[CategoryMealsViewModel::class.java]

        categoryMealsMvvm.getMealsByCategory(categoryName)
        observeMealsByCategory()
        onCategoryMealsClick()

    }

    private fun prepareRecyclerView() {
        categoryMealsAdapter = CategoryMealsAdapter()
        binding.rvMeals.apply {
            layoutManager = GridLayoutManager(context, 2, GridLayoutManager.VERTICAL, false)
            adapter = categoryMealsAdapter
        }
    }

    private fun getInformationFromIntent() {
        val intent = intent
        categoryName = intent.getStringExtra(HomeFragment.CATEGORY_NAME)!!

    }

    private fun observeMealsByCategory() {
        categoryMealsMvvm.observeMealsLiveData()
            .observe(this,
                object : Observer<List<MealsByCategory>> {
                    override fun onChanged(t: List<MealsByCategory>?) {
                        val mealCategory = t
                        mealCategory!!.forEach { categories ->
                            Log.d("test", categories.strMeal)
                        }
                        binding.tvCategoryCount.text = mealCategory.size.toString()
                        categoryMealsAdapter.setMealsList(mealCategory)
                    }
                })
    }

    private fun onCategoryMealsClick() {
        categoryMealsAdapter.onItemClick = { meal ->
            val intent = Intent(this, MealActivity::class.java)
            intent.putExtra(HomeFragment.MEAL_ID, meal.idMeal)
            intent.putExtra(HomeFragment.MEAL_NAME, meal.strMeal)
            intent.putExtra(HomeFragment.MEAL_THUMB, meal.strMealThumb)
            startActivity(intent)
        }
    }

}