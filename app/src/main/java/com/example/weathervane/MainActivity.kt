package com.example.weathervane

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.weathervane.R.id.toolbar
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(toolbar))

        getForecast("2648579")
    }

    fun getForecast(locationId: String): String {
        var locationName: String = "Error"
        val windData = findViewById<TextView>(R.id.windData)
        val description = findViewById<TextView>(R.id.description)

        val queue = Volley.newRequestQueue(this)
        val url = "https://weather-broker-cdn.api.bbci.co.uk/en/mobile/$locationId"

        val stringRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                // Display the first 500 characters of the response string.
                val forecasts = response.getJSONArray("forecasts")[0] as JSONObject
                val summary = forecasts.getJSONObject("summary")
                val report = summary.getJSONObject("report")
                val windSpeedMph = report.getInt("windSpeedMph")
                val windDescription = report.getString("windDescription")
                val windDirection = report.getString("windDirection")

                val location = response.getJSONObject("location").getString("name")
                locationName = location

                windData.text = "Wind speed is $windSpeedMph miles per hour in $location."
                description.text = "$windDescription."

                animateVane(windDirection)

                val vaneText = findViewById<TextView>(R.id.centreText)
                vaneText.text = windSpeedMph.toString()


            },
            {
                windData.text = "That didn't work!"
            })

        queue.add(stringRequest)
        return locationName
    }

    private fun animateVane(windDirection: String) {
        when (windDirection) {
            "SW" -> rotateVane(45, windDirection)
            "W" -> rotateVane(90, windDirection)
            "NW" -> rotateVane(135, windDirection)
            "N" -> rotateVane(180, windDirection)
            "NE" -> rotateVane(225, windDirection)
            "E" -> rotateVane(270, windDirection)
            "SE" -> rotateVane(315, windDirection)
            else -> {
                rotateVane(0, windDirection)
            }
        }
    }

    private fun rotateVane(degrees: Int, windDirection: String) {
        val rotate = RotateAnimation(
            0F,
            720 + degrees.toFloat(),
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )

        rotate.duration = 4000
        rotate.interpolator = OvershootInterpolator()
        rotate.fillAfter = true
        rotate.repeatCount = 0

        val vane = findViewById<ImageView>(R.id.imageView)
        vane.startAnimation(rotate)
        val vaneText = findViewById<TextView>(R.id.centreText)
        vaneText.text = windDirection
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}