// PRM392_GROUP2_Skincare_Mobile/app/src/main/java/com/example/prm392_group2_skincare_mobile/ui/map/MapActivity.kt
package com.example.prm392_group2_skincare_mobile.ui.map

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.content.res.AppCompatResources
import com.example.prm392_group2_skincare_mobile.R
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Setup Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Store Location"

        mapView = findViewById(R.id.mapView)

        // The loadStyle method is asynchronous. We put all map manipulation logic
        // inside its callback to ensure the map is ready.
        mapView.mapboxMap.loadStyle(Style.MAPBOX_STREETS) {
            Log.d("MapActivity", "Map style has been loaded successfully!")

            // Get location data from the intent that started this activity
            val storeName = intent.getStringExtra("STORE_NAME")
            val latitude = intent.getDoubleExtra("LATITUDE", 0.0)
            val longitude = intent.getDoubleExtra("LONGITUDE", 0.0)

            // Only move the camera and add a marker if valid coordinates were passed
            if (latitude != 0.0 && longitude != 0.0) {
                centerMapOnLocation(latitude, longitude)
                addMarkerToMap(latitude, longitude, storeName)
            } else {
                Log.d("MapActivity", "No location data provided. Displaying default map view.")
            }
        }
    }

    private fun centerMapOnLocation(latitude: Double, longitude: Double) {
        val cameraOptions = CameraOptions.Builder()
            .center(Point.fromLngLat(longitude, latitude))
            .zoom(15.5) // A closer zoom level for a specific store
            .build()
        mapView.mapboxMap.setCamera(cameraOptions)
    }

    private fun addMarkerToMap(latitude: Double, longitude: Double, title: String?) {
        bitmapFromDrawableRes(R.drawable.red_marker)?.let { bitmap ->
            val annotationApi = mapView.annotations
            val pointAnnotationManager = annotationApi.createPointAnnotationManager()
            pointAnnotationManager.deleteAll() // Clear any existing markers

            val pointAnnotationOptions = PointAnnotationOptions()
                .withPoint(Point.fromLngLat(longitude, latitude))
                .withIconImage(bitmap)
                .withTextField(title ?: "Store Location")
                .withTextOffset(listOf(0.0, -2.5))
                .withTextColor("#D32F2F")
                .withTextSize(16.0)

            pointAnnotationManager.create(pointAnnotationOptions)
            Log.d("MapActivity", "Marker added for: ${title ?: "Unknown"}")
        }
    }

    private fun bitmapFromDrawableRes(drawableRes: Int): Bitmap? {
        val drawable: Drawable? = AppCompatResources.getDrawable(this, drawableRes)
        return if (drawable is BitmapDrawable) {
            drawable.bitmap
        } else if (drawable != null && drawable.intrinsicWidth > 0 && drawable.intrinsicHeight > 0) {
            val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        } else {
            null
        }
    }

    // Handles the back arrow on the toolbar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    // Standard MapView lifecycle methods
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