package com.bookstore.demo.service;

import com.bookstore.demo.model.Shop;
import com.bookstore.demo.repository.ShopRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopService {

    private final ShopRepository shopRepository;

    public ShopService(ShopRepository shopRepository) { this.shopRepository = shopRepository; }

    public Shop create(Shop shop) { return shopRepository.save(shop); }

    public Shop findById(Long id) {
        return shopRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Shop not found: " + id));
    }

    public List<Shop> findAll() { return shopRepository.findAll(); }

    public Shop update(Long id, Shop updated) {
        Shop s = findById(id);
        s.setName(updated.getName());
        s.setMerchant(updated.getMerchant());
        return shopRepository.save(s);
    }

    public void delete(Long id) { shopRepository.deleteById(id); }
}