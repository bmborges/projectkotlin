package dev.brunomborges.projectkotlin.fragments

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import dev.brunomborges.projectkotlin.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WeatherFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WeatherFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_weather, container, false)


        if(ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getLocation(view)
        }else{
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        }
        return view
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }

    private fun getLocation(view: View){
        val locationManager = requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if(ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, object:
            LocationListener {
            override fun onLocationChanged(location: Location) {
                lifecycleScope.launch{
                    val weatherData = getWeatherData(location.latitude, location.longitude)
                    val cityDistrict = getCityDistrict(location.latitude, location.longitude)

                    updateUi(weatherData, cityDistrict, view)
                }
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        })
    }

    private fun updateUi(weatherData: WeatherData, cityDistrict: DistrictDate, view: View){
        val temperaturaTextView = view.findViewById<TextView>(R.id.temperatura)
        val descricaoTextView = view.findViewById<TextView>(R.id.descricao)
        val bairroCidadeTextView = view.findViewById<TextView>(R.id.bairroCidade)

        temperaturaTextView.text = "${weatherData.temperature} °C"
        descricaoTextView.text = weatherData.description
        bairroCidadeTextView.text = "${cityDistrict.district}, ${cityDistrict.city}"

        try{
            val imageView = view.findViewById<ImageView>(R.id.imageView)
            val drawableId = resources.getIdentifier(getWeatherIcon(weatherData.conditionCode), "drawable", requireContext().packageName)
            imageView.setImageResource(drawableId)

            val hexColor = getWeatherColor(weatherData.conditionCode)
            val color = Color.parseColor(hexColor)
            imageView.setColorFilter(color)
        }catch(e: Exception){
            println(e.message)
        }
    }

    private suspend fun getCityDistrict(latitude: Double, longitude: Double): DistrictDate{
        val url = "https://api.bigdatacloud.net/data/reverse-geocode-client?latitude=$latitude&longitude=$longitude&localityLanguage=pt"
        val jsonText = withContext(Dispatchers.IO) { URL(url).readText() }
        val jsonObject = JSONObject(jsonText)
        val city = jsonObject.getString("city")
        val district = jsonObject.getString("locality")

        return DistrictDate(city, district)
    }

    private suspend fun getWeatherData(latitude: Double, longitude: Double): WeatherData{
        val apiKey = "91fbe21a8c3c4d3d8ef174846232703"
        val url = "https://api.weatherapi.com/v1/current.json?lang=pt&key=$apiKey&q=$latitude,$longitude"
        val jsonText = withContext(Dispatchers.IO) { URL(url).readText() }
        val jsonObject = JSONObject(jsonText)
        val current = jsonObject.getJSONObject("current")
        val temperature = current.getDouble("temp_c")
        val description = current.getJSONObject("condition").getString("text")
        val conditionCode = current.getJSONObject("condition").getInt("code")

        return WeatherData(temperature, description, conditionCode)
    }

    private fun getWeatherColor(conditionCode: Int): String{
        return when(conditionCode){
            in 200..232 -> "#637E90"
            in 300..321 -> "#29B3FF"
            in 500..531 -> "#14C2DD"
            in 600..622 -> "#E5F2F0"
            in 701..781 -> "#FFFEA8"
            800 -> "#FBC740"
            801 -> "#BCECE0"
            802 -> "#BCECE0"
            803, 804 -> "#36EEE0"
            1183 -> "#14C2DD"
            else -> "#FBC740"
        }
    }

    private fun getWeatherIcon(conditionCode: Int): String{
        return when(conditionCode){
            in 200..232 -> "wi_thundeerstorm"
            in 300..321 -> "wi_showers"
            in 500..531 -> "wi_rain"
            in 600..622 -> "wi_snow"
            in 701..781 -> "wi_fog"
            800 -> "wi_day_syunny"
            801 -> "wi_day_cloud"
            802 -> "wi_cloud"
            803, 804 -> "wi_day_cloud_high"
            1183 -> "wi_day_light_wind"
            else -> "wi_day_sunny"
        }
    }
}