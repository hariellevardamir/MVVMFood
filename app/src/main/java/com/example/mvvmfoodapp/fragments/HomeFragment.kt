package com.example.mvvmfoodapp.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mvvmfoodapp.R
import com.example.mvvmfoodapp.activities.CategoryMealsActivity
import com.example.mvvmfoodapp.activities.MainActivity
import com.example.mvvmfoodapp.activities.MealActivity
import com.example.mvvmfoodapp.adapter.CategoriesAdapter
import com.example.mvvmfoodapp.adapter.MostPopularAdapter
import com.example.mvvmfoodapp.databinding.FragmentHomeBinding
import com.example.mvvmfoodapp.fragments.bottomSheet.MealBottomSheetFragment
import com.example.mvvmfoodapp.pojo.Category
import com.example.mvvmfoodapp.pojo.MealsByCategory
import com.example.mvvmfoodapp.pojo.Meal
import com.example.mvvmfoodapp.viewModel.HomeViewModel

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var viewModel: HomeViewModel
    private lateinit var randomMeal: Meal
    private lateinit var popularItemsAdapter: MostPopularAdapter
    private lateinit var categoryItemsAdapter: CategoriesAdapter
    //  private var onLongItemClick: ((MealsByCategory) -> Unit)? = null

    companion object {
        const val MEAL_ID = "com.example.mvvmfoodapp.fragments.idMeal"
        const val MEAL_NAME = "com.example.mvvmfoodapp.fragments.nameMeal"
        const val MEAL_THUMB = "com.example.mvvmfoodapp.fragments.thumbMeal"
        const val CATEGORY_NAME = "com.example.mvvmfoodapp.fragments.categoryName"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        popularItemsAdapter = MostPopularAdapter()
        categoryItemsAdapter = CategoriesAdapter()
        viewModel = (activity as MainActivity).viewModel

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        preparePopularItemsRecyclerView()
        onPopularItemClick()
        //viewModel.getRandomMeal()
        observerRandomMeal()
        onRandomMealClick()
        viewModel.getPopularItems()
        observePopularItemsLiveData()
        prepareCategoriesRecyclerView()
        viewModel.getCategories()
        observeCategoriesLiveData()
        onCategoryClick()
        onPopularItemLongClick()
        onSearchIconClick()

    }

    private fun onRandomMealClick() {
        binding.cardRandomMeal.setOnClickListener {
            val intent = Intent(activity, MealActivity::class.java)
            intent.putExtra(MEAL_ID, randomMeal.idMeal)
            intent.putExtra(MEAL_NAME, randomMeal.strMeal)
            intent.putExtra(MEAL_THUMB, randomMeal.strMealThumb)
            startActivity(intent)
        }
    }

    private fun observerRandomMeal() {
        viewModel.observeRandomMealLiveData().observe(
            viewLifecycleOwner
        ) { meal ->
            Glide.with(this@HomeFragment)
                .load(meal!!.strMealThumb)
                .into(binding.imgRandomMeal)

            this.randomMeal = meal
        }
    }

    private fun observePopularItemsLiveData() {
        viewModel.observePopularItemsLiveData()
            .observe(viewLifecycleOwner, object : Observer<List<MealsByCategory>> {
                override fun onChanged(t: List<MealsByCategory>?) {
                    val mealList = t
                    popularItemsAdapter.setMeals(mealsList = mealList as ArrayList<MealsByCategory>)
                }
            })
    }

    private fun observeCategoriesLiveData() {
        viewModel.observeCategoriesLiveData()
            .observe(viewLifecycleOwner, object : Observer<List<Category>> {
                override fun onChanged(t: List<Category>?) {
                    val categoryList = t
                    categoryList!!.forEach { category ->
                        Log.d("test", category.strCategory)
                    }
                    categoryItemsAdapter.setCategoryList(categoryList = categoryList as ArrayList<Category>)
                }
            })
    }

    private fun preparePopularItemsRecyclerView() {
        binding.recViewMealsPopular.apply {
            layoutManager = LinearLayoutManager(
                activity,
                LinearLayoutManager.HORIZONTAL, false
            )
            adapter = popularItemsAdapter
        }
    }

    private fun prepareCategoriesRecyclerView() {
        binding.recViewCategories.apply {
            layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
            adapter = categoryItemsAdapter
        }
    }

    private fun onPopularItemClick() {
        popularItemsAdapter.onItemClick = { meal ->
            val intent = Intent(activity, MealActivity::class.java)
            intent.putExtra(MEAL_ID, meal.idMeal)
            intent.putExtra(MEAL_NAME, meal.strMeal)
            intent.putExtra(MEAL_THUMB, meal.strMealThumb)
            startActivity(intent)
        }
    }

    private fun onCategoryClick() {
        categoryItemsAdapter.onItemClick = { category ->
            val intent = Intent(activity, CategoryMealsActivity::class.java)
            intent.putExtra(CATEGORY_NAME, category.strCategory)
            startActivity(intent)
        }
    }

    private fun onPopularItemLongClick() {
        popularItemsAdapter.onLongItemClick = { meal ->
            val mealBottomSheetFragment = MealBottomSheetFragment.newInstance(meal.idMeal)
            mealBottomSheetFragment.show(childFragmentManager, "Meal Info")
        }
    }

    private fun onSearchIconClick() {
        binding.imgSearch.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_searchFragment)
        }
    }

}


