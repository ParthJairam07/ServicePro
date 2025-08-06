package com.example.b07proj.presenter;

import android.content.Context
import com.example.b07proj.model.DirectLinksModel
import com.example.b07proj.view.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class DirectLinksPresenter() {
    // State flow for loading state and error messages
    private val _isLoading = MutableStateFlow<Boolean>(true)
    val loading = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    // State flow for city and resources
    private val _warmupCity = MutableStateFlow<String?>(null)
    val warmupCity = _warmupCity.asStateFlow()

    private val _resources = MutableStateFlow<List<Resource>>(emptyList())
    val resources = _resources.asStateFlow()

     // Initialize the model
     private val model = DirectLinksModel()

    // Fetch city and resources from the model
    suspend fun fetchCityAndResources(context: Context) {
        val city = model.loadWarmupCity()
        if (city == null) {
            _errorMessage.value = "Failed to load city"
            _isLoading.value = false
            return
        }
        // Update the state with the city
        _warmupCity.value = city
        // Fetch resources for the city
        val resources = model.loadResources(context)
        if (resources.isEmpty()) {
            _errorMessage.value = "List of resources for $city is empty"
            _isLoading.value = false
            return
        }
        // Update the state with the resources
        _resources.value = resources
        _isLoading.value = false
    }
}
