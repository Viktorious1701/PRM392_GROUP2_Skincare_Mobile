// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/ui/billing/GHNViewModel.kt
package com.example.prm392_group2_skincare_mobile.ui.billing

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prm392_group2_skincare_mobile.data.model.request.GetDistrictRequest
import com.example.prm392_group2_skincare_mobile.data.model.request.GetWardRequest
import com.example.prm392_group2_skincare_mobile.data.model.response.District
import com.example.prm392_group2_skincare_mobile.data.model.response.Province
import com.example.prm392_group2_skincare_mobile.data.model.response.Ward
import com.example.prm392_group2_skincare_mobile.data.repository.GHNRepository
import kotlinx.coroutines.launch

// ViewModel to manage the state of address data for the UI.
class GHNViewModel(private val repository: GHNRepository) : ViewModel() {

    private val _provinces = MutableLiveData<List<Province>>()
    val provinces: LiveData<List<Province>> = _provinces

    private val _districts = MutableLiveData<List<District>>()
    val districts: LiveData<List<District>> = _districts

    private val _wards = MutableLiveData<List<Ward>>()
    val wards: LiveData<List<Ward>> = _wards

    fun fetchProvinces() = viewModelScope.launch {
        try {
            val response = repository.getProvinces()
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                _provinces.postValue(response.body()?.data ?: emptyList())
            }
        } catch (e: Exception) {
            // Handle error
        }
    }

    fun fetchDistricts(provinceId: Int) = viewModelScope.launch {
        try {
            // Create the request object to be sent as the body.
            val request = GetDistrictRequest(provinceId = provinceId)
            val response = repository.getDistricts(request)
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                _districts.postValue(response.body()?.data ?: emptyList())
            } else {
                _districts.postValue(emptyList()) // Clear previous results on failure
            }
        } catch (e: Exception) {
            _districts.postValue(emptyList()) // Clear previous results on error
        }
    }

    fun fetchWards(districtId: Int) = viewModelScope.launch {
        try {
            // Create the request object to be sent as the body.
            val request = GetWardRequest(districtId = districtId)
            val response = repository.getWards(request)
            if (response.isSuccessful && response.body()?.isSuccess == true) {
                _wards.postValue(response.body()?.data ?: emptyList())
            } else {
                _wards.postValue(emptyList()) // Clear previous results on failure
            }
        } catch (e: Exception) {
            _wards.postValue(emptyList()) // Clear previous results on error
        }
    }
}