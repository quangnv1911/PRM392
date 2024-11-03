package com.example.demo.service;

import com.example.demo.model.ProductType;
import com.example.demo.repo.ProductTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductTypeService {
    @Autowired
    private ProductTypeRepository productTypeRepository;

    public List<ProductType> getAllProductTypes() {
        return productTypeRepository.findAll();
    }
}
