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

    // CREATE / SAVE //
    public Address save(Address address) {
        return addressRepository.save(address);
    }

    // READ (By ID) //
    public Address findById(Long id) {
        return addressRepository.findById(id).orElse(null);
    }

    // READ (All) //
    public List<Address> findAll() {
        return addressRepository.findAll();
    }

    // UPDATE //
    public Address update(Long id, Address updatedAddress) {
        Address existing = findById(id);
        if (existing == null) {
            return null;
        }

        existing.setFirstName(updatedAddress.getFirstName());
        existing.setLastName(updatedAddress.getLastName());
        existing.setStreet(updatedAddress.getStreet());
        existing.setUnit(updatedAddress.getUnit());
        existing.setCity(updatedAddress.getCity());
        existing.setRegion(updatedAddress.getRegion());
        existing.setPostcode(updatedAddress.getPostcode());
        existing.setCountry(updatedAddress.getCountry());
        existing.setCustomer(updatedAddress.getCustomer());

        return addressRepository.save(existing);
    }

    // DELETE //
    public void delete(Long id) {
        addressRepository.deleteById(id);
    }
}
