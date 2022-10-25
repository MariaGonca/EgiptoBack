package com.example.demo.modelo.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.modelo.Producto;


public interface IProductoDao extends JpaRepository<Producto, Long>{
	public List<Producto> findByFavTrue();

}
