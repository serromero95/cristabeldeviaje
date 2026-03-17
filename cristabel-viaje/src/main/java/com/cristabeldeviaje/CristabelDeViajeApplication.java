package com.cristabeldeviaje;

import com.cristabeldeviaje.model.*;
import com.cristabeldeviaje.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CristabelDeViajeApplication {

	public static void main(String[] args) {
		SpringApplication.run(CristabelDeViajeApplication.class, args);
	}

	@Bean
	CommandLineRunner initData(UsuarioRepository usuarioRepo, ProductoRepository productoRepo, CategoriaRepository categoriaRepo) {
		return args -> {
			if (categoriaRepo.count() == 0) {
				Categoria cat1 = new Categoria(); cat1.setNombre("Ropa de senderismo");
				Categoria cat2 = new Categoria(); cat2.setNombre("Calzado");
				Categoria cat3 = new Categoria(); cat3.setNombre("Accesorios");
				categoriaRepo.save(cat1); categoriaRepo.save(cat2); categoriaRepo.save(cat3);
			}

			if (usuarioRepo.count() == 0) {
				Usuario sergio = new Usuario();
				sergio.setNombre("Sergio");
				sergio.setEmail("sergioromeroboiro@gmail.com");
				sergio.setPassword("1234");
				sergio.setRol("ADMIN");
				usuarioRepo.save(sergio);
			}

			if (productoRepo.count() == 0) {
				Categoria ropa = categoriaRepo.findAll().get(0);
				Categoria calzado = categoriaRepo.findAll().get(1);

				Producto p1 = new Producto();
				p1.setNombre("Chaqueta Térmica");
				p1.setDescripcion("Chaqueta cortavientos ideal para rutas de alta montaña con clima frío.");
				p1.setPrecio(89.99);
				p1.setImagen("chaqueta.jpg");
				p1.setCategoria(ropa);

				Producto p2 = new Producto();
				p2.setNombre("Botas de Montaña");
				p2.setDescripcion("Botas resistentes al agua con suela de agarre extremo.");
				p2.setPrecio(120.00);
				p2.setImagen("botas.jpg");
				p2.setCategoria(calzado);

				productoRepo.save(p1);
				productoRepo.save(p2);
				System.out.println("✅ Datos de Cristabel de Viaje cargados con éxito.");
			}
		};
	}
}