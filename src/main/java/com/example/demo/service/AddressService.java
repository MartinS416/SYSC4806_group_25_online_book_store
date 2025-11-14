package com.example.demo.service;

import com.example.demo.model.Address;
import com.example.demo.repository.AddressRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
        Address a = findById(id);

        a.setFirstName(updated.getFirstName());
        a.setLastName(updated.getLastName());
        a.setStreet(updated.getStreet());
        a.setUnit(updated.getUnit());
        a.setCity(updated.getCity());
        a.setRegion(updated.getRegion());
        a.setPostcode(updated.getPostcode());
        a.setCountry(updated.getCountry());

        return addressRepository.save(a);
    }

    public void delete(Long id) {
        addressRepository.deleteById(id);
    }
}