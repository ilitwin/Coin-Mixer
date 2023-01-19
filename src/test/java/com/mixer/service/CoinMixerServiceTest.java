package com.mixer.service;

import com.mixer.config.CoinClient;
import com.mixer.entity.Address;
import com.mixer.entity.AddressMapping;
import com.mixer.entity.Transaction;
import com.mixer.repository.AddressMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import java.util.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.stream.Collectors;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CoinMixerServiceTest {

    private String ADDRESS = "Address";
    private Integer UUID_LENGTH = 36;
    private Double MIXER_FEE = 0.05;
    private Double TRANSACTION_AMOUNT = 1.0;
    private Integer NUMBER_OF_TRANSACTIONS = 3;

    @Mock
    AddressMappingRepository addressMappingRepository;

    @InjectMocks
    private CoinMixerService coinMixerService;

    @Mock
    private CoinClient coinClient;

    @Mock
    private KafkaProducerService kafkaProducerService;


    @BeforeEach
    public void setup() {
        ReflectionTestUtils.setField(coinMixerService, "mixerFee", MIXER_FEE);
    }

    @Test
    public void testStartMixing() {

        List<String> addresses = new ArrayList<>();
        addresses.add(ADDRESS);
        AddressMapping mapping = new AddressMapping(UUID.randomUUID().toString(), addresses.get(0), null);

        when(addressMappingRepository.save(any())).thenReturn(mapping);

        String output = coinMixerService.startMixing(addresses);

        assertNotNull(output);
        assertTrue(output.contains(addresses.get(0)));
        assertEquals(UUID_LENGTH, output.split("[:.]")[1].trim().length());

    }

    @Test
    public void testWatch() {

        List<String> addresses = new ArrayList<>();
        addresses.add(ADDRESS);
        AddressMapping mapping = new AddressMapping(UUID.randomUUID().toString(), addresses.get(0), null);

        Transaction transactions = new Transaction(ADDRESS, ADDRESS, TRANSACTION_AMOUNT.toString());
        Address address = new Address(TRANSACTION_AMOUNT.toString(), Collections.singletonList(transactions));

        when(addressMappingRepository.findDistinctMixerAddress()).thenReturn(Collections.singletonList(ADDRESS));
        when(addressMappingRepository.findByMixerAddress(any())).thenReturn(Collections.singletonList(mapping));
        when(coinClient.getAddressData(any())).thenReturn(address);

        coinMixerService.watch();
        verify(kafkaProducerService, times(2)).sendToSchedulerTopic(any());

    }

    @Test
    public void testGenerateRandomTransactions() {

        List<Transaction> transactionSplit = coinMixerService.generateRandomTransactions(ADDRESS, TRANSACTION_AMOUNT, Collections.singletonList(ADDRESS));
        List<String> amounts = transactionSplit.stream().map(Transaction::getAmount).collect(Collectors.toList());
        Double total = amounts.stream().mapToDouble(Double::valueOf).sum();

        assertEquals(TRANSACTION_AMOUNT, total);

    }

    @Test
    public void testSplitCoinAmount() {

        Double[] amountSplit = coinMixerService.splitCoinAmount(TRANSACTION_AMOUNT, NUMBER_OF_TRANSACTIONS);
        double total = Arrays.stream(amountSplit).mapToDouble(Double::doubleValue).sum();

        assertEquals(amountSplit.length, NUMBER_OF_TRANSACTIONS);
        assertEquals(TRANSACTION_AMOUNT, total);

    }

}