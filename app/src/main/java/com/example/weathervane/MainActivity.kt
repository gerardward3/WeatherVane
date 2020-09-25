package com.example.weathervane

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import com.example.weathervane.R.id.toolbar
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(toolbar))

        getForecast("2648579")
    }

    private fun getForecast(locationId: String): Unit {
        val windData = findViewById<TextView>(R.id.windData)
        val description = findViewById<TextView>(R.id.description)

        val queue = Volley.newRequestQueue(this)
        val url = "https://weather-broker-cdn.api.bbci.co.uk/en/mobile/$locationId"

        val stringRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            Response.Listener { response ->
                // Display the first 500 characters of the response string.
                val forecasts = response.getJSONArray("forecasts")[0] as JSONObject
                val summary = forecasts.getJSONObject("summary")
                val report = summary.getJSONObject("report")
                val windSpeedMph = report.getInt("windSpeedMph")
                val windDescription = report.getString("windDescription")

                val location = response.getJSONObject("location").getString("name")

                windData.text = "Wind speed is $windSpeedMph miles per hour in $location."
                description.text = "$windDescription."

            },
            Response.ErrorListener { windData.text = "That didn't work!" })

        queue.add(stringRequest)

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