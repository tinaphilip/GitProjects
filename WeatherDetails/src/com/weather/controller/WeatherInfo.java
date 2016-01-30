package com.weather.controller;
 
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.bitpipeline.lib.owm.OwmClient;
import org.bitpipeline.lib.owm.WeatherData;
import org.bitpipeline.lib.owm.WeatherData.WeatherCondition;
import org.bitpipeline.lib.owm.WeatherStatusResponse;
import org.json.JSONException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
 

@Controller
@RequestMapping("/welcome")
public class WeatherInfo {
 
	@RequestMapping(method = RequestMethod.POST)
	public String printWelcome(@RequestParam("cityName") String cityName, ModelMap model) { 	
		OwmClient owm = new OwmClient ();
		WeatherStatusResponse currentWeather = null;
		 WeatherData weather = null;
		 WeatherCondition weatherCondition = null;
		try {
			currentWeather = owm.currentWeatherAtCity (cityName, "AU");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {			
			e.printStackTrace();
		}
		if (currentWeather.hasWeatherStatus ()) {
		    weather = currentWeather.getWeatherStatus ().get (0);		   		    
		    weatherCondition = weather.getWeatherConditions ().get (0);	       
		} else {
			return null;
		}
		String time = getDayTime();
		Double temperature = getTemperature(weather.getTemp());
		Double windSpeed = getWindSpeed(weather.getWindSpeed());
		
		model.addAttribute("cityName",cityName);
		model.addAttribute("time", time);
		model.addAttribute("temp", temperature.toString()+ " " + "degree celcius");
		model.addAttribute("weather", weatherCondition.getDescription ());
		model.addAttribute("windSpeed", windSpeed + " " +"km/hr");
		return "welcome";
}
	
	public String getDayTime() {
		String weekDay = "";
		 Calendar cal = Calendar.getInstance();
	        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
	        int day = cal.get(Calendar.DAY_OF_WEEK); 
	        switch (day) {
	        case 1:
	        	weekDay = "Sunday";
	        	break;
	        case 2 : 
	        	weekDay = "Monday";
	        	break;
	        case 3 :
	        	weekDay = "Tuesday";
	        	break;
	        case 4 :
	        	weekDay = "Wednesday";
	        	break;
	        case 5 :
	        	weekDay = "Thursday";
	        	break;
	        case 6 :
	        	weekDay = "Friday";
	        	break;
	        case 7 :
	        	weekDay = "Saturday";
	        	break;
	        }	      
	        
	        return weekDay + " " + sdf.format(cal.getTime());
	}
	
	public Double getTemperature(Float temp) {
	
		if (temp == 0.0 || temp == null) {
			return (double) 0.0;
		}
						
		BigDecimal tempKelvin = new BigDecimal(temp);
		BigDecimal tempCelcius = tempKelvin.subtract((BigDecimal.valueOf(273)));
		Double value = tempCelcius.doubleValue();		
		
		return (double) Math.round(value*100.00)/100.00;
		
	}
	
	public Double getWindSpeed (Float windSpeed) {
		
		BigDecimal speedMeter = new BigDecimal(windSpeed);
		BigDecimal speedKm= speedMeter.multiply(BigDecimal.valueOf(3.6));
		Double value = speedKm.doubleValue();
		return (double) Math.round(value*100.00)/100.00;
		
	}
}