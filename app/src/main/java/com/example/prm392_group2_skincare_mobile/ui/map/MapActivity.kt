// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/ui/map/MapActivity.kt
package com.example.prm392_group2_skincare_mobile.ui.map

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.prm392_group2_skincare_mobile.R
import com.mapbox.maps.MapView
import com.mapbox.maps.Style

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        mapView = findViewById(R.id.mapView)

        // This is the key part. We tell the map to load the default Street Map style.
        // It happens asynchronously, so we provide a callback to know when it's done.
        mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS) {
            // This code inside the curly braces runs ONLY after the style has finished loading.
            // Here we add our log message for confirmation.
            Log.d("MapActivity", "Map style has been loaded successfully!")
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }
}