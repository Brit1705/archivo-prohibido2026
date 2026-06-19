# 1. Usamos una imagen oficial de Java de peso ligero
FROM openjdk:17-jdk-slim

# 2. Creamos la carpeta de trabajo dentro del servidor de internet
WORKDIR /app

# 3. Copiamos todo nuestro código y carpetas (incluyendo src y carpetas web) al servidor
COPY . .

# 4. Compilamos el archivo principal de Java apuntando a su ruta correcta
RUN javac -d bin src/servidorgrupo/ServidorGrupo.java

# 5. Le decimos al servidor que exponga el puerto 8080 (donde escucha tu código)
EXPOSE 8080

# 6. El comando definitivo para encender tu programa
CMD ["java", "-cp", "bin", "servidorgrupo.ServidorGrupo"]
