package com.app.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

//Aggregation Import//
import static org.springframework.data.mongodb.core.aggregation.Aggregation.project;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.group;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;

//******************//

import com.app.dao.Produit;
import com.app.entities.MeilleurNote;
import com.app.entities.Product;
import com.app.entities.TotalIp;

@RestController
@RequestMapping("/produit")
@CrossOrigin(origins= {"http://localhost:4200"})
public class productController {

	@Autowired
	private Produit p;

	@Autowired
	private MongoTemplate PR1;

	/* 0- All Product : //test : http://localhost:8484/produit/all */
	@GetMapping("/all")
	public Iterable<Product> getall() {
		return (p.findAll());

	}

	/* 1- ajouter un produit donné */
	@PostMapping(value = "/add")
	public Product save(@RequestBody Product product) {
		return p.save(product);
	}

	/* 2- Delete Product */
	@DeleteMapping("/delete/{id}")
	public List<Product> delete(@PathVariable("id") String id) {
		p.deleteById(id);
		return p.findAll();
	}

	/* 3- update un produit donnée */
	@PutMapping("/update/{id}")
	public Product update(@PathVariable("id") String id, @RequestBody Product pro) {
		pro.setId(id);
		return p.save(pro);
	}

	/* 4- Find By Designation */
	@GetMapping("/designation/{id}")
	public List<Product> findBydesignation(@PathVariable("id") String id) {
		return (p.findBydesignation(id));

	}

	/* 5- Description Containing */
	@GetMapping(value = "/findByDescriptionContaining/{infix}")
	public List<Product> RechercheAvec(@PathVariable("infix") String infix) {
		return (p.findByDescriptionContaining(infix));
	}

	/* 6- Find By Price Between */
	@GetMapping("/RechercheBetween/{p1}/{p2}")
	public List<Product> findByPriceBetween(@PathVariable("p1") double p1, @PathVariable("p2") double p2) {
		return (p.findByPriceBetween(p1, p2));

	}

	/*- 6- All Product By Page */
	@RequestMapping("allproductctp/{page}")
	public Page<Product> afficherproduitparpage(@PathVariable("page") int page) {
		return (p.findAll(PageRequest.of(page, 3)));
	}

	/* 7- All Product By NbrOfPage and NbreOfProduct */
	@GetMapping("/allproduct/{page}/{NOMBRE}")
	public Page<Product> afficherproduitpage(@PathVariable("page") int page, @PathVariable("NOMBRE") int NOMBRE) {
		return (p.findAll(PageRequest.of(page, NOMBRE, Sort.by("price").descending())));
	}

	/* 8-Find By Description And Price */
	@GetMapping("/description/{iddes}/{idprice}")
	public List<Product> findByDescriptionAndPrice(@PathVariable("iddes") String iddes,
			@PathVariable("idprice") double idprice) {
		return (p.findByDescriptionAndPrice(iddes, idprice));
	}

	@GetMapping("/Pmax")
	public List<Product> PmaxPrice() {

		Query Q1 = new Query();
		Q1.with(new Sort((Sort.Direction.DESC), "price"));
		Q1.limit(1);

		double P = PR1.findOne(Q1, Product.class).getPrice();
		Query Q2 = new Query();
		Q2.addCriteria(Criteria.where("Price").is(P));

		return PR1.find(Q2, Product.class);

	}

	@GetMapping("/totalprix")
	public double Totalprix() {

		GroupOperation gr = group().sum("price").as("Total");

		ProjectionOperation po = project("Total");

		AggregationResults<TotalIp> result = PR1.aggregate(newAggregation(gr, po), Product.class, TotalIp.class);

		return result.getUniqueMappedResult().Total;
	}

	@GetMapping("/Totalproduitmc/{mc}")
	public double Totalproduitmc(@PathVariable("mc") String mc) {

		MatchOperation Q1 = match(Criteria.where("designation").regex(mc));

		GroupOperation gr = group().sum("price").as("Total");

		ProjectionOperation po = project("Total");
		AggregationResults<TotalIp> result = PR1.aggregate(newAggregation(Q1, gr, po), Product.class, TotalIp.class);

		return result.getUniqueMappedResult().Total;
	}

		
		
		
	
	
	

}
