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
                case 3:
                    borrarLibro(sentencia);
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

    private static void borrarLibro(Statement sentencia) {
        System.out.println("Dame nombre del libro que deseas eliminar");
        String nombreLibro = sc.nextLine();

        try {
            // Verificar si el libro existe
            ResultSet resultado = sentencia.executeQuery("SELECT * FROM Libros WHERE Titulo = '" + nombreLibro + "'");

            if (resultado.next()) {
                // Si el libro existe, solicitar confirmación para eliminar
                System.out.println("¿Seguro que deseas eliminar el libro? (s para sí, n para no)");
                String confirmacion = sc.nextLine();

                if (confirmacion.equalsIgnoreCase("s")) {
                    // Eliminar el libro de la base de datos
                    sentencia.executeUpdate("DELETE FROM Libros WHERE Titulo = '" + nombreLibro + "'");
                    System.out.println("Libro eliminado correctamente.");
                } else {
                    System.out.println("Operación cancelada.");
                }
            } else {
                // El libro no existe en la base de datos
                System.out.println("Error: El libro '" + nombreLibro + "' no existe en la base de datos.");
            }
        } catch (SQLException e) {
            System.err.println("Se ha producido un error al eliminar el libro.");
            e.printStackTrace();
        }
    }

    private static void borrarAutor(Statement sentencia) {
        System.out.println("Dame el nombre del autor que deseas eliminar");
        String nombreAutor = sc.nextLine();

        try {
            // Verificar si el autor existe
            ResultSet resultado = sentencia.executeQuery("SELECT * FROM Autores WHERE Nombre = '" + nombreAutor + "'");

            if (resultado.next()) {
                // Si el autor existe, obtener su DNI
                String dniAutor = resultado.getString("DNI");

                // Eliminar los libros asociados al autor
                sentencia.executeUpdate("DELETE FROM Libros WHERE Autor = '" + dniAutor + "'");
                System.out.println("Libros del autor eliminados correctamente.");

                // Eliminar al autor
                sentencia.executeUpdate("DELETE FROM Autores WHERE Nombre = '" + nombreAutor + "'");
                System.out.println("Autor eliminado correctamente.");
            } else {
                // El autor no existe en la base de datos
                System.out.println("Error: El autor '" + nombreAutor + "' no existe en la base de datos.");
            }
        } catch (SQLException e) {
            System.err.println("Se ha producido un error al eliminar el autor.");
            e.printStackTrace();
        }
    }

    private static void consultarLibroPorTitulo(Statement sentencia) {
        System.out.println("Introduce el título del libro que deseas consultar:");
        String tituloLibro = sc.nextLine();

        try {
            // Consultar el libro por título
            ResultSet resultado = sentencia.executeQuery("SELECT * FROM Libros WHERE Titulo = '" + tituloLibro + "'");

            if (resultado.next()) {
                // Si se encuentra el libro, mostrar sus detalles
                String titulo = resultado.getString("Titulo");
                float precio = resultado.getFloat("Precio");
                String autorDNI = resultado.getString("Autor");

                // Obtener el nombre del autor utilizando el DNI del libro
                ResultSet resultadoAutor = sentencia.executeQuery("SELECT Nombre FROM Autores WHERE DNI = '" + autorDNI + "'");
                resultadoAutor.next();
                String nombreAutor = resultadoAutor.getString("Nombre");

                System.out.println("Título: " + titulo);
                System.out.println("Precio: " + precio);
                System.out.println("Autor: " + nombreAutor);
            } else {
                // Si no se encuentra el libro, mostrar un mensaje de error
                System.out.println("No se encontró ningún libro con el título '" + tituloLibro + "'.");
            }
        } catch (SQLException e) {
            System.err.println("Se ha producido un error al consultar el libro por título.");
            e.printStackTrace();
        }
    }

    private static void consultarLibrosPorAutor(Statement sentencia) {
        System.out.println("Introduce el nombre del autor:");
        String nombreAutor = sc.nextLine();

        try {
            // Consultar el autor por nombre para obtener su DNI
            ResultSet resultadoAutor = sentencia.executeQuery("SELECT DNI FROM Autores WHERE Nombre = '" + nombreAutor + "'");

            if (resultadoAutor.next()) {
                // Si se encuentra el autor, obtener su DNI
                String autorDNI = resultadoAutor.getString("DNI");

                // Consultar los libros del autor utilizando su DNI
                ResultSet resultadoLibros = sentencia.executeQuery("SELECT * FROM Libros WHERE Autor = '" + autorDNI + "'");

                if (resultadoLibros.next()) {
                    // Si el autor tiene libros, mostrar los detalles de cada libro
                    System.out.println("Libros del autor '" + nombreAutor + "':");
                    do {
                        String titulo = resultadoLibros.getString("Titulo");
                        float precio = resultadoLibros.getFloat("Precio");

                        System.out.println("Título: " + titulo + ", Precio: " + precio);
                    } while (resultadoLibros.next());
                } else {
                    // Si el autor no tiene libros, mostrar un mensaje indicando que no hay libros del autor
                    System.out.println("El autor '" + nombreAutor + "' no tiene libros asociados.");
                }
            } else {
                // Si no se encuentra el autor, mostrar un mensaje indicando que el autor no existe
                System.out.println("No se encontró ningún autor con el nombre '" + nombreAutor + "'.");
            }
        } catch (SQLException e) {
            System.err.println("Se ha producido un error al consultar los libros del autor.");
            e.printStackTrace();
        }
    }



}