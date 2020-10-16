package com.pruebatecnica.app.controladores;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pruebatecnica.app.entidades.Categorias;
import com.pruebatecnica.app.entidades.Permisos;
import com.pruebatecnica.app.entidades.Productos;
import com.pruebatecnica.app.excepciones.RecursoNoEncontradoExcepcion;
import com.pruebatecnica.app.postobj.ProductosPost;
import com.pruebatecnica.app.repositorio.CategoriasRepo;
import com.pruebatecnica.app.repositorio.ProductosRepo;



@RestController
@RequestMapping("/api/v1")
public class ControladorProductos {
	
	

	
	
	@Autowired
	private ProductosRepo productosRepo;
	
	@Autowired
	private CategoriasRepo categoriasRepo;
	
	
	// get productos
	@GetMapping("/productos")
	public List<Productos> getAllProductos(){
		return (List<Productos>) this.productosRepo.findAll();
		}
	
	// get usuario por id
	@GetMapping("/productos/{id}")
	public Productos getProductosPorId(@PathVariable(value = "id") Integer id) {
		return this.productosRepo.findById(id).orElseThrow(() -> new RecursoNoEncontradoExcepcion("Productos no encontrado"));
	}
	
	//post(crear) productos
	@PostMapping("/productos")
	public Productos crearProductos( @RequestBody ProductosPost post ) throws Exception  {
		

		String nombre = post.getNombre();
		Float costo = post.getCosto();
		Float precio = post.getPrecio();
		String tags = post.getTags();
		List<String> categorias = post.getCategorias();
		
		
		List<Categorias> catExistentes = extraerCategorias(categorias);
		
		Productos producto = new Productos(nombre, costo, precio, tags);
		
		
		agregarCategorias(catExistentes, producto);
		

		
		
		
		/*System.out.println("nombre = " + nombre);
		System.out.println("apellido = " + apellido);
		System.out.println("usuario_nom = " + usuario_nom);
		System.out.println("pass = " + pass);
		System.out.println("nombre_permiso = " + nombre_permiso);*/

		
	
		 

	
		return this.productosRepo.save(producto);
	}

	private void agregarCategorias(List<Categorias> catExistentes, Productos producto) {
		for(Categorias cat: catExistentes) {
			cat.getProductos().add(producto);
			producto.getCategorias().add(cat);
		}
	}

	
	
	
	private List<Categorias> extraerCategorias(List<String> categorias) {
		List<Categorias> catExistentes = new ArrayList<>();
		
		
		for(String cat : categorias) {
			
			Categorias temp = this.categoriasRepo.getCategoriaByNombre(cat);
			
			
			
			if(temp == null) {
				throw new UsernameNotFoundException("Categoria: " + temp + " no encontrado" );
			}
			
			
			catExistentes.add(temp);
			
			
		}
		return catExistentes;
	}
	
	
	//put(update) usuario
	
	@PutMapping("/productos/{id}")
	public Productos updateProductos( @RequestBody ProductosPost post, @PathVariable ("id") Integer id) {
		Productos ProductoActual = this.productosRepo.findById(id).orElseThrow(() -> new RecursoNoEncontradoExcepcion("Productos no encontrado"));
		
		
		ProductoActual.setNombre(post.getNombre());
		ProductoActual.setCosto(post.getCosto());
		ProductoActual.setPrecio(post.getPrecio());
		ProductoActual.setEstado(post.getEstado());
		ProductoActual.setUlt_actualizacion();
		
		
		List<Categorias> catExistentes = extraerCategorias(post.getCategorias());
		


		actualizarCategorias(ProductoActual, catExistentes);
		
	
		
		
		return this.productosRepo.save(ProductoActual);
	}
	
	

	private void actualizarCategorias(Productos ProductoActual, List<Categorias> catExistentes) {
		ProductoActual.getCategorias().clear();
		
		for(Categorias cat : catExistentes) {
			
			cat.getProductos().clear();
			cat.getProductos().add(ProductoActual);
			
			
			ProductoActual.getCategorias().add(cat);
			
		}
	}
	
	
	
	//borrar/desactivar por id
	
	@DeleteMapping("/productos/{id}")
	public ResponseEntity<Productos> deactivarProductos( @PathVariable ("id") Integer id) {
		Productos ProductoActual = this.productosRepo.findById(id).orElseThrow(() -> new RecursoNoEncontradoExcepcion("Productos no encontrado"));
		
	
	   return null;
	}
	
		

}