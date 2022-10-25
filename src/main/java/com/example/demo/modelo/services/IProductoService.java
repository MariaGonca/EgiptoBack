package com.example.demo.modelo.services;

import java.util.List;

import com.example.demo.modelo.Producto;

public interface IProductoService {

	public List<Producto> findAll();
	
	public Producto findById(Long id);
	
	public Producto save(Producto foto);
	
	public void delete(Long id);
	
	List<Producto> findFav();
	
}
