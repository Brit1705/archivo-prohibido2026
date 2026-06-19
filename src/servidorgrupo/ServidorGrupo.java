package servidorgrupo;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class ServidorGrupo {

    // 🔑 CREDENCIALES DE CLEVER CLOUD (Remplaza estos textos con tus datos reales de la web)
    private static final String DB_HOST = "bifz9f7gbre71iqzrmft-mysql.services.clever-cloud.com";
    private static final String DB_NAME = "bifz9f7gbre71iqzrmft";
    private static final String DB_USER = "ue98nui8sigael4a";
    private static final String DB_PASS = "1ZIUb34Rz9bfhwnBdLIS";
    private static final String DB_PORT = "3306"; // Por defecto en Clever Cloud es 3306

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        
        // 1. Ruta para el proceso de Login
        server.createContext("/login", new LoginHandler());
        
        // 2. Ruta para servir la página Principal
        server.createContext("/principal", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                java.io.File file = new java.io.File("src/web/principal.html");
                enviarArchivo(exchange, file, "text/html");
            }
        });

        // ⭐ Ruta específica para el archivo de las reglas
        server.createContext("/reglas.html", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                java.io.File file = new java.io.File("src/web/reglas.html");
                enviarArchivo(exchange, file, "text/html");
            }
        });
        
        // ⭐ Ruta específica para el archivo de las encuestas
        server.createContext("/encuestas.html", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                java.io.File file = new java.io.File("src/web/encuestas.html");
                enviarArchivo(exchange, file, "text/html");
            }
        });
        
        // ⭐ Ruta específica para el archivo de confesiones
        server.createContext("/confesiones.html", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                java.io.File file = new java.io.File("src/web/confesiones.html");
                enviarArchivo(exchange, file, "text/html");
            }
        });
        
        // ⭐ Ruta específica para el archivo de miembros
        server.createContext("/miembros.html", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                java.io.File file = new java.io.File("src/web/miembros.html");
                enviarArchivo(exchange, file, "text/html");
            }
        });

        // ⭐ Ruta dinámica para que el navegador pueda leer cualquier foto de la carpeta 'imagenes'
        server.createContext("/imagenes/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                String path = exchange.getRequestURI().getPath();
                java.io.File file = new java.io.File("src/web" + path);
                if (file.exists() && !file.isDirectory()) {
                    enviarArchivo(exchange, file, "image/jpeg");
                } else {
                    String respuesta = "Imagen no encontrada";
                    exchange.sendResponseHeaders(404, respuesta.length());
                    java.io.OutputStream os = exchange.getResponseBody();
                    os.write(respuesta.getBytes());
                    os.close();
                }
            }
        });
        
        // ⭐ Ruta específica para el chat grupal
        server.createContext("/chat.html", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                java.io.File file = new java.io.File("src/web/chat.html");
                enviarArchivo(exchange, file, "text/html");
            }
        });
        
        // ⭐ Ruta específica para El Tribunal
        server.createContext("/tribunal.html", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                java.io.File file = new java.io.File("src/web/tribunal.html");
                enviarArchivo(exchange, file, "text/html");
            }
        });
        
        // ⭐ Ruta específica para el Panel de Reportes de Admin
        server.createContext("/reportes.html", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                java.io.File file = new java.io.File("src/web/reportes.html");
                enviarArchivo(exchange, file, "text/html");
            }
        });
        
        // ⭐ Ruta específica para la Sala de Administración Privada
        server.createContext("/sala-admin.html", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                java.io.File file = new java.io.File("src/web/sala-admin.html");
                enviarArchivo(exchange, file, "text/html");
            }
        });

        // 3. Ruta de inicio (Login HTML) - DEBE IR AL FINAL
        server.createContext("/", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                java.io.File file = new java.io.File("src/web/index.html");
                enviarArchivo(exchange, file, "text/html");
            }
        });
        
        server.setExecutor(null); 
        System.out.println("🚀 Servidor corriendo en http://localhost:8080");
        server.start();
    }

    private static void enviarArchivo(HttpExchange exchange, java.io.File file, String contentType) throws IOException {
        if (file.exists()) {
            exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=UTF-8");
            exchange.sendResponseHeaders(200, file.length());
            try (OutputStream os = exchange.getResponseBody()) {
                java.nio.file.Files.copy(file.toPath(), os);
            }
        } else {
            String error = "No se encontro el archivo: " + file.getName();
            exchange.sendResponseHeaders(404, error.length());
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(error.getBytes());
            }
        }
    }

    static class LoginHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                java.io.InputStream is = exchange.getRequestBody();
                java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    bos.write(buffer, 0, len);
                }
                String query = bos.toString("UTF-8");

                Map<String, String> params = new HashMap<>();
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    if (pair.length > 1) {
                        params.put(java.net.URLDecoder.decode(pair[0], "UTF-8"), java.net.URLDecoder.decode(pair[1], "UTF-8"));
                    }
                }

                String txtUsuario = params.get("usuario");
                String txtContrasena = params.get("contrasena");

                // Buscamos el usuario y obtenemos sus datos desde MySQL
                String datosUsuario = obtenerDatosUsuario(txtUsuario, txtContrasena);

                String respuesta = (datosUsuario != null) ? "OK," + datosUsuario : "ERROR";
                
                exchange.sendResponseHeaders(200, respuesta.length());
                OutputStream os = exchange.getResponseBody();
                os.write(respuesta.getBytes());
                os.close();
            }
        }

        private String obtenerDatosUsuario(String usuario, String contrasena) {
            // URL de conexión remota para MySQL
            String url = "jdbc:mysql://" + DB_HOST + ":" + DB_PORT + "/" + DB_NAME + "?useSSL=false&serverTimezone=UTC";
            
            // Mantenemos tus mayúsculas exactas del Access en las columnas
            String sql = "SELECT NombreMostrado, Rol FROM Usuarios WHERE Usuario = ? AND Contrasena = ?";
            
            try {
                // Forzamos la carga del Driver de MySQL que metiste en Libraries
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                try (Connection conn = DriverManager.getConnection(url, DB_USER, DB_PASS);
                     PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    
                    pstmt.setString(1, usuario);
                    pstmt.setString(2, contrasena);
                    
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            // Retorna "Nombre,Rol" igual a como lo procesaba antes
                            return rs.getString("NombreMostrado") + "," + rs.getString("Rol");
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println("❌ Error en MySQL Remoto: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }
    }
}