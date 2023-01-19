package com.mixer.repository;

import com.mixer.entity.AddressMapping;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import java.util.*;


@ExtendWith(MockitoExtension.class)
@DataJpaTest
@EnableAutoConfiguration
public class AddressMappingRepositoryTest {

    private String ADDRESS = "Address";

    @Autowired
    AddressMappingRepository addressMappingRepository;

    @Test
    public void testRepository() {
        AddressMapping addressMapping = new AddressMapping(1L, ADDRESS, ADDRESS, null);
        Date date = new Date();

        // Test save
        addressMappingRepository.save(addressMapping);
        Assert.assertNotNull(addressMapping.getId());

        // Test update
        addressMappingRepository.updateLastTransactionTime(date, ADDRESS);
        Assert.assertEquals(date, addressMappingRepository.findById(addressMapping.getId()).get().getLastTransactionTime());

        // Test get distinct addresses
        Assert.assertEquals(Collections.singletonList(ADDRESS), addressMappingRepository.findDistinctMixerAddress());

    }

}