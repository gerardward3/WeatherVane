package com.example.weathervane

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.app.PendingIntent
import android.widget.RemoteViews
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject


/**
 * Implementation of App Widget functionality.
 */
class WindWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {

        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
                .let { intent ->
                    PendingIntent.getActivity(context, 0, intent, 0)
                }

            val views = RemoteViews(context.packageName, R.layout.wind_widget).apply {
                setOnClickPendingIntent(R.id.WindButton, pendingIntent)
            }

            updateAppWidget(context, appWidgetManager, appWidgetId, views)
        }

    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int,
    views: RemoteViews
) {
    // Construct the RemoteViews object

    var windSpeedText = ""
    val queue = Volley.newRequestQueue(context)
    val url = "https://weather-broker-cdn.api.bbci.co.uk/en/mobile/2648579"

    val stringRequest = JsonObjectRequest(
        Request.Method.GET, url, null,
        { response ->
            // Display the first 500 characters of the response string.
            val forecasts = response.getJSONArray("forecasts")[0] as JSONObject
            val summary = forecasts.getJSONObject("summary")
            val report = summary.getJSONObject("report")
            val windSpeedMph = report.getInt("windSpeedMph")
            val windDirection = report.getString("windDirection")
            windSpeedText = windSpeedMph.toString()
            // animateVane(windDirection)


            views.setTextViewText(R.id.WindButton, windSpeedText)
            appWidgetManager.updateAppWidget(appWidgetId, views)


        },
        {
            windSpeedText = "wrong"
        })

    queue.add(stringRequest)

    // Instruct the widget manager to update the widget

}

private fun animateVane(windDirection: String, views: RemoteViews) {
    when (windDirection) {
        "SW" -> rotateVane(45, views)
        "W" -> rotateVane(90, views)
        "NW" -> rotateVane(135, views)
        "N" -> rotateVane(180, views)
        "NE" -> rotateVane(225, views)
        "E" -> rotateVane(270, views)
        "SE" -> rotateVane(315, views)
        else -> {
            rotateVane(0, views)
        }
    }
}

private fun rotateVane(degrees: Int, views: RemoteViews) {
    // views.setImageViewResource(R.id.WindButton, R.id.WindButton)
}