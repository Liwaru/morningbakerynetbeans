/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package morning_bakery;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class koneksi {

    private final String url = "jdbc:mysql://localhost/morning_bakery";
    private final String username = "root";
    private final String password = "";

    private Connection con;

    public void connect() {

        try {

            con = DriverManager.getConnection(url, username, password);
            System.out.println("Koneksi Berhasil");

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(null,
                    "Koneksi gagal : " + e.getMessage());

        }

    }

    public Connection getCon() {
        return con;
    }

}