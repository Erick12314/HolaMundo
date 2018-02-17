package com.costureria.pruebas.util;

import java.sql.Connection;
import java.sql.DriverManager;

public class Conexion {
    
    private static Conexion con = null;
    
    private static final String usuario = "";
    private static final String password = "";
    private static final String puerto = "";
    private static final String baseDatos = "";
    private static final String direccion = "";
    private static String url;
    private Connection conector;
    
    private Conexion() {
        try {
            url = "jdbc:mysql://" + direccion + ":" + puerto + "/" + baseDatos;
            
            Class.forName("com.mysql.jdbc.Driver");
            conector = DriverManager.getConnection(url, usuario, password);
            
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    
    public Conexion getInstance() throws Exception{
        if( con == null ){
            con = new Conexion();
        }
        return con;
    }
    
    public Connection getConexion(){
        return conector;
    }
    
}
