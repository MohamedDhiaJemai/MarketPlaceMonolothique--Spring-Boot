package com.app.dao;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.app.entities.Product;


public interface Produit extends MongoRepository<Product,String> 
{
	List<Product>findBydesignation(String designation);
	List<Product>findByDescriptionAndPrice(String description, double price);
	List<Product>findByPriceBetween(double p2, double p1);
	List<Product>findByDescriptionContaining(String description);
	
	

}
