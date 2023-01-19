package com.mixer.repository;

import com.mixer.entity.AddressMapping;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Date;
import java.util.List;


@Repository
public interface AddressMappingRepository extends CrudRepository<AddressMapping, Long> {

    Iterable<AddressMapping> findByMixerAddress(String mixerAddress);

    @Query("SELECT DISTINCT a.mixerAddress FROM AddressMapping a")
    List<String> findDistinctMixerAddress();

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query("UPDATE AddressMapping u SET u.lastTransactionTime = :date WHERE u.mixerAddress = :address")
    void updateLastTransactionTime(@Param("date") Date date, @Param("address") String address);
}
