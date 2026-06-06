package personalexpense;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class PersonalExpense extends Application {

    private final ObservableList<Income>  incomeList  = FXCollections.observableArrayList();
    private final ObservableList<Expense> expenseList = FXCollections.observableArrayList();
    private ListView<String> transactionListView;
    private Label totalIncomeLabel;
    private Label totalExpenseLabel;
    private Label balanceLabel;
    private PieChart pieChart;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Personal Expense Tracker");

        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(createTopBar());
        mainLayout.setCenter(createCenterPane());
        mainLayout.setBottom(createBottomBar());

        Scene scene = new Scene(mainLayout, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();

        addSampleData();
        updateDisplay();
    }

    // =============================================
    // TOP BAR
    // =============================================
    private VBox createTopBar() {
        VBox topBar = new VBox(10);
        topBar.setPadding(new Insets(15));
        topBar.setStyle("-fx-background-color: #2c3e50;");

        Label titleLabel = new Label("Personal Expense Tracker");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");

        HBox summaryBox = new HBox(20);
        summaryBox.setPadding(new Insets(10, 0, 0, 0));

        totalIncomeLabel = new Label("Total Income: $0.00");
        totalIncomeLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-size: 18px; -fx-font-weight: bold;");

        totalExpenseLabel = new Label("Total Expense: $0.00");
        totalExpenseLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 18px; -fx-font-weight: bold;");

        balanceLabel = new Label("Balance: $0.00");
        balanceLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");

        summaryBox.getChildren().addAll(totalIncomeLabel, totalExpenseLabel, balanceLabel);
        topBar.getChildren().addAll(titleLabel, summaryBox);
        return topBar;
    }

    // =============================================
    // CENTER PANE
    // =============================================
    private SplitPane createCenterPane() {
        SplitPane splitPane = new SplitPane();
        splitPane.setDividerPositions(0.6);

        VBox leftPane = new VBox(10);
        leftPane.setPadding(new Insets(15));
        Label transactionsLabel = new Label("Recent Transactions");
        transactionsLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        transactionListView = new ListView<>();
        transactionListView.setPrefHeight(500);
        leftPane.getChildren().addAll(transactionsLabel, transactionListView);

        VBox rightPane = new VBox(10);
        rightPane.setPadding(new Insets(15));
        Label chartsLabel = new Label("Expense Breakdown");
        chartsLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        pieChart = new PieChart();
        pieChart.setTitle("Expenses by Category");
        pieChart.setPrefSize(400, 400);
        rightPane.getChildren().addAll(chartsLabel, pieChart);

        splitPane.getItems().addAll(leftPane, rightPane);
        return splitPane;
    }

    // =============================================
    // BOTTOM BAR
    // =============================================
    private HBox createBottomBar() {
        HBox bottomBar = new HBox(15);
        bottomBar.setPadding(new Insets(15));
        bottomBar.setStyle("-fx-background-color: #ecf0f1;");

        Button addIncomeBtn = new Button("Add Income");
        addIncomeBtn.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px;");
        addIncomeBtn.setOnAction(e -> showAddIncomeDialog());

        Button addExpenseBtn = new Button("Add Expense");
        addExpenseBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px;");
        addExpenseBtn.setOnAction(e -> showAddExpenseDialog());

        Button deleteBtn = new Button("Delete Selected");
        deleteBtn.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-size: 14px;");
        deleteBtn.setOnAction(e -> deleteSelectedTransaction());

        Button refreshBtn = new Button("Refresh");
        refreshBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px;");
        refreshBtn.setOnAction(e -> updateDisplay());

        bottomBar.getChildren().addAll(addIncomeBtn, addExpenseBtn, deleteBtn, refreshBtn);
        return bottomBar;
    }

    // =============================================
    // ADD INCOME DIALOG
    // =============================================
    private void showAddIncomeDialog() {
        Dialog<Income> dialog = new Dialog<>();
        dialog.setTitle("Add Income");
        dialog.setHeaderText("Add New Income");

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField descriptionField   = new TextField();
        TextField amountField        = new TextField();
        DatePicker datePicker        = new DatePicker(LocalDate.now());
        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Salary", "Freelance", "Investment", "Gift", "Other");
        categoryBox.setValue("Salary");

        grid.add(new Label("Description:"), 0, 0); grid.add(descriptionField, 1, 0);
        grid.add(new Label("Amount ($):"),  0, 1); grid.add(amountField,      1, 1);
        grid.add(new Label("Date:"),        0, 2); grid.add(datePicker,       1, 2);
        grid.add(new Label("Category:"),    0, 3); grid.add(categoryBox,      1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                try {
                    String description = descriptionField.getText().trim();
                    if (description.isEmpty()) {
                        showAlert("Input Error", "Description cannot be empty!");
                        return null;
                    }
                    double amount = Double.parseDouble(amountField.getText().trim());
                    if (amount <= 0) {
                        showAlert("Input Error", "Amount must be greater than zero!");
                        return null;
                    }
                    LocalDate date = datePicker.getValue();
                    String category = categoryBox.getValue() != null ? categoryBox.getValue() : "Other";
                    return new Income(description, amount, date, category);
                } catch (NumberFormatException e) {
                    showAlert("Input Error", "Please enter a valid number for amount!");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(income -> {
            incomeList.add(income);
            updateDisplay();
        });
    }

    // =============================================
    // ADD EXPENSE DIALOG
    // =============================================
    private void showAddExpenseDialog() {
        Dialog<Expense> dialog = new Dialog<>();
        dialog.setTitle("Add Expense");
        dialog.setHeaderText("Add New Expense");

        ButtonType addButton = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField descriptionField   = new TextField();
        TextField amountField        = new TextField();
        DatePicker datePicker        = new DatePicker(LocalDate.now());
        ComboBox<String> categoryBox = new ComboBox<>();
        categoryBox.getItems().addAll("Food", "Transport", "Shopping", "Entertainment", "Bills", "Healthcare", "Other");
        categoryBox.setValue("Food");

        grid.add(new Label("Description:"), 0, 0); grid.add(descriptionField, 1, 0);
        grid.add(new Label("Amount ($):"),  0, 1); grid.add(amountField,      1, 1);
        grid.add(new Label("Date:"),        0, 2); grid.add(datePicker,       1, 2);
        grid.add(new Label("Category:"),    0, 3); grid.add(categoryBox,      1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButton) {
                try {
                    String description = descriptionField.getText().trim();
                    if (description.isEmpty()) {
                        showAlert("Input Error", "Description cannot be empty!");
                        return null;
                    }
                    double amount = Double.parseDouble(amountField.getText().trim());
                    if (amount <= 0) {
                        showAlert("Input Error", "Amount must be greater than zero!");
                        return null;
                    }
                    LocalDate date = datePicker.getValue();
                    String category = categoryBox.getValue() != null ? categoryBox.getValue() : "Other";
                    return new Expense(description, amount, date, category);
                } catch (NumberFormatException e) {
                    showAlert("Input Error", "Please enter a valid number for amount!");
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(expense -> {
            expenseList.add(expense);
            updateDisplay();
        });
    }

    // =============================================
    // DELETE SELECTED (Bug Fixed)
    // =============================================
    private void deleteSelectedTransaction() {
        String selected = transactionListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select a transaction to delete first!");
            return;
        }

        int spaceIndex = selected.indexOf(" ");
        if (spaceIndex == -1) return;
        String cleanSelected = selected.substring(spaceIndex + 1);

        boolean found = false;
        for (int i = 0; i < incomeList.size(); i++) {
            if (incomeList.get(i).toString().equals(cleanSelected)) {
                incomeList.remove(i);
                found = true;
                break;
            }
        }
        if (!found) {
            for (int i = 0; i < expenseList.size(); i++) {
                if (expenseList.get(i).toString().equals(cleanSelected)) {
                    expenseList.remove(i);
                    break;
                }
            }
        }
        updateDisplay();
    }

    // =============================================
    // UPDATE DISPLAY
    // =============================================
    private void updateDisplay() {
        transactionListView.getItems().clear();

        for (Income income : incomeList) {
            transactionListView.getItems().add("💰 " + income.toString());
        }
        for (Expense expense : expenseList) {
            transactionListView.getItems().add("💸 " + expense.toString());
        }

        double totalIncome = 0;
        for (Income income : incomeList)    totalIncome  += income.getAmount();

        double totalExpense = 0;
        for (Expense expense : expenseList) totalExpense += expense.getAmount();

        double balance = totalIncome - totalExpense;

        totalIncomeLabel.setText(String.format("Total Income: $%.2f", totalIncome));
        totalExpenseLabel.setText(String.format("Total Expense: $%.2f", totalExpense));
        balanceLabel.setText(String.format("Balance: $%.2f", balance));

        if (balance >= 0) {
            balanceLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 18px; -fx-font-weight: bold;");
        } else {
            balanceLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 18px; -fx-font-weight: bold;");
        }

        updatePieChart();
    }

    // =============================================
    // UPDATE PIE CHART
    // =============================================
    private void updatePieChart() {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        Map<String, Double> categoryTotals = new HashMap<>();

        for (Expense expense : expenseList) {
            String category = expense.getCategory();
            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0.0) + expense.getAmount());
        }

        for (Map.Entry<String, Double> entry : categoryTotals.entrySet()) {
            pieChartData.add(new PieChart.Data(
                    entry.getKey() + " ($" + String.format("%.2f", entry.getValue()) + ")",
                    entry.getValue()
            ));
        }

        pieChart.setData(pieChartData);
    }

    // =============================================
    // SAMPLE DATA
    // =============================================
    private void addSampleData() {
        incomeList.add(new Income("Monthly Salary",    5000, LocalDate.now().minusDays(5),  "Salary"));
        incomeList.add(new Income("Freelance Project", 1000, LocalDate.now().minusDays(10), "Freelance"));

        expenseList.add(new Expense("Groceries",        450, LocalDate.now().minusDays(2), "Food"));
        expenseList.add(new Expense("Restaurant",        75, LocalDate.now().minusDays(4), "Food"));
        expenseList.add(new Expense("Uber rides",       120, LocalDate.now().minusDays(3), "Transport"));
        expenseList.add(new Expense("Netflix",           15, LocalDate.now().minusDays(1), "Entertainment"));
        expenseList.add(new Expense("Electricity Bill", 180, LocalDate.now().minusDays(7), "Bills"));
    }

    // =============================================
    // ALERT HELPER
    // =============================================
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // =============================================
    // MAIN
    // =============================================
    public static void main(String[] args) {
        launch(args);
    }
}