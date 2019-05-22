package com.space.service;

import com.space.controller.ShipOrder;
import com.space.model.Ship;
import com.space.model.ShipType;
import com.space.repository.ShipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.*;
import java.util.stream.Stream;

@Service
public class ShipService {
    @Autowired
    ShipRepository repository;

    public boolean dataIsValid(Ship ship, boolean notNullFields) {
        if (ship == null) {
            return false;
        }

        if (notNullFields) {
            if (Stream.of(ship.getName(), ship.getPlanet(), ship.getShipType(), ship.getProdDate(), ship.getSpeed(), ship.getCrewSize())
                    .anyMatch(Objects::isNull)) {
                return false;
            }
        }


        //--
        if (ship.getName() != null && !stringIsValid(ship.getName())) {
            return false;
        }
        if (ship.getPlanet() != null && !stringIsValid(ship.getPlanet())) {
            return false;
        }


        if (ship.getProdDate() != null) {
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(ship.getProdDate());
            if (!productionDateIsValid(ship.getProdDate()))
                return false;
        }

        if (ship.getSpeed() != null && !speedIsValid(ship.getSpeed())) {
            return false;
        }
        if (ship.getCrewSize() != null && !crewSizeIsValid(ship.getCrewSize())) {
            return false;
        }


        //---
//        if ((notNullFields && !textHasContent(ship.getName()))
//                || (notNullFields && !textHasContent(ship.getPlanet()))
//                || (notNullFields && ship.getName().length() > 50)
//                || (notNullFields && ship.getPlanet().length() > 50)
//                || (ship.getProdDate()!=null && (calendar.get(Calendar.YEAR) < 2800 || calendar.get(Calendar.YEAR) > 3019))
//                || (ship.getSpeed()!=null && (ship.getSpeed() < 0.01 || ship.getSpeed() > 0.99))
//                || (ship.getCrewSize()!=null && (ship.getCrewSize() < 1 || ship.getCrewSize() > 9999))) {
//            return false;


        return true;
    }

    public boolean stringIsValid(String text) {
        String EMPTY_STRING = "";
        return (text != null) && (!text.trim().equals(EMPTY_STRING)) && (text.length() <= 50);
    }

    public boolean productionDateIsValid(Date productDate) {
        int minYear = 2800;
        int maxYear = 3019;
        Calendar calendar = GregorianCalendar.getInstance();

        if (productDate == null) {
            return false;
        }

        calendar.setTime(productDate);
        return calendar.get(Calendar.YEAR) >= minYear && calendar.get(Calendar.YEAR) <= maxYear;


    }

    public boolean speedIsValid(Double speed) {
        Double minSpeed = 0.01;
        Double maxSpeed = 0.99;
        if (speed == null)
            return false;
        return speed >= minSpeed && speed <= maxSpeed;
    }

    public boolean crewSizeIsValid(Integer crewSize) {
        int minCrewSize = 1;
        int maxCrewSize = 9999;

        if (crewSize == null)
            return false;

        return crewSize >= minCrewSize && crewSize <= maxCrewSize;
    }


    public Page<Ship> findByCriteria(String name, String planet, ShipType shipType, Boolean isUsed,
                                     Long after, Long before,
                                     Integer minCrewSize, Integer maxCrewSize,
                                     Double minSpeed, Double maxSpeed,
                                     Double minRating, Double maxRating,
                                     ShipOrder order, Integer pageNumber, Integer pageSize) {

        Pageable pageable;
        if (pageNumber != null && pageSize != null && order != null) {
            pageable = PageRequest.of(pageNumber, pageSize, Sort.by(order.getFieldName()));
        } else {
            pageable = Pageable.unpaged();
        }

        return repository.findAll(new Specification<Ship>() {
            @Override
            public Predicate toPredicate(Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                //like
                if (name != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("name"), "%" + name + "%")));
                }
                if (planet != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.like(root.get("planet"), "%" + planet + "%")));
                }
                if (shipType != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("shipType"), shipType)));
                }
                if (isUsed != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.equal(root.get("isUsed"), isUsed)));
                }
                //between
                if (after != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("prodDate"), convertDateToYearBeggining(after))));
                }
                if (before != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("prodDate"), convertDateToYearBeggining(before))));
                }
                if (minCrewSize != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("crewSize"), minCrewSize)));
                }
                if (maxCrewSize != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("crewSize"), maxCrewSize)));
                }
                if (minSpeed != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("speed"), minSpeed)));
                }
                if (maxSpeed != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("speed"), maxSpeed)));
                }
                if (minRating != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("rating"), minRating)));
                }
                if (maxRating != null) {
                    predicates.add(criteriaBuilder.and(criteriaBuilder.lessThanOrEqualTo(root.get("rating"), maxRating)));
                }

                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);
    }

    public Page<Ship> findByCriteria(String name, String planet, ShipType shipType, Boolean isUsed,
                                     Long after, Long before,
                                     Integer minCrewSize, Integer maxCrewSize,
                                     Double minSpeed, Double maxSpeed,
                                     Double minRating, Double maxRating) {
        return findByCriteria(name, planet, shipType, isUsed, after, before, minCrewSize, maxCrewSize, minSpeed, maxSpeed, minRating, maxRating,
                ShipOrder.ID, null, null);

    }


    public Ship create(Ship ship) {
        shipParamsRecalculations(ship);
        return repository.save(ship);
    }

    public Optional<Ship> getOne(Long id) {
        return repository.findById(id);
    }

    public Ship update(Long shipId, Ship ship) {
        try {
            Ship savedShip = repository.findById(shipId).get();
            boolean dataChanged = false;

            if (ship.getName() != null) {
                savedShip.setName(ship.getName());
                dataChanged=true;
            }
            if (ship.getPlanet() != null) {
                savedShip.setPlanet(ship.getPlanet());
                dataChanged=true;
            }
            if (ship.getShipType() != null) {
                savedShip.setShipType(ship.getShipType());
                dataChanged=true;
            }
            if (ship.getProdDate() != null) {
                savedShip.setProdDate(ship.getProdDate());
                dataChanged=true;
            }
            if (ship.getUsed() != null) {
                if(!Stream.of(ship.getName(), ship.getPlanet(), ship.getShipType(), ship.getProdDate(), ship.getSpeed(), ship.getCrewSize())
                        .anyMatch(Objects::isNull)){
                savedShip.setUsed(ship.getUsed());
                dataChanged=true;
                }
            }
            if (ship.getSpeed() != null) {
                savedShip.setSpeed(ship.getSpeed());
                dataChanged=true;
            }
            if (ship.getCrewSize() != null) {
                savedShip.setCrewSize(ship.getCrewSize());
                dataChanged=true;
            }

            if(dataChanged){
                savedShip = shipParamsRecalculations(savedShip);
                repository.save(savedShip);
            }
            return savedShip;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void deleteOne(Long shipId) {
        repository.deleteById(shipId);
    }

    private Ship shipParamsRecalculations(Ship ship) {
        final int places = 2;
        final double SCALE = Math.pow(10, places);

        ship.setSpeed(Math.round(ship.getSpeed() * SCALE) / SCALE);

        double recencyCoefficient = ship.getUsed() ? 0.5 : 1;
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(ship.getProdDate());

        Double rating = (80 * ship.getSpeed() * recencyCoefficient / (3019 - calendar.get(Calendar.YEAR) + 1));
        ship.setRating(Math.round(rating * SCALE) / SCALE);
        return ship;
    }

    private boolean textHasContent(String text) {
        String EMPTY_STRING = "";
        return (text != null) && (!text.trim().equals(EMPTY_STRING));
    }

    public boolean idIsValid(String shipId) {
        Long id = null;
        if (shipId.indexOf('.') != -1 || shipId.indexOf(',') != -1) {
            return false;
        }
        try {
            id = Long.valueOf(shipId);
        } catch (NumberFormatException e) {
            return false;
        }
        if (id <= 0) {
            return false;
        }
        return true;
    }

    private Date convertDateToYearBeggining(Long date) {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTimeInMillis(date);
        calendar.set(calendar.get(Calendar.YEAR), Calendar.JANUARY, 1, 0, 0, 0);
        return calendar.getTime();
    }


    public boolean idExists(Long id) {
        return repository.existsById(id);
    }
}
