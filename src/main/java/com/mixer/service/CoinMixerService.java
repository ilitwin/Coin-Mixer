package com.mixer.service;

import com.mixer.config.CoinClient;
import com.mixer.entity.Address;
import com.mixer.entity.AddressMapping;
import com.mixer.entity.Transaction;
import com.mixer.repository.AddressMappingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class CoinMixerService {

    private static final String houseAddress = "House";

    @Value(value = "${mixer-fee}")
    private Double mixerFee;

    @Autowired
    KafkaProducerService kafkaProducerService;

    @Autowired
    AddressMappingRepository addressMappingRepository;

    @Autowired
    CoinClient coinClient;


    public String startMixing(List<String> addresses) {

        String newAddress = UUID.randomUUID().toString();

        for (String address : addresses) {
            AddressMapping mapping = new AddressMapping(newAddress, address, null);
            addressMappingRepository.save(mapping);
        }

        return "Please transfer your Coins to address: " + newAddress
                + ". The coins will be mixed, a " + mixerFee * 100 + "% fee will be deducted, and the remaining amount will be sent to your destination addresses: " + addresses + ".";
    }

    public void watch(){
        List<Transaction> allTransactionsPerWatch = new ArrayList<>();
        List<String> addressesToCheck = addressMappingRepository.findDistinctMixerAddress();

        for (String address : addressesToCheck) {

            Address data = coinClient.getAddressData(address);
            Iterable<AddressMapping> mapping = addressMappingRepository.findByMixerAddress(address);

            // Get timestamps of last processes transactions for mixer's address
            List<Date> time = new ArrayList<>();
            for (AddressMapping u : mapping) {
                time.add(u.getLastTransactionTime());
            }

            // Consolidate transactions that have not been mixed yet
            List<Transaction> newTransactions;
            if (time.get(0) != null) {
                newTransactions = data.getTransactions().stream().filter(o -> o.getTimestamp().after(time.get(0)) && address.equals(o.getToAddress())).collect(Collectors.toList());
            } else {
                newTransactions = data.getTransactions().stream().filter(o -> address.equals(o.getToAddress())).collect(Collectors.toList());
            }

            if (!newTransactions.isEmpty()) {

                addressMappingRepository.updateLastTransactionTime(newTransactions.get(0).getTimestamp(), address);

                Double amount = newTransactions.stream().map(t -> Double.parseDouble(t.getAmount())).mapToDouble(Double::doubleValue).sum();

                Transaction newTransaction = new Transaction(houseAddress, address, amount.toString());
                kafkaProducerService.sendToSchedulerTopic(newTransaction);

                // Charge the mixing fee
                amount = amount * (1 - mixerFee);

                Iterable<AddressMapping> recordsToClose = addressMappingRepository.findByMixerAddress(address);
                List<String> addressesToSend = new ArrayList<>();
                recordsToClose.forEach(h -> addressesToSend.add(h.getUserAddress()));

                // Randomize the amounts
                List<Transaction> randomizedTransactions = generateRandomTransactions(houseAddress, amount, addressesToSend);
                allTransactionsPerWatch.addAll(randomizedTransactions);
            }

        }

        // Send in small increments over random period of time
        Collections.shuffle(allTransactionsPerWatch);
        for (Transaction transaction : allTransactionsPerWatch) {
            kafkaProducerService.sendToSchedulerTopic(transaction);
        }

    }

    public List<Transaction> generateRandomTransactions(String fromAddress, Double total, List<String> toAddresses)
    {
        // Generate random number of outgoing transactions
        Random rand = new Random();
        int minimum = toAddresses.size();
        int maximum = total.intValue();
        if (minimum > maximum) {
            maximum = minimum;
        }
        int numberOfElements = minimum + rand.nextInt((maximum - minimum) + 1);

        // Generate random split of amount
        Double amounts[] = splitCoinAmount(total, numberOfElements);

        // Map addresses to random amounts and ensure all user accounts are represented in transactions
        List<String> result = new ArrayList<>();
        List<List<String>> accountReplicas = Collections.nCopies(numberOfElements/toAddresses.size() + 1, toAddresses);
        accountReplicas.forEach(result::addAll);
        List<String> finalAccounts = result.subList(0, numberOfElements);

        List<Transaction> transactions = new ArrayList<>();

        for (int i = 0; i < numberOfElements; i++) {
            Transaction transaction = new Transaction(finalAccounts.get(i), fromAddress, String.valueOf(amounts[i]));
            transactions.add(transaction);
        }

        return transactions;

    }

    public Double[] splitCoinAmount(Double total, Integer numberOfElements) {

        Double[] result = new Double[numberOfElements];
        result[0] = 0.0;
        double maxInc = total / numberOfElements;

        for (int i = 1; i < numberOfElements; i++) {
            double randomOne = Math.random() * maxInc;
            result[i] = randomOne;
        }

        double lastElement = total - Arrays.stream(result).mapToDouble(Double::doubleValue).sum();
        result[0] = lastElement;
        return result;

    }

}
