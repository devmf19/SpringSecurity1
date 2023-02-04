package com.consiti.springsecurity1.controller;

import com.consiti.springsecurity1.dto.Message;
import com.consiti.springsecurity1.dto.ProductDto;
import com.consiti.springsecurity1.entity.Product;
import com.consiti.springsecurity1.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = "*")
public class ProductController {
    @Autowired
    ProductService productService;


    //products list
    @GetMapping("")
    public ResponseEntity<List<Product>> findAll() {
        List<Product> list = productService.list();
        return new ResponseEntity<List<Product>>(list, HttpStatus.OK);
    }

    //get product by id
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") int id) {
        if (!productService.existsById(id)) {
            return new ResponseEntity<>(new Message("El producto solicitado no existe"), HttpStatus.NOT_FOUND);
        }
        Product product = productService.getOne(id).get();
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    //get product by name
    @GetMapping("/detail-name/{name}")
    public ResponseEntity<?> getByName(@PathVariable("name") String name) {
        if (!productService.existByName(name)) {
            return new ResponseEntity<>(new Message("El producto solicitado no existe"), HttpStatus.NOT_FOUND);
        }
        Product product = productService.getByName(name).get();
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    //create new product
    @PostMapping("")
    public ResponseEntity<Message> create(@RequestBody ProductDto productDto) {
        if (productDto.getName() == null) {
            return new ResponseEntity<Message>(new Message("El nombre del producto es obligatorio"), HttpStatus.BAD_REQUEST);
        }
        if (productDto.getPrice() == null || productDto.getPrice() < 0) {
            return new ResponseEntity<Message>(new Message("El precio debe ser mayor que 0.0"), HttpStatus.BAD_REQUEST);
        }
        if (productService.existByName(productDto.getName())) {
            return new ResponseEntity<Message>(new Message("El El producto ya se encuentra registrado"), HttpStatus.BAD_REQUEST);
        }
        Product product = new Product(productDto.getName(), productDto.getPrice());
        productService.save(product);
        return new ResponseEntity<Message>(new Message("Producto creado con éxito"), HttpStatus.CREATED);
    }

    //update product
    @PutMapping("/{id}")
    public ResponseEntity<Message> update(@PathVariable("id") int id, @RequestBody ProductDto productDto) {
        if (!productService.existsById(id)) {
            return new ResponseEntity<Message>(new Message("El El producto no existe"), HttpStatus.NOT_FOUND);
        }
        if (productService.existByName(productDto.getName()) && productService.getByName(productDto.getName()).get().getId() != id) {
            return new ResponseEntity<Message>(new Message("El nombre ya se encuentra registrado"), HttpStatus.BAD_REQUEST);
        }
        if (productDto.getName() == null) {
            return new ResponseEntity<Message>(new Message("El nombre del producto es obligatorio"), HttpStatus.BAD_REQUEST);
        }
        if (productDto.getPrice() == null || productDto.getPrice() < 0) {
            return new ResponseEntity<Message>(new Message("El precio debe ser mayor que 0.0"), HttpStatus.BAD_REQUEST);
        }
        Product product = productService.getOne(id).get();
        product.setName(productDto.getName());
        product.setPrice(productDto.getPrice());

        productService.save(product);
        return new ResponseEntity<Message>(new Message("Producto actualizado con éxito"), HttpStatus.CREATED);
    }

    //if user has role "ADMIN", then he can delete any product through this endpoint
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Message> delete(@PathVariable("id") int id) {
        if (!productService.existsById(id)) {
            return new ResponseEntity<Message>(new Message("El producto a eliminar no existe"), HttpStatus.NOT_FOUND);
        }
        productService.delete(id);
        return new ResponseEntity<Message>(new Message("Producto Eliminado"), HttpStatus.OK);
    }
}
