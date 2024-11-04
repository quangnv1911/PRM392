package com.example.demo.controller;

import com.example.demo.Dtos.Request.ProductCreateDto;
import com.example.demo.model.Product;
import com.example.demo.model.ProductDetail;
import com.example.demo.repo.CategoriesRepository;
import com.example.demo.repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;
    @GetMapping("/product")
    public List<Product> getProductsByType(@RequestParam(required = false) String type) {

        if (type == null) {
            return productRepository.findAll();
        }
        return productRepository.findByType(type);
    }

    @GetMapping("/productById")
    public Optional<Product> getProductById(@RequestParam Long id) {
        return productRepository.findById(id);
    }
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProductsByName(@RequestParam String productName) {
        List<Product> products = productRepository.findByProductNameContaining(productName);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/top")
    public List<Product> getTopProducts() {
        return productRepository.findTop5ByOrderByPurchaseCountDesc();
    }

    @PostMapping("product")
    public ResponseEntity<ProductCreateDto> createNewProduct(@RequestBody ProductCreateDto productCreateDto) {
        Product product = new Product();
        product.setProductName(productCreateDto.getProductName());
        product.setPrice(productCreateDto.getPrice());
        product.setStockQuantity(productCreateDto.getStockQuantity());
        product.setImage(productCreateDto.getImage());
        product.setCategory(categoriesRepository.findByCategoryId(productCreateDto.getCategoryId()));
        product.setDescription(productCreateDto.getDescription());
        product.setPurchaseCount(0);
        // Lưu sản phẩm vào cơ sở dữ liệu
        productRepository.save(product);

        return new ResponseEntity<>(productCreateDto, HttpStatus.CREATED);
    }

    @PutMapping("")
    public void updateProductDetail(@RequestParam int productId, @RequestBody Product productUpdate) {

        productRepository.save(productUpdate);
    }

    @GetMapping("/searchProduct")
    public ResponseEntity<List<Product>> searchProduct(@RequestParam String search) {
        List<Product> products = productRepository.findBySearch(search);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/product/sizes-colors")
    public ResponseEntity<Map<String, List<String>>> getSizesAndColorsByProductId(@RequestParam Long productId) {
        Optional<Product> productOptional = productRepository.findById(productId);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            Map<String, List<String>> response = new HashMap<>();
            response.put("sizes", product.getSizes());
            response.put("colors", product.getColors());
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}