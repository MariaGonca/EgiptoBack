package com.example.demo.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.modelo.Producto;
import com.example.demo.modelo.services.IProductoService;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api")
public class ProductoRestController {
	
	@Autowired
	private IProductoService productoService;
	
	@GetMapping("/productos")
	public List<Producto> index(){
		return productoService.findAll();
	}

	@GetMapping("/productos/carrito")
	public List<Producto> favoritas(){
		
		Double valor = 0.0;
		List<Producto> favs;
		favs = productoService.findFav();	
		for (Producto producto : favs) {
			producto.setPrecioTotal(producto.getPrecio(), producto.getCantidadCompra());
			valor = valor + producto.getPrecioTotal();
			producto.setCarritoPrecio(valor);
			for (Producto producto2 : favs) {
				producto2.setCarritoPrecio(valor);
			}
		}
		return productoService.findFav();
	}
	
	
	@GetMapping("/productos/carrito/total")
	public Double favoritasTotal(){
		
		Double valor = 0.0;
		List<Producto> favs;
		favs = productoService.findFav();	
		for (Producto producto : favs) {
			producto.setPrecioTotal(producto.getPrecio(), producto.getCantidadCompra());
			valor = valor + producto.getPrecioTotal();
			producto.setCarritoPrecio(valor);
			for (Producto producto2 : favs) {
				producto2.setCarritoPrecio(valor);
			}
		}
		return valor;
	}
	
	@GetMapping("/productos/{idProducto}")
	public ResponseEntity<?> show(@PathVariable Long idProducto){
		Producto producto = null;
		Map<String, Object> response = new HashMap<>();	
		try {
			producto = productoService.findById(idProducto);			
		}catch(DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if(producto == null) {
			response.put("mensaje", "La publicación con ID: ".concat(idProducto.toString().concat(" no existe")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Producto>(producto, HttpStatus.OK);
	}
	
	
	@PostMapping("/productos/{idProducto}")
	public ResponseEntity<?> setFav(@Valid @RequestBody Producto producto, BindingResult result, @PathVariable Long idProducto) {
		Producto productoActual = productoService.findById(idProducto);
		
		Map<String, Object> response = new HashMap<>();
		
		if(result.hasErrors()) {
			
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo "+err.getDefaultMessage()+ ", "+err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		if(productoActual == null) {
			response.put("mensaje", "La publicación con ID: ".concat(idProducto.toString().concat(" no se puedo editar, no existe")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
	
		try {
	
			if(productoActual.isFav()) {
				productoActual.setFav(false);
				productoActual = productoService.save(productoActual);
			}else {
				productoActual.setFav(true);
				productoActual = productoService.save(productoActual);
			}
		}catch(DataAccessException e) {
			response.put("mensaje", "Error al actualizar en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Publicación actualizada con éxtito");
		response.put("foto", productoActual);
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	@PostMapping("/productos")
	public ResponseEntity<?> create(@Valid @RequestBody Producto producto, BindingResult result) {
		Producto productoNew = null;
		producto.setCantidadCompra(1);
		producto.setFav(false);
		Map<String, Object> response = new HashMap<>();	
		
		if(result.hasErrors()) {
			
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo "+err.getDefaultMessage()+ ", "+err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			productoNew = productoService.save(producto);
		}catch(DataAccessException e) {
			response.put("mensaje", "Error al insertar en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Publicación realizada con éxtito");
		response.put("foto", productoNew);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	
	
	@PutMapping("/productos/{idProducto}")
	public ResponseEntity<?> update(@Valid @RequestBody Producto producto, BindingResult result, @PathVariable Long idProducto) {
		Producto productoActual = productoService.findById(idProducto);
		Producto productoUpdated = null;
		Map<String, Object> response = new HashMap<>();
		
		if(result.hasErrors()) {
			
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo "+err.getDefaultMessage()+ ", "+err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		if(productoActual == null) {
			response.put("mensaje", "La publicación con ID: ".concat(idProducto.toString().concat(" no se puedo editar, no existe")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
	
		try {
			productoActual.setDescripcion(producto.getDescripcion());
			productoActual.setNombre(producto.getNombre());
			productoActual.setUrl(producto.getUrl());
			productoActual.setPrecio(producto.getPrecio());
		
			productoUpdated = productoService.save(productoActual);
		}catch(DataAccessException e) {
			response.put("mensaje", "Error al actualizar en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Publicación actualizada con éxtito");
		response.put("producto", productoUpdated);
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
	
	
	
	@DeleteMapping("/productos/{idProducto}")
	public ResponseEntity<?> delete(@PathVariable Long idProducto) {
		
		Map<String, Object> response = new HashMap<>();
		
		try {
		    productoService.delete(idProducto);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar el publicación de la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "La publicación se ha eliminado con éxito!");
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	
	@GetMapping("/productos/carrito/suma")
	public List<Producto> favoritasSuma(){
		
		List<Producto> favs;
		favs = productoService.findFav();	
		for (Producto producto : favs) {
		producto.setPrecioTotal(producto.getPrecio(), producto.getCantidadCompra());
		}
		System.out.println(favs);
		return favs;
	}
	
	@PutMapping("/productos/setCantidad/{idProducto}")
	public ResponseEntity<?> setCantidad(@Valid @RequestBody Producto producto, BindingResult result, @PathVariable Long idProducto) {
		Producto productoActual = productoService.findById(idProducto);
		Producto productoUpdated = null;
		List<Producto> favs = productoService.findFav();
		Map<String, Object> response = new HashMap<>();
		
		if(result.hasErrors()) {
			
			List<String> errors = result.getFieldErrors()
					.stream()
					.map(err -> "El campo "+err.getDefaultMessage()+ ", "+err.getDefaultMessage())
					.collect(Collectors.toList());
			
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		if(productoActual == null) {
			response.put("mensaje", "La publicación con ID: ".concat(idProducto.toString().concat(" no se puedo editar, no existe")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
	
		try {
			productoActual.setCantidadCompra(producto.getCantidadCompra());
			productoActual.setPrecioTotal(producto.getPrecio(), producto.getCantidadCompra());
		
			System.out.println(productoActual);
			productoUpdated = productoService.save(productoActual);
			System.out.println(productoUpdated);
		}catch(DataAccessException e) {
			response.put("mensaje", "Error al actualizar en la base de datos");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensaje", "Publicación actualizada con éxtito");
		response.put("producto", productoUpdated);
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
	}
}
