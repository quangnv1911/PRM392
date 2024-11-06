package com.example.demo.controller;

import com.example.demo.Dtos.Request.ProductCreateDto;
import com.example.demo.Dtos.Response.ListProductDto;
import com.example.demo.Dtos.Response.UpdateCreateDto;
import com.example.demo.model.Product;
import com.example.demo.model.ProductDetail;
import com.example.demo.repo.CategoriesRepository;
import com.example.demo.repo.ProductDetailRepository;
import com.example.demo.repo.ProductRepository;
import jakarta.transaction.Transactional;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoriesRepository categoriesRepository;
    @Autowired
    private ProductDetailRepository productDetailRepository;

    @GetMapping("/products")
    public List<ListProductDto> getAllProducts(@RequestParam(required = false) String type) {

        List<Product> products;
        if (type == null) {
            products = productRepository.findAll();
        } else {
            products = productRepository.findByType(type);
        }

        // Convert each Product to ProductCreateDto
        return products.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    // Helper method to convert Product to ProductCreateDto
    private ListProductDto convertToDto(Product product) {
        ListProductDto dto = new ListProductDto();

        // Basic fields
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setImage(product.getImage());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setCategoryId(product.getCategory().getCategoryId());
        dto.setDescription(product.getDescription());
        dto.setType(product.getType());
        dto.setPurchaseCount(product.getPurchaseCount());
        dto.setRate(product.getRate());
        dto.setCreatedBy(product.getCreatedBy());

        // Fields for list-based attributes
        dto.setSizes(product.getSizes());
        dto.setColors(product.getColors());

        List<ProductDetail> detailList = productDetailRepository.findByProductId(Long.valueOf(product.getProductId()));
        // Image product details
        List<String> imageDetails = detailList.stream()
                .map(ProductDetail::getImageProduct)
                .collect(Collectors.toList());
        dto.setImageProductDetails(imageDetails);

        // Set imageLink if applicable
        dto.setImageLink(product.getImageLink());

        return dto;
    }

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
        product.setSizes(productCreateDto.getSizes());
        product.setColors(productCreateDto.getColors());


        Product productSave = productRepository.save(product);
        for (String imageLink : productCreateDto.getImageProductDetails()) {
            ProductDetail productDetail = new ProductDetail();
            productDetail.setProductId(Long.valueOf(productSave.getProductId()));
            productDetail.setImageProduct(imageLink);
            productDetailRepository.save(productDetail);
        }


        return new ResponseEntity<>(productCreateDto, HttpStatus.CREATED);
    }

    @PutMapping("product/{id}")
    @Transactional
    public ResponseEntity<ProductCreateDto> updateProduct(
            @PathVariable Long id,
            @RequestBody ProductCreateDto productCreateDto) {

        // Find existing product by ID
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // If not found, return 404
        }

        Product product = optionalProduct.get();

        // Update product fields
        product.setProductName(productCreateDto.getProductName());
        product.setPrice(productCreateDto.getPrice());
        product.setStockQuantity(productCreateDto.getStockQuantity());
        product.setImage(productCreateDto.getImage());
        product.setCategory(categoriesRepository.findByCategoryId(productCreateDto.getCategoryId()));
        product.setDescription(productCreateDto.getDescription());
        product.setSizes(productCreateDto.getSizes());
        product.setColors(productCreateDto.getColors());

        // Reset the purchase count if it's provided in DTO, or keep existing if not
        if (productCreateDto.getPurchaseCount() != null) {
            product.setPurchaseCount(productCreateDto.getPurchaseCount());
        }

        // Update the product and save it
        Product updatedProduct = productRepository.save(product);

        // Clear existing product details to avoid duplicates, if needed
        boolean exists = productDetailRepository.existsByProductId(id);

        // If details exist, proceed with deletion
        if (exists) {
            productDetailRepository.deleteProductDetailByProductId(id);
        }
        // Save new product details
        for (String imageLink : productCreateDto.getImageProductDetails()) {
            ProductDetail productDetail = new ProductDetail();
            productDetail.setProductId(id);
            productDetail.setImageProduct(imageLink);
            productDetailRepository.save(productDetail);
        }

        // Return updated product DTO as response
        return new ResponseEntity<>(productCreateDto, HttpStatus.OK);
    }

    @GetMapping("product/{id}")
    public ResponseEntity<UpdateCreateDto> findProductById(@PathVariable Long id) {

        // Find the product by ID
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Return 404 if not found
        }

        Product product = optionalProduct.get();

        // Convert Product entity to ProductCreateDto
        UpdateCreateDto productDto = new UpdateCreateDto();
        productDto.setProductId(product.getProductId());
        productDto.setProductName(product.getProductName());
        productDto.setPrice(product.getPrice());
        productDto.setStockQuantity(product.getStockQuantity());
        productDto.setImage(product.getImage());
        productDto.setCategoryId(product.getCategory().getCategoryId());
        productDto.setDescription(product.getDescription());
        productDto.setPurchaseCount(product.getPurchaseCount());
        productDto.setSizes(product.getSizes());
        productDto.setColors(product.getColors());

        // Retrieve associated product details (images)
        List<String> imageDetails = productDetailRepository.findByProductId((long) Math.toIntExact(id))
                .stream()
                .map(ProductDetail::getImageProduct)
                .collect(Collectors.toList());
        productDto.setImageProductDetails(imageDetails);

        // Return the DTO as the response
        return new ResponseEntity<>(productDto, HttpStatus.OK);
    }

    @DeleteMapping("product/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {

        Optional<Product> optionalProduct = productRepository.findById(id);
        if (!optionalProduct.isPresent()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND); // Return 404 if not found
        }

        productRepository.delete(optionalProduct.get());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
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