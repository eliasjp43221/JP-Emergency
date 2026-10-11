package com.example.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.models.BloodDonorEntry
import com.example.models.DataImportResult
import com.example.models.IssueReportEntry
import com.example.models.ServiceCategory
import com.example.models.ServiceEntry
import com.example.models.Upazila
import com.example.repositories.JamalpurRepository
import com.example.repositories.ThemeMode
import com.example.repositories.UserPreferencesRepository
import com.example.repositories.UserPreferencesState
import com.example.utils.BengaliSearchAndFormatUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FilterAndLocationState(
    val searchQuery: String = "",
    val selectedCategory: ServiceCategory = ServiceCategory.ALL,
    val selectedUpazila: Upazila = Upazila.ALL,
    val selectedBloodGroup: String = "সকল",
    val userLat: Double? = null,
    val userLng: Double? = null,
    val isNearbySortActive: Boolean = false,
    val lastImportResult: DataImportResult? = null,
    val snackbarMessageBn: String? = null,
    val isInitialized: Boolean = false
)

data class JamalpurUiState(
    val allServices: List<ServiceEntry> = emptyList(),
    val filteredServices: List<ServiceEntry> = emptyList(),
    val favoriteServices: List<ServiceEntry> = emptyList(),
    val recentlyViewedServices: List<ServiceEntry> = emptyList(),
    val bloodDonors: List<BloodDonorEntry> = emptyList(),
    val issueReports: List<IssueReportEntry> = emptyList(),
    val searchQuery: String = "",
    val selectedCategory: ServiceCategory = ServiceCategory.ALL,
    val selectedUpazila: Upazila = Upazila.ALL,
    val selectedBloodGroup: String = "সকল",
    val userLat: Double? = null,
    val userLng: Double? = null,
    val isNearbySortActive: Boolean = false,
    val preferences: UserPreferencesState = UserPreferencesState(),
    val lastImportResult: DataImportResult? = null,
    val snackbarMessageBn: String? = null,
    val categoryCounts: Map<ServiceCategory, Int> = emptyMap(),
    val upazilaCounts: Map<Upazila, Int> = emptyMap(),
    val isLoading: Boolean = true
)

class JamalpurViewModel(
    private val repository: JamalpurRepository,
    private val preferencesRepository: UserPreferencesRepository,
    appContext: Context? = null
) : ViewModel() {

    private val filterState = MutableStateFlow(FilterAndLocationState())

    init {
        viewModelScope.launch {
            repository.ensureSeedDataPopulated(appContext)
            filterState.update { it.copy(isInitialized = true) }
        }
    }

    val uiState: StateFlow<JamalpurUiState> = combine(
        repository.allServices.onStart { emit(emptyList()) },
        repository.favoriteServices.onStart { emit(emptyList()) },
        repository.recentlyViewedServices.onStart { emit(emptyList()) },
        repository.allBloodDonors.onStart { emit(emptyList()) },
        repository.allIssueReports.onStart { emit(emptyList()) }
    ) { services, favorites, recent, donors, reports ->
         QuintupleData(services, favorites, recent, donors, reports)
    }.combine(
        preferencesRepository.preferencesFlow.onStart { emit(UserPreferencesState()) }
    ) { data, prefs ->
        data to prefs
    }.combine(filterState) { (data, prefs), filter ->
        val (rawServices, favorites, recent, donors, reports) = data
        val services = rawServices.ifEmpty {
            com.example.data.VerifiedJamalpurSeedData.defaultSeedItems().map { it.toServiceEntry() }
        }

        val baseFiltered = BengaliSearchAndFormatUtils.filterServices(
            services = services,
            query = filter.searchQuery,
            selectedCategory = filter.selectedCategory,
            selectedUpazila = filter.selectedUpazila
        )

        val sortedFiltered = if (filter.isNearbySortActive && filter.userLat != null && filter.userLng != null) {
            baseFiltered.sortedBy { entry ->
                val lat = entry.latitude ?: entry.upazila.centerLat
                val lng = entry.longitude ?: entry.upazila.centerLng
                BengaliSearchAndFormatUtils.calculateDistanceKm(
                    filter.userLat,
                    filter.userLng,
                    lat,
                    lng
                )
            }
        } else {
            baseFiltered
        }

        val filteredDonors = donors.filter { donor ->
            val sampleMatch = if (prefs.showSampleDonors) true else !donor.isSampleEntry
            val groupMatch = filter.selectedBloodGroup == "সকল" ||
                donor.bloodGroup.equals(filter.selectedBloodGroup, ignoreCase = true) ||
                donor.bloodGroup == "সকল গ্রুপ"
            val upazilaMatch = filter.selectedUpazila == Upazila.ALL ||
                filter.selectedUpazila == Upazila.DISTRICT_WIDE ||
                donor.upazilaId == filter.selectedUpazila.id
            sampleMatch && groupMatch && upazilaMatch
        }

        val catCounts = ServiceCategory.displayCategories.associateWith { cat ->
            services.count { it.categoryId == cat.id }
        }

        val upzCounts = Upazila.entries.associateWith { upz ->
            when (upz) {
                Upazila.ALL -> services.size
                else -> services.count { it.upazilaId == upz.id }
            }
        }

        JamalpurUiState(
            allServices = services,
            filteredServices = sortedFiltered,
            favoriteServices = favorites,
            recentlyViewedServices = recent,
            bloodDonors = filteredDonors,
            issueReports = reports,
            searchQuery = filter.searchQuery,
            selectedCategory = filter.selectedCategory,
            selectedUpazila = filter.selectedUpazila,
            selectedBloodGroup = filter.selectedBloodGroup,
            userLat = filter.userLat,
            userLng = filter.userLng,
            isNearbySortActive = filter.isNearbySortActive,
            preferences = prefs,
            lastImportResult = filter.lastImportResult,
            snackbarMessageBn = filter.snackbarMessageBn,
            categoryCounts = catCounts,
            upazilaCounts = upzCounts,
            isLoading = !filter.isInitialized && services.isEmpty()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = run {
            val initialServices = com.example.data.VerifiedJamalpurSeedData.defaultSeedItems().map { it.toServiceEntry() }
            JamalpurUiState(
                allServices = initialServices,
                filteredServices = initialServices,
                isLoading = false
            )
        }
    )

    fun updateSearchQuery(query: String) {
        filterState.update { it.copy(searchQuery = query) }
    }

    fun clearSearchQuery() {
        filterState.update { it.copy(searchQuery = "") }
    }

    fun selectCategory(category: ServiceCategory) {
        filterState.update { it.copy(selectedCategory = category) }
    }

    fun selectUpazila(upazila: Upazila) {
        filterState.update { it.copy(selectedUpazila = upazila) }
    }

    fun resetFilters() {
        filterState.update {
            it.copy(
                searchQuery = "",
                selectedCategory = ServiceCategory.ALL,
                selectedUpazila = Upazila.ALL,
                isNearbySortActive = false
            )
        }
    }

    fun selectBloodGroup(group: String) {
        filterState.update { it.copy(selectedBloodGroup = group) }
    }

    fun updateUserLocation(lat: Double, lng: Double) {
        filterState.update {
            it.copy(
                userLat = lat,
                userLng = lng,
                isNearbySortActive = true,
                snackbarMessageBn = "আপনার বর্তমান অবস্থান অনুযায়ী কাছাকাছি সেবাসমূহ ক্রমানুসারে সাজানো হয়েছে।"
            )
        }
    }

    fun disableNearbySort() {
        filterState.update {
            it.copy(
                isNearbySortActive = false,
                snackbarMessageBn = "কাছাকাছি দূরত্বের ফিল্টার বন্ধ করা হয়েছে।"
            )
        }
    }

    fun toggleFavorite(entry: ServiceEntry) {
        viewModelScope.launch {
            repository.toggleFavorite(entry.id, entry.isFavorite)
            val msg = if (!entry.isFavorite) {
                "'${entry.nameBn}' প্রিয় তালিকায় যুক্ত করা হয়েছে।"
            } else {
                "'${entry.nameBn}' প্রিয় তালিকা থেকে সরানো হয়েছে।"
            }
            filterState.update { it.copy(snackbarMessageBn = msg) }
        }
    }

    fun markServiceViewed(serviceId: String) {
        viewModelScope.launch {
            repository.markServiceViewed(serviceId)
        }
    }

    fun clearRecentHistory() {
        viewModelScope.launch {
            repository.clearRecentHistory()
            filterState.update { it.copy(snackbarMessageBn = "সম্প্রতি দেখা সেবার ইতিহাস মুছে ফেলা হয়েছে।") }
        }
    }

    fun addVoluntaryBloodDonor(
        displayNameBn: String,
        bloodGroup: String,
        upazila: Upazila,
        contactNumber: String,
        availabilityNoteBn: String,
        isAvailabilityConfirmed: Boolean
    ) {
        viewModelScope.launch {
            repository.addVoluntaryBloodDonor(
                displayNameBn = displayNameBn,
                bloodGroup = bloodGroup,
                upazilaId = upazila.id,
                contactNumber = contactNumber,
                availabilityNoteBn = availabilityNoteBn,
                isAvailabilityConfirmed = isAvailabilityConfirmed
            )
            filterState.update {
                it.copy(snackbarMessageBn = "স্বেচ্ছাসেবী রক্তদাতা হিসেবে তথ্যটি সংরক্ষিত হয়েছে।")
            }
        }
    }

    fun removeBloodDonor(donorId: Long) {
        viewModelScope.launch {
            repository.removeBloodDonor(donorId)
            filterState.update {
                it.copy(snackbarMessageBn = "রক্তদাতার তথ্য তালিকা থেকে মুছে ফেলা হয়েছে।")
            }
        }
    }

    fun submitIssueReport(
        serviceId: String,
        serviceNameBn: String,
        issueTypeBn: String,
        detailsBn: String,
        suggestedCorrectionBn: String,
        onReportSaved: (IssueReportEntry) -> Unit = {}
    ) {
        viewModelScope.launch {
            val saved = repository.submitLocalIssueReport(
                serviceId = serviceId,
                serviceNameBn = serviceNameBn,
                issueTypeBn = issueTypeBn,
                detailsBn = detailsBn,
                suggestedCorrectionBn = suggestedCorrectionBn
            )
            filterState.update {
                it.copy(snackbarMessageBn = "রিপোর্টটি স্থানীয়ভাবে সংরক্ষিত হয়েছে। আপনি এখন এটি শেয়ার করতে পারেন।")
            }
            onReportSaved(saved)
        }
    }

    fun deleteIssueReport(reportId: Long) {
        viewModelScope.launch {
            repository.deleteIssueReport(reportId)
            filterState.update { it.copy(snackbarMessageBn = "রিপোর্টটি মুছে ফেলা হয়েছে।") }
        }
    }

    fun importJsonData(jsonString: String) {
        viewModelScope.launch {
            val result = repository.importServicesFromJson(jsonString)
            filterState.update {
                it.copy(
                    lastImportResult = result,
                    snackbarMessageBn = result.messageBn
                )
            }
        }
    }

    fun syncDefaultVerifiedDataset(context: Context? = null) {
        viewModelScope.launch {
            val result = repository.resetOrSyncDefaultSeedData(context)
            filterState.update {
                it.copy(
                    lastImportResult = result,
                    snackbarMessageBn = "ডিফল্ট যাচাইকৃত ডাটাবেস সফলভাবে সিঙ্ক করা হয়েছে।"
                )
            }
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        viewModelScope.launch {
            preferencesRepository.setThemeMode(mode)
        }
    }

    fun setDefaultUpazila(upazila: Upazila) {
        viewModelScope.launch {
            preferencesRepository.setDefaultUpazila(upazila)
            filterState.update { it.copy(selectedUpazila = upazila) }
        }
    }

    fun setShowSampleDonors(show: Boolean) {
        viewModelScope.launch {
            preferencesRepository.setShowSampleDonors(show)
        }
    }

    fun showSnackbarMessage(messageBn: String) {
        filterState.update { it.copy(snackbarMessageBn = messageBn) }
    }

    fun dismissSnackbar() {
        filterState.update { it.copy(snackbarMessageBn = null) }
    }

    private data class QuintupleData(
        val services: List<ServiceEntry>,
        val favorites: List<ServiceEntry>,
        val recent: List<ServiceEntry>,
        val donors: List<BloodDonorEntry>,
        val reports: List<IssueReportEntry>
    )

    companion object {
        fun provideFactory(
            repository: JamalpurRepository,
            preferencesRepository: UserPreferencesRepository,
            appContext: Context? = null
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return JamalpurViewModel(repository, preferencesRepository, appContext) as T
            }
        }
    }
}
