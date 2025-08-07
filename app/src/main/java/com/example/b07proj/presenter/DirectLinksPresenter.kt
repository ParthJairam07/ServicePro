package com.example.b07proj.presenter
import android.content.Context
import com.example.b07proj.model.DirectLinksModel
import com.example.b07proj.view.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
class DirectLinksPresenter(
    private val model: DirectLinksModel = DirectLinksModel()   // single source of truth
) {

    private val _isLoading    = MutableStateFlow(true)
    val  loading              = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val  errorMessage         = _errorMessage.asStateFlow()

    private val _warmupCity   = MutableStateFlow<String?>(null)
    val  warmupCity           = _warmupCity.asStateFlow()

    private val _resources    = MutableStateFlow<List<Resource>>(emptyList())
    val  resources            = _resources.asStateFlow()

    suspend fun fetchCityAndResources(context: Context) {
        val city = model.loadWarmupCity()
        if (city == null) {
            _errorMessage.value = "Failed to load city"
            _isLoading.value    = false
            return
        }
        _warmupCity.value = city

        val resources = model.loadResources(context)
        if (resources.isEmpty()) {
            _errorMessage.value = "List of resources for $city is empty"
            _isLoading.value    = false
            return
        }
        _resources.value = resources
        _isLoading.value = false
    }
}


