package com.example.navigation

object AppRoutes {
    const val ROUTE_HOME = "home"
    const val ROUTE_SERVICES = "services"
    const val ROUTE_FAVORITES = "favorites"
    const val ROUTE_SETTINGS = "settings"

    const val ROUTE_SERVICE_DETAILS = "service_details/{serviceId}"
    const val ROUTE_BLOOD_DONORS = "blood_donors"
    const val ROUTE_DATA_UPDATE = "data_update"
    const val ROUTE_ABOUT_PRIVACY = "about_privacy"

    fun serviceDetailsRoute(serviceId: String): String = "service_details/$serviceId"
}
