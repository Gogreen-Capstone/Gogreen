package com.capstone.gogreen.controllers;

import com.capstone.gogreen.models.Location;
import com.capstone.gogreen.repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

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
    public String mapBox(Model model) {
        List<Location> listAddresses = locationsDao.findAll(); //finding all address to loop through them
        model.addAttribute("mapBoxKey", mapBoxKey);

        for (Location listAddress : listAddresses) {
            String address = listAddress.addressToString();
            model.addAttribute("address", address); //adding address to our page
            System.out.println(address);
        }

        return "reviews/index";
    }



}
