package controller;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import logic.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Controller {
    @FXML private TextField nameField;
    @FXML private TextField lastNameField;
    @FXML private TextField ratingField;
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student,Integer> idColumn;
    @FXML private TableColumn<Student, String> nameColumn;
    @FXML private TableColumn<Student, String> lastNameColumn;
    @FXML private TableColumn<Student, Double> ratingColumn;

    private Connection connection;

    @FXML
    public void initialize() {

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

        Properties props = new Properties();
        props.setProperty("user","student");
        props.setProperty("password", "123");
        try {
            connection = DriverManager.getConnection("jdbc:mariadb://localhost:3306/univer", props);
            fillStudentTable(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fillStudentTable(Connection connection) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement("select * from student")) {
            ResultSet resultSet = ps.executeQuery();
            List<Student> students = new ArrayList<>();
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String lastname = resultSet.getString("lastname");
                double rating = resultSet.getDouble("rating");
                students.add(new Student(id, name, lastname, rating));
            }
            studentTable.setItems(FXCollections.observableList(students));
        }
    }

    public void addStudent() {
        String name = nameField.getText();
        String lastName = lastNameField.getText();
        double rating = Double.parseDouble(ratingField.getText());
        writeStudentToDB(name, lastName, rating);
        try {
            fillStudentTable(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void writeStudentToDB(String name, String lastName, double rating) {
        try (PreparedStatement ps = connection.prepareStatement("insert into student (name, lastname, rating) values (?, ?, ?)")){
            ps.setString(1, name);
            ps.setString(2, lastName);
            ps.setDouble(3, rating);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}

//TODO реализовать полный набор CRUD (UD)
