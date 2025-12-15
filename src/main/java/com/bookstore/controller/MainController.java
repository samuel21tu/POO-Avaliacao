package com.bookstore.controller;

import com.bookstore.Main;

import com.bookstore.dao.AuthorDAO;
import com.bookstore.dao.BookDAO;
import com.bookstore.model.Author;
import com.bookstore.model.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class MainController {

    @FXML
    private TextField titleField;
    @FXML
    private TextField priceField;
    @FXML
    private ComboBox<Author> authorComboBox;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<String> searchTypeCombo;
    @FXML
    private TextField maxPriceField;

    @FXML
    private TableView<Book> bookTable;
    @FXML
    private TableColumn<Book, Integer> colId;
    @FXML
    private TableColumn<Book, String> colTitle;
    @FXML
    private TableColumn<Book, Double> colPrice;
    @FXML
    private TableColumn<Book, Author> colAuthor;

    private BookDAO bookDAO;
    private AuthorDAO authorDAO;
    private ObservableList<Book> bookList;
    private ObservableList<Author> authorList;

    // Track selected book for updates
    private Book selectedBook;

    @FXML
    public void initialize() {
        bookDAO = new BookDAO();
        authorDAO = new AuthorDAO();

        // Initialize Table Columns
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        // For Author, we might need a custom cell factory or just toString behavior in
        // Book
        // But Book has an Author object. PropertyValueFactory might call toString on
        // it.
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));

        // Load data
        loadAuthors();
        loadBooks();

        // Initialize Search Type Combo
        searchTypeCombo.setItems(FXCollections.observableArrayList("Título", "Nome do Autor"));
        searchTypeCombo.getSelectionModel().selectFirst();

        // Search listener - keeping dynamic search for text field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            handleFilter();
        });

        // Listen to Max Price changes too
        maxPriceField.textProperty().addListener((obs, oldV, newV) -> handleFilter());

        // Table selection listener
        bookTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) ->

        {
            if (newSelection != null) {
                selectedBook = newSelection;
                populateForm(newSelection);
            }
        });
    }

    private void loadAuthors() {
        authorList = FXCollections.observableArrayList(authorDAO.findAll());
        authorComboBox.setItems(authorList);
    }

    private void loadBooks() {
        bookList = FXCollections.observableArrayList(bookDAO.findAll());
        bookTable.setItems(bookList);
    }

    @FXML
    private void handleFilter() {
        String query = searchField.getText().toLowerCase();
        String searchType = searchTypeCombo.getValue();
        String priceText = maxPriceField.getText();

        ObservableList<Book> filtered = FXCollections.observableArrayList();

        for (Book b : bookList) {
            boolean matchesSearch = true;
            boolean matchesPrice = true;

            // 1. Text Filter (Title or Author)
            if (query != null && !query.isEmpty()) {
                if ("Título".equals(searchType)) {
                    matchesSearch = b.getTitle().toLowerCase().contains(query);
                } else if ("Nome do Autor".equals(searchType)) {
                    matchesSearch = b.getAuthor().getName().toLowerCase().contains(query);
                }
            }

            // 2. Price Filter
            if (priceText != null && !priceText.isEmpty()) {
                try {
                    double maxPrice = Double.parseDouble(priceText);
                    if (b.getPrice() > maxPrice) {
                        matchesPrice = false;
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid price input (treat as no filter)
                }
            }

            if (matchesSearch && matchesPrice) {
                filtered.add(b);
            }
        }
        bookTable.setItems(filtered);
    }

    @FXML
    private void handleLogout() throws java.io.IOException {
        Main.setRoot("welcome");
    }

    private void populateForm(Book book) {
        titleField.setText(book.getTitle());
        priceField.setText(String.valueOf(book.getPrice()));

        // Find matching author in combobox
        for (Author a : authorComboBox.getItems()) {
            if (a.getId() == book.getAuthor().getId()) {
                authorComboBox.getSelectionModel().select(a);
                break;
            }
        }
    }

    @FXML
    private void handleAdd() {
        if (validateInput()) {
            Book newBook = new Book(
                    titleField.getText(),
                    Double.parseDouble(priceField.getText()),
                    authorComboBox.getValue());
            bookDAO.create(newBook);
            clearForm();
            loadBooks(); // refresh
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedBook != null && validateInput()) {
            selectedBook.setTitle(titleField.getText());
            selectedBook.setPrice(Double.parseDouble(priceField.getText()));
            selectedBook.setAuthor(authorComboBox.getValue());

            bookDAO.update(selectedBook);
            clearForm();
            loadBooks(); // refresh
        } else {
            showAlert("Nenhuma Seleção", "Por favor, selecione um livro para atualizar.");
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedBook != null) {
            bookDAO.delete(selectedBook.getId());
            clearForm();
            loadBooks();
        } else {
            showAlert("Nenhuma Seleção", "Por favor, selecione um livro para excluir.");
        }
    }

    @FXML
    private void handleClear() {
        clearForm();
    }

    @FXML
    private void handleAddAuthor() {
        // Simple dialog to add author quickly for testing
        TextInputDialog dialog = new TextInputDialog("Nome");
        dialog.setTitle("Adicionar Autor");
        dialog.setHeaderText("Adicionar novo Autor");
        dialog.setContentText("Nome:");

        dialog.showAndWait().ifPresent(name -> {
            Author newAuthor = new Author(name, "Unknown");
            authorDAO.create(newAuthor);
            loadAuthors();
        });
    }

    @FXML
    private void handleDeleteAuthor() {
        Author selectedAuthor = authorComboBox.getValue();
        if (selectedAuthor != null) {
            boolean success = authorDAO.delete(selectedAuthor.getId());
            if (success) {
                loadAuthors();
                authorComboBox.getSelectionModel().clearSelection();
                showAlert("Sucesso", "Autor excluído com sucesso.");
            } else {
                showAlert("Erro ao Excluir", "Não foi possível excluir o autor. Pode haver livros associados.");
            }
        } else {
            showAlert("Nenhuma Seleção", "Por favor, selecione um autor para excluir.");
        }
    }

    private void clearForm() {
        titleField.clear();
        priceField.clear();
        authorComboBox.getSelectionModel().clearSelection();
        selectedBook = null;
        bookTable.getSelectionModel().clearSelection();
    }

    private boolean validateInput() {
        if (titleField.getText().isEmpty() ||
                priceField.getText().isEmpty() ||
                authorComboBox.getValue() == null) {
            showAlert("Erro de Validação", "Por favor, preencha todos os campos obrigatórios (Título, Preço, Autor).");
            return false;
        }
        try {
            Double.parseDouble(priceField.getText());
        } catch (NumberFormatException e) {
            showAlert("Erro de Validação", "O preço deve ser um número válido.");
            return false;
        }
        return true;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
