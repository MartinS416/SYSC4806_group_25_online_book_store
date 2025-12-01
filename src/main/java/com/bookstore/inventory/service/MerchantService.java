package com.bookstore.inventory.service;

import com.bookstore.inventory.model.Merchant;
import com.bookstore.inventory.repository.MerchantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantService {

    private final MerchantRepository merchantRepository;

    public MerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    public Merchant create(Merchant m) { return merchantRepository.save(m); }

    public Merchant findById(Long id) {
        return merchantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Merchant not found: " + id));
    }

    public List<Merchant> findAll() { return merchantRepository.findAll(); }

    public Merchant update(Long id, Merchant updated) {
        Merchant m = findById(id);
        m.setName(updated.getName());
        m.setEmail(updated.getEmail());
        return merchantRepository.save(m);
    }

    public void delete(Long id) { merchantRepository.deleteById(id); }
}
