package com.capstone.gogreen.controllers;

import com.capstone.gogreen.models.Location;
import com.capstone.gogreen.repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class LocationController {

    private final LocationRepository locationsDao;

    public LocationController(LocationRepository locationsDao) {
        this.locationsDao = locationsDao;
    }

    @Value("${mapbox.api.key}")
    private String mapBoxKey;

    @GetMapping("/mapbox")
    public String showMapBox(Model model) {
        model.addAttribute("mapBoxKey", mapBoxKey);
        return "reviews/index";
    }

    // Adding json object to JS
    @RequestMapping(value = "/mapbox.json", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public List<Location> userLocations() {
        return locationsDao.findAll();
    }
}
