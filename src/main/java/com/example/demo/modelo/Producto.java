package com.example.demo.modelo;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import org.hibernate.annotations.ColumnDefault;

@Entity
@Table(name="productos")
public class Producto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID_PRODUCTO")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idProducto;

	@Column(name = "NOMBRE")
	@NotEmpty(message = "no puede estar vacio")
	private String nombre;

	@Column(name = "URL")
	@NotEmpty(message = "no puede estar vacio")
	private String url;

	@Column(name = "DESCRIPCION")
	@NotEmpty(message = "no puede estar vacio")
	private String descripcion;

	@Column(name = "PRECIO")
	private double precio;

	@Column(name = "CANTIDAD")
	private Integer cantidadCompra;

	@Column(name = "SUBTOTAL")
	private Double precioTotal;

	@Column(name="FAV")
	@ColumnDefault("false")
	private Boolean fav;

	@Column(name = "PRECIO_CARRITO", nullable = true)
	private Double carritoPrecio;
	

	public Double getCarritoPrecio() {
		return carritoPrecio;
	}

	public void setCarritoPrecio(Double carritoPrecio) {
		this.carritoPrecio = carritoPrecio;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public Long getIdProducto() {
		return idProducto;
	}

	public void setIdProducto(Long idProducto) {
		this.idProducto = idProducto;
	}

	public Integer getCantidadCompra() {
		return cantidadCompra;
	}

	public void setCantidadCompra(Integer cantidadCompra) {
		this.cantidadCompra = cantidadCompra;
	}

	public Double getPrecioTotal() {
		return precioTotal;
	}

	public void setPrecioTotal(Double precio, int cantidadCompra) {
		this.precioTotal = (precio * cantidadCompra);
	}

	public Boolean isFav() {
		return fav;
	}

	public void setFav(Boolean fav) {
		this.fav = fav;
	}
	
	

	public Producto() {
	}

	public Producto(Long idProducto, String nombre, String url, String descripcion, double precio, Integer cantidadCompra, Double precioTotal) {
		this.idProducto = idProducto;
		this.nombre = nombre;
		this.url = url;
		this.descripcion = descripcion;
		this.precio = precio;
		this.cantidadCompra = cantidadCompra;
		this.precioTotal = precioTotal;
	}

	public Producto(Long idProducto, String nombre, String url, String descripcion, double precio) {
		this.idProducto = idProducto;
		this.nombre = nombre;
		this.url = url;
		this.descripcion = descripcion;
		this.precio = precio;
	}

	public Producto(Long idProducto, String nombre, String url, String descripcion, double precio, Integer cantidadCompra, Double precioTotal, Boolean fav) {
		this.idProducto = idProducto;
		this.nombre = nombre;
		this.url = url;
		this.descripcion = descripcion;
		this.precio = precio;
		this.cantidadCompra = cantidadCompra;
		this.precioTotal = precioTotal;
		this.fav = false;
	}

	@Override
	public String toString() {
		return "Producto [idProducto=" + idProducto + ", nombre=" + nombre + ", url=" + url + ", descripcion="
				+ descripcion + ", precio=" + precio + ", cantidadCompra=" + cantidadCompra + ", precioTotal="
				+ precioTotal + ", fav=" + fav + "]";
	}
}
