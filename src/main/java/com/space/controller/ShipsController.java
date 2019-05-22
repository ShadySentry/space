package com.space.controller;

import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.service.ShipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ShipsController {
    @Autowired
    ShipService service;

    @RequestMapping("/rest/ships")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    List<Ship> findAllByCriteria(@RequestParam(value = "name", required = false) String name,
                                 @RequestParam(value = "planet", required = false) String planet,
                                 @RequestParam(value = "shipType", required = false) ShipType shipType,
                                 @RequestParam(value = "after", required = false) Long after,
                                 @RequestParam(value = "before", required = false) Long before,
                                 @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                                 @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                                 @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                                 @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                                 @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                                 @RequestParam(value = "minRating", required = false) Double minRating,
                                 @RequestParam(value = "maxRating", required = false) Double maxRating,
                                 @RequestParam(value = "order", required = false, defaultValue = "ID") ShipOrder order,
                                 @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
                                 @RequestParam(value = "pageSize", required = false, defaultValue = "3") Integer pageSize) {
//        if (order == null)
//            order = ShipOrder.ID;

        return service.findByCriteria(name, planet, shipType, isUsed, after, before, minCrewSize, maxCrewSize, minSpeed, maxSpeed, minRating, maxRating, order, pageNumber, pageSize)
                .getContent();
    }

    @RequestMapping(value = "rest/ships/count", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody
    long getCount(@RequestParam(value = "name", required = false) String name,
                  @RequestParam(value = "planet", required = false) String planet,
                  @RequestParam(value = "shipType", required = false) ShipType shipType,
                  @RequestParam(value = "after", required = false) Long after,
                  @RequestParam(value = "before", required = false) Long before,
                  @RequestParam(value = "isUsed", required = false) Boolean isUsed,
                  @RequestParam(value = "minSpeed", required = false) Double minSpeed,
                  @RequestParam(value = "maxSpeed", required = false) Double maxSpeed,
                  @RequestParam(value = "minCrewSize", required = false) Integer minCrewSize,
                  @RequestParam(value = "maxCrewSize", required = false) Integer maxCrewSize,
                  @RequestParam(value = "minRating", required = false) Double minRating,
                  @RequestParam(value = "maxRating", required = false) Double maxRating) {

        return service.findByCriteria(name, planet, shipType, isUsed, after, before, minCrewSize, maxCrewSize, minSpeed, maxSpeed, minRating, maxRating)
                .getTotalElements();
    }


    @PostMapping(value = "/rest/ships")
    @ResponseBody
    public ResponseEntity<Ship> create(@RequestBody Ship ship) {
        if (!service.dataIsValid(ship, true)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Ship createdShip = service.create(ship);
        return new ResponseEntity<>(createdShip, HttpStatus.OK);
    }

    @RequestMapping(value = "/rest/ships/{shipId}", method = RequestMethod.GET)
    public @ResponseBody
    ResponseEntity<Ship> getOne(@PathVariable String shipId) {
        if (!service.idIsValid(shipId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        long id = Long.valueOf(shipId);

        Optional<Ship> ship = service.getOne(id);

        if (ship.isPresent()) {
            return new ResponseEntity<>(ship.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/rest/ships/{shipId}", method = RequestMethod.POST)
    public @ResponseBody
    ResponseEntity<Ship> updateOne(@PathVariable String shipId, @RequestBody(required = false) Ship ship) {
        if (!service.idIsValid(shipId) || !service.dataIsValid(ship,false)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (!service.idExists(Long.valueOf(shipId))) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(service.update(Long.valueOf(shipId), ship), HttpStatus.OK);
    }

    @RequestMapping(value = "/rest/ships/{shipId}", method = RequestMethod.DELETE)
    public @ResponseBody
    ResponseEntity<HttpStatus> deleteOne(@PathVariable String shipId) {
        Long id;
        try {
            id = Long.valueOf(shipId);
        } catch (NumberFormatException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if ( !service.idIsValid(shipId)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (!service.idExists(id)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        service.deleteOne(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
