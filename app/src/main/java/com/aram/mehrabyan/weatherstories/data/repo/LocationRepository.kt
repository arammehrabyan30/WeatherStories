package com.aram.mehrabyan.weatherstories.data.repo

import android.location.Location

/**
 * Repository interface for location operations
 */
interface LocationRepository {
    /**
     * Get current location of the device
     * @return Location or throws exception
     */
    suspend fun getCurrentLocation(): Result<Location>

    /**
     * Check if location permissions are granted
     */
    fun hasLocationPermission(): Boolean
}