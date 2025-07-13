package com.example.prm392_group2_skincare_mobile.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prm392_group2_skincare_mobile.R
import com.example.prm392_group2_skincare_mobile.data.model.response.CosmeticResponse
import com.example.prm392_group2_skincare_mobile.data.repository.CosmeticRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val cosmeticRepository: CosmeticRepository) : ViewModel() {

    // LiveData for promotional banners
    private val _banners = MutableLiveData<List<Int>>()
    val banners: LiveData<List<Int>> = _banners

    // LiveData for categories
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    // LiveData for featured products
    private val _featuredProducts = MutableLiveData<Result<List<CosmeticResponse>>>()
    val featuredProducts: LiveData<Result<List<CosmeticResponse>>> = _featuredProducts

    init {
        loadHomePageData()
    }

    private fun loadHomePageData() {
        loadBanners()
        loadCategories()
        loadFeaturedProducts()
    }

    private fun loadBanners() {
        // In a real app, you would fetch these from a remote source
        _banners.value = listOf(
            R.drawable.banner_1,
            R.drawable.banner_2,
            R.drawable.banner_3
        )
    }

    private fun loadCategories() {
        // In a real app, you might fetch these from a remote source as well
        _categories.value = listOf(
            Category("Cleansers", R.drawable.ic_category_cleanser),
            Category("Sunscreens", R.drawable.ic_category_sunscreen),
            Category("Moisturizers", R.drawable.ic_category_moisturizer),
            Category("Serums", R.drawable.ic_category_serum),
            Category("Masks", R.drawable.ic_category_mask)
        )
    }

    private fun loadFeaturedProducts() {
        viewModelScope.launch {
            try {
                val response = cosmeticRepository.getCosmetics(pageIndex = 1, pageSize = 10)
                if (response.isSuccessful && response.body()?.isSuccess == true) {
                    _featuredProducts.postValue(Result.success(response.body()?.data?.items ?: emptyList()))
                } else {
                    val errorMsg = response.body()?.message ?: "Failed to load products"
                    _featuredProducts.postValue(Result.failure(Exception(errorMsg)))
                }
            } catch (e: Exception) {
                _featuredProducts.postValue(Result.failure(e))
            }
        }
    }
}