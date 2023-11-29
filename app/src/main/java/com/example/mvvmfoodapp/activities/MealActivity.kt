package com.example.mvvmfoodapp.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.example.mvvmfoodapp.R
import com.example.mvvmfoodapp.databinding.ActivityMainBinding
import com.example.mvvmfoodapp.databinding.ActivityMealBinding
import com.example.mvvmfoodapp.db.MealDatabase
import com.example.mvvmfoodapp.fragments.HomeFragment
import com.example.mvvmfoodapp.pojo.Meal
import com.example.mvvmfoodapp.viewModel.HomeViewModel
import com.example.mvvmfoodapp.viewModel.MealViewModel
import com.example.mvvmfoodapp.viewModel.MealViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MealActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMealBinding
    private lateinit var mealId: String
    private lateinit var mealThumb: String
    private lateinit var mealName: String
    private lateinit var mealMvvm: MealViewModel
    private lateinit var youtubeLink: String
    private lateinit var mealDatabase: MealDatabase
    private lateinit var viewModelFactory: MealViewModelFactory
    private var mealToSave: Meal? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMealBinding.inflate(layoutInflater)
        setTheme(R.style.Base_Theme_MVVMFoodApp)
        setContentView(binding.root)

        loadingCase()
        getInformationFromIntent()
        setInformationViews()

        mealDatabase = MealDatabase.getInstance(this)
        viewModelFactory = MealViewModelFactory(mealDatabase)
        //   mealMvvm = ViewModelProvider(this)[MealViewModel::class.java]
        mealMvvm = ViewModelProvider(this, viewModelFactory)[MealViewModel::class.java]

        mealMvvm.getMealDetail(mealId)
        observeMealDetailsLiveData()
        onYoutubeImageClick()
        onFavoriteClick()

    }

    private fun getInformationFromIntent() {
        val intent = intent
        mealId = intent.getStringExtra(HomeFragment.MEAL_ID)!!
        mealThumb = intent.getStringExtra(HomeFragment.MEAL_THUMB)!!
        mealName = intent.getStringExtra(HomeFragment.MEAL_NAME)!!
    }

    private fun setInformationViews() {
        Glide.with(applicationContext)
            .load(mealThumb)
            .into(binding.imgMealDetail)

        binding.collapsingToolbar.title = mealName
        binding.collapsingToolbar.setExpandedTitleColor(resources.getColor(R.color.white))
        binding.collapsingToolbar.setCollapsedTitleTextColor(resources.getColor(R.color.white))
    }

    private fun observeMealDetailsLiveData() {
        mealMvvm.observeMealDetailsLiveData().observe(this, object : Observer<Meal> {
            override fun onChanged(t: Meal?) {
                onResponseCase()
                val meal = t
                mealToSave = meal
                binding.tvCategory.text = meal!!.strCategory
                binding.tvArea.text = meal!!.strArea
                binding.tvInstructionsSteps.text = meal!!.strInstructions
                youtubeLink = meal.strYoutube.toString()
            }
        })
    }

    private fun loadingCase() {
        binding.progressBar.visibility = View.VISIBLE
        binding.imgMealDetail.visibility = View.INVISIBLE
        binding.btnAddToFav.visibility = View.INVISIBLE
        binding.tvInstructions.visibility = View.INVISIBLE
        binding.tvCategory.visibility = View.INVISIBLE
        binding.tvArea.visibility = View.INVISIBLE
        binding.imgYoutube.visibility = View.INVISIBLE
    }

    private fun onResponseCase() {
        binding.progressBar.visibility = View.INVISIBLE
        binding.imgMealDetail.visibility = View.VISIBLE
        binding.btnAddToFav.visibility = View.VISIBLE
        binding.tvInstructions.visibility = View.VISIBLE
        binding.tvCategory.visibility = View.VISIBLE
        binding.tvArea.visibility = View.VISIBLE
        binding.imgYoutube.visibility = View.VISIBLE
    }

    private fun onYoutubeImageClick() {
        binding.imgYoutube.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(youtubeLink))
            startActivity(intent)
        }
    }

    private fun onFavoriteClick() {
        binding.btnAddToFav.setOnClickListener {
            mealToSave?.let {
                mealMvvm.upsertMeal(it)
                Toast.makeText(this, "You saved the ${mealToSave!!.strMeal}", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

}