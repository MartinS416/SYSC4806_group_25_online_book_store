package com.bookstore.common.service;

import com.bookstore.common.model.Address;
import com.bookstore.common.model.Customer;
import com.bookstore.common.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Address save(Address address) { return addressRepository.save(address); }

    public Address findById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Address not found: " + id));
    }

    public List<Address> findAll() { return addressRepository.findAll(); }

    public Address update(Long id, Address updated) {
        Address existing = addressRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Address not found: " + id));

        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setStreet(updated.getStreet());
        existing.setUnit(updated.getUnit());
        existing.setCity(updated.getCity());
        existing.setRegion(updated.getRegion());
        existing.setPostcode(updated.getPostcode());
        existing.setCountry(updated.getCountry());

        // Ensure the customer is not null
        if (updated.getCustomer() != null) {
            existing.setCustomer(updated.getCustomer());
        } else if (existing.getCustomer() == null) {
            // default empty Customer instead of null
            // todo --> review for fix/ improvement?
            existing.setCustomer(new Customer());
        }

        return addressRepository.save(existing);
    }

    public void delete(Long id) {
        addressRepository.deleteById(id);
    }
}