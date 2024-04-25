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
            System.err.println("Se ha producido un error al insertar");
        }
    }


}