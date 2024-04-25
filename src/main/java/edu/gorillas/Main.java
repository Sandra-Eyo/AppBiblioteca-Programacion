package edu.gorillas;
import java.sql.*;
import java.util.Scanner;


public class Main {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        Statement sentencia = null;
        Connection conexion = null;


        int op = 0;

        try {
            Class.forName("org.mariadb.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        String url = "jdbc:mariadb://localhost:3306/?user=root&password=";
        try {
            conexion = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println("No hay ningún driver que reconozca la URL especificada");
        } catch (Exception e) {
            System.out.println("Se ha producido algún otro error");
        }

        try {
            sentencia = conexion.createStatement();
        } catch (SQLException e) {
            System.out.println("Error");
        }

        CreacionBD.crearBase(sentencia);

        do {
            System.out.println("**** MENU ****\n"
                    + "[1] Insertar un nuevo Autor/a"
                    + "[2] Insertar un nuevo libro"
                    + "[3] Borrar Libro"
                    + "[4] Borrar autor"
                    + "[5] Consultar datos de un libro"
                    + "[6] Consultar libros de un Autor"
                    + "[7] Listar libros"
                    + "[8] Listar autores con sus libros"
                    + "[9] Modificar libro por título"
                    + "[10] Modificar autor por DNI"
                    + "[11] Salir");
            op = sc.nextInt();
            sc.nextLine();

            switch (op) {
                case 1:
                    nuevoAutor(sentencia);
                    break;
                case 2:
                    nuevoLibro(sentencia);
            }

        } while (op != 11);

    }

    private static void nuevoAutor(Statement sentencia) {
        System.out.println("Dame el DNI del nuevo autor");
        String dni = sc.nextLine();
        System.out.println("Dame el nuevo nombre del autor");
        String nombre = sc.nextLine();
        System.out.println("Dame la nacionalidad del nuevo autor");
        String nacionalidad = sc.nextLine();
        sc = new Scanner(System.in);

        try {
            sentencia.executeUpdate("INSERT INTO Autores (DNI, Nombre, Nacionalidad) VALUES('" + dni + "', '" + nombre + "', '" + nacionalidad + "')");
        } catch (SQLException e) {
            System.err.println("Se ha producido un error al insertar el nuevo autor");
        }
    }

    private static void nuevoLibro(Statement sentencia) {
        System.out.println("Dame el título del libro");
        String titulo = sc.nextLine();
        System.out.println("Dame el precio del libro");
        float precio = sc.nextFloat();

        // Agregar una línea para consumir el salto de línea pendiente
        sc.nextLine(); // Consumir el salto de línea pendiente

        System.out.println("Dame el nombre del autor");
        String autorNombre = sc.nextLine();

        try {
            // Verificamos si el autor existe
            ResultSet resultado = sentencia.executeQuery("SELECT * FROM Autores WHERE Nombre = '" + autorNombre + "'");

            if (resultado.next()) {
                // Si el autor existe, se inserta el libro
                String autorDNI = resultado.getString("DNI");
                sentencia.executeUpdate("INSERT INTO Libros (Titulo, Precio, Autor) VALUES('" + titulo + "', " + precio + ", '" + autorDNI + "')");
                System.out.println("Libro insertado correctamente.");
            } else {
                // Si el autor no existe, se lanza el mensaje de error
                System.out.println("Error: El autor '" + autorNombre + "' no existe en la base de datos.");
            }
        } catch (SQLException e) {
            System.err.println("Se ha producido un error al insertar el libro.");
            e.printStackTrace();
        }
    }


}