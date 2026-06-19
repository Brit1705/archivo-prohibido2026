# 1. Usamos la imagen oficial y actualizada de Eclipse Temurin (Java 17)
FROM eclipse-temurin:17-jdk-jammy

# 2. Creamos la carpeta de trabajo en el servidor
WORKDIR /app

# 3. Instalamos 'curl' para descargar el conector de MySQL de forma automática
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

# 4. Creamos una carpeta para las librerías y descargamos el conector MySQL JDBC oficial
RUN mkdir -p lib && \
    curl -L -o lib/mysql-connector-j.jar https://repo1.maven.org/maven2/com/mysql/mysql-connector-j/8.3.0/mysql-connector-j-8.3.0.jar

# 5. Copiamos todo nuestro código fuente y archivos web al servidor
COPY . .

# 6. Compilamos el código enlazando la librería del conector MySQL recién descargada
RUN javac -cp "lib/mysql-connector-j.jar" -d bin src/servidorgrupo/ServidorGrupo.java

# 7. Exponemos el puerto de red 8080 que usa tu aplicación
EXPOSE 8080

# 8. Comando definitivo de arranque incluyendo el Classpath con el Driver de MySQL y la carpeta bin
CMD ["java", "-cp", "bin:lib/mysql-connector-j.jar", "servidorgrupo.ServidorGrupo"]
