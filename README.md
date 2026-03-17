# Cristabel De Viaje

Aplicación web desarrollada con **Java y Spring Boot** que permite gestionar contenido relacionado con viajes.

---

## Tecnologías utilizadas

* Java
* Spring Boot
* Maven
* MySQL

---

## Requisitos

Antes de ejecutar el proyecto, es necesario tener instalado:

* Java (17 o superior)
* MySQL
* Maven (opcional, ya que el proyecto incluye Maven Wrapper)

---

## Ejecución del proyecto

1. Clonar el repositorio:

```bash
git clone https://github.com/TU_USUARIO/TU_REPO.git
```

2. Acceder a la carpeta del proyecto:

cd cristabel-viaje

3. Configurar la base de datos en:

src/main/resources/application.properties

Ejemplo:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/cristabeldeviaje?serverTimezone=UTC&createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=TU_PASSWORD
```

4. Ejecutar la aplicación:

En Linux/Mac:

./mvnw spring-boot:run

En Windows:

mvnw.cmd spring-boot:run

---

## Base de datos

* El proyecto utiliza **MySQL**
* La base de datos se crea automáticamente si no existe (`createDatabaseIfNotExist=true`)
* Las tablas se generan automáticamente gracias a Hibernate (`spring.jpa.hibernate.ddl-auto=update`)

---

## 🌐 Acceso a la aplicación

Una vez iniciada la aplicación, acceder desde el navegador:

http://localhost:8080
