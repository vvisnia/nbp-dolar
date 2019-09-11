package com.vvisnia.nbp.rest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.ResponseEntity.ok;

@RestController
public class DollarController {

    private JSONArray getRates(int fromYear, int fromMonth, int fromDay, int toYear, int toMonth, int toDay) {

        String formattedFromMonth = String.format("%02d", fromMonth);
        String formattedFromDay = String.format("%02d", fromDay);
        String formattedToMonth = String.format("%02d", toMonth);
        String formattedToDay = String.format("%02d", toDay);

        String sURL;

        sURL = "http://api.nbp.pl/api/exchangerates/rates/c/usd/" + fromYear + "-" + formattedFromMonth + "-" + formattedFromDay + "/" + toYear + "-" + formattedToMonth + "-" + formattedToDay + "/?format=json";


        try {
            URL url = new URL(sURL);
            URLConnection request = url.openConnection();
            request.connect();

            Object object = new JSONParser().parse(new InputStreamReader((InputStream) request.getContent()));

            JSONObject jsonObject = (JSONObject) object;

            return (JSONArray) jsonObject.get("rates");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    @GetMapping("/usd/{year}/{month}/{day}")
    public ResponseEntity parseUSDJson(@PathVariable int year, @PathVariable int month, @PathVariable int day) throws ParseException {

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);

        Map<Object, Object> model = new HashMap<>();

        JSONArray rates = new JSONArray();
        JSONArray temp;

        if (year != currentYear) {

            for (int j = year; j < currentYear; j++) {

                if (currentYear - j == 1) {

                    temp = getRates(j, month, day, j + 1, 1, 1);
                    for (int i = 0; i < temp.size(); i++) {
                        JSONObject jsonObject = (JSONObject) temp.get(i);
                        rates.add(jsonObject);
                    }

                    temp = getRates(j + 1, 1, 2, j + 1, currentMonth, currentDay);
                    for (int i = 0; i < temp.size(); i++) {
                        JSONObject jsonObject = (JSONObject) temp.get(i);
                        rates.add(jsonObject);
                    }

                } else {
                    temp = getRates(j, month, day, j + 1, month, day);
                    for (int i = 0; i < temp.size(); i++) {
                        JSONObject jsonObject = (JSONObject) temp.get(i);
                        rates.add(jsonObject);
                    }
                }
            }

        } else {
            temp = getRates(currentYear, month, day, currentYear, currentMonth, currentDay);
            for (int i = 0; i < temp.size(); i++) {
                JSONObject jsonObject = (JSONObject) temp.get(i);
                rates.add(jsonObject);
            }
        }

        JSONObject rate;
        JSONObject ratePrevious;

        for (int i = 1; i < rates.size(); i++) {

            rate = (JSONObject) rates.get(i);
            ratePrevious = (JSONObject) rates.get(i - 1);

            BigDecimal ask = BigDecimal.valueOf((Double) rate.get("ask"));
            BigDecimal askPrevious = BigDecimal.valueOf((Double) ratePrevious.get("ask"));
            BigDecimal bid = BigDecimal.valueOf((Double) rate.get("bid"));
            BigDecimal bidPrevious = BigDecimal.valueOf((Double) ratePrevious.get("bid"));

            rate.put("askDifference", ask.subtract(askPrevious));
            rate.put("bidDifference", bid.subtract(bidPrevious));
        }

        model.put("usd", rates);
        return ok(model);
    }
}

