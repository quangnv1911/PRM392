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

import java.util.List;

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
}