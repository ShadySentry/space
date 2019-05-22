package com.space.repository;

import com.space.model.Ship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ShipRepository extends JpaRepository <Ship, Long>, JpaSpecificationExecutor<Ship> {
//    @Override
//    @Transactional (timeout =10)
//    List<Ship> findAll();
//
//    List<Ship> getShips(Map<String,String> filters);

//    @Transactional
//     List<Ship> findAll(Pageable pageable);


//---------------------------------



}
