import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.io.File;
import java.util.Scanner;
import java.util.Optional;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BusinessApp extends Application {

    private BusinessManager manager = new BusinessManager();
    private VBox listContainer = new VBox(15); // Stacked vertically
    private ChoiceBox<String> categoryPicker = new ChoiceBox<>();
    private TextField searchField = new TextField();

    @Override
    public void start(Stage stage) {
        // Bot check before the program launches
        if (!runVerification()) System.exit(0);

        loadDataFromJson("./src/data.json");

        stage.setTitle("RateTheA");
        stage.setWidth(950);
        stage.setHeight(850);

        // CSS STYLING
        String styleCSS = """
            .root { -fx-background-color: #fcfcfc; }
            .scroll-pane { -fx-background-color: transparent; -fx-background-insets: 0; }
            .scroll-pane > .viewport { -fx-background-color: transparent; }
            .button { -fx-cursor: hand; -fx-background-radius: 8; -fx-font-weight: bold; }
            .search-bar { -fx-background-color: white; -fx-padding: 20; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.05), 10, 0, 0, 5); }
            """;

        // HEADER
        VBox header = new VBox(5);
        header.setStyle("-fx-background-color: #8497c7; -fx-padding: 30;");
        Label title = new Label("RateTheA");
        title.setStyle("-fx-text-fill: white; -fx-font-size: 32; -fx-font-weight: bold;");
        Label subtitle = new Label("Rate Arlington, VA!");
        subtitle.setStyle("-fx-text-fill: #ffffff;");
        header.getChildren().addAll(title, subtitle);

        // SEARCH BAR
        searchField.setPromptText("Try searching 'Pizza' or 'Tech'...");
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-background-radius: 20; -fx-padding: 10 20; -fx-background-color: #e8edf1;");
        
        refreshCategoryPicker();

        Button applyBtn = new Button("Apply Filters");
        applyBtn.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-padding: 10 25;");
        applyBtn.setOnAction(e -> runCombinedFilter());

        Button resetBtn = new Button("Reset");
        resetBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #64748b;");
        resetBtn.setOnAction(e -> {
            searchField.clear();
            categoryPicker.setValue("All Categories");
            updateListView(manager.getTopRated());
        });

        HBox filterBar = new HBox(20, searchField, categoryPicker, applyBtn, resetBtn);
        filterBar.getStyleClass().add("search-bar");
        filterBar.setAlignment(Pos.CENTER);

        // LIST CONTAINER SETUP
        listContainer.setPadding(new Insets(20, 50, 20, 50));
        listContainer.setAlignment(Pos.TOP_CENTER);

        ScrollPane scrollPane = new ScrollPane(listContainer);
        scrollPane.setFitToWidth(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // STATUS BAR
        HBox statusBar = new HBox();
        statusBar.setStyle("-fx-background-color: #f8fafc; -fx-padding: 12 30; -fx-border-color: #e2e8f0; -fx-border-width: 1 0 0 0;");
        Label footerTxt = new Label("Systems Active \u2022 " + manager.getTopRated().size() + " Entries Loaded");
        footerTxt.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11;");
        statusBar.getChildren().add(footerTxt);

        VBox root = new VBox(0, header, filterBar, scrollPane, statusBar);
        Scene scene = new Scene(root);
        scene.getStylesheets().add("data:text/css," + styleCSS);
        
        updateListView(manager.getTopRated());
        stage.setScene(scene);
        stage.show();
    }

    private HBox createBusinessBlob(Business b) {
        HBox blob = new HBox(25);
        blob.setPadding(new Insets(20));
        blob.setAlignment(Pos.CENTER_LEFT);
        blob.setMaxWidth(850);
        blob.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-border-color: #f1f5f9; -fx-border-radius: 20;");
        
        // Drop Shadow Effect
        DropShadow ds = new DropShadow();
        ds.setRadius(12);
        ds.setOffsetY(4);
        ds.setColor(Color.rgb(0, 0, 0, 0.1));
        blob.setEffect(ds);

        // Icon for Locations
        StackPane iconPane = new StackPane();
        Circle circle = new Circle(20, Color.web("#a4c1de"));
        iconPane.getChildren().addAll(circle);

        // Info Section (Name & Category)
        VBox info = new VBox(5);
        Label name = new Label(b.getName());
        name.setStyle("-fx-font-size: 18; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        Label cat = new Label(b.getCategory());
        cat.setStyle("-fx-text-fill: #64748b; -fx-font-size: 13;");
        info.getChildren().addAll(name, cat);

        // Rating Section
        HBox ratingBox = new HBox(5);
        ratingBox.setAlignment(Pos.CENTER);
        Label star = new Label("\u2605");
        star.setStyle("-fx-text-fill: #f59e0b; -fx-font-size: 22;");
        Label score = new Label(String.valueOf(b.getRating()));
        score.setStyle("-fx-font-weight: bold; -fx-font-size: 18; -fx-text-fill: #1e293b;");
        ratingBox.getChildren().addAll(star, score);

        // Spacer to push button to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Rate Button
        Button rateBtn = new Button("Add Rating");
        rateBtn.setStyle("-fx-background-color: #f8fafc; -fx-border-color: #e2e8f0; -fx-border-radius: 8; -fx-padding: 8 15;");
        rateBtn.setOnAction(e -> showRatingDialog(b));

        blob.getChildren().addAll(iconPane, info, spacer, ratingBox, rateBtn);
        
        // Hover effect
        blob.setOnMouseEntered(e -> blob.setStyle("-fx-background-color: #f8fafc; -fx-background-radius: 20; -fx-border-color: #3b82f6; -fx-border-radius: 20;"));
        blob.setOnMouseExited(e -> blob.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-border-color: #f1f5f9; -fx-border-radius: 20;"));

        return blob;
    }

    // Updates the list view to ensure all businesses are included
    private void updateListView(List<Business> list) {
        listContainer.getChildren().clear();
        for (Business b : list) {
            listContainer.getChildren().add(createBusinessBlob(b));
        }
    }

    // Filters the list of businesses based on search and category filter
    private void runCombinedFilter() {
        String query = searchField.getText().toLowerCase().trim();
        String cat = categoryPicker.getValue();
        List<Business> filtered = manager.getTopRated().stream()
            .filter(b -> b.getName().toLowerCase().contains(query))
            .filter(b -> cat.equals("All Categories") || b.getCategory().equalsIgnoreCase(cat))
            .collect(Collectors.toList());
        updateListView(filtered);
    }

    // Goes through the JSON file to see if there are new categories not listed on the dropdown
    private void refreshCategoryPicker() {
        Set<String> categories = manager.getTopRated().stream()
                                    .map(Business::getCategory)
                                    .collect(Collectors.toSet());
        ObservableList<String> options = FXCollections.observableArrayList("All Categories");
        options.addAll(categories);
        categoryPicker.setItems(options);
        categoryPicker.setValue("All Categories");
        categoryPicker.setStyle("-fx-background-radius: 20; -fx-padding: 5 15;");
    }

    // Displays a dialog to rate a business (appears when you click the "Rate" button)
    private void showRatingDialog(Business b) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Review");
        dialog.setHeaderText("Rating for " + b.getName());
        VBox vb = new VBox(15);
        vb.setAlignment(Pos.CENTER);
        ToggleGroup tg = new ToggleGroup();
        HBox hb = new HBox(10);
        hb.setAlignment(Pos.CENTER);
        for (int i = 1; i <= 5; i++) {
            ToggleButton tb = new ToggleButton(String.valueOf(i));
            tb.setToggleGroup(tg);
            tb.setUserData(i);
            tb.setStyle("-fx-min-width: 40;");
            hb.getChildren().add(tb);
        }
        vb.getChildren().addAll(new Label("Select Stars:"), hb);
        dialog.getDialogPane().setContent(vb);
        ButtonType bt = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(bt, ButtonType.CANCEL);
        dialog.setResultConverter(res -> res == bt ? (Integer)tg.getSelectedToggle().getUserData() : null);

        dialog.showAndWait().ifPresent(val -> {
            b.addNewRating(val);
            updateListView(manager.getTopRated());
            saveDataToJson("./src/data.json");
        });
    }

    // Parses JSON manually with a string by reading through the file and splitting it by "objects" (where the "}" substrings are)
    private void loadDataFromJson(String filename) {
        try (Scanner sc = new Scanner(new File(filename))) {
            StringBuilder sb = new StringBuilder();
            while (sc.hasNextLine()) 
                sb.append(sc.nextLine());
            String c = sb.toString().trim();
            if (!c.contains("[")) 
                return;
            c = c.substring(c.indexOf("[")+1, c.lastIndexOf("]"));
            String[] objects = c.split("\\},");
            for (String obj : objects) {
                String clean = obj.replace("{", "").replace("}", "").trim();
                manager.addBusiness(new Business(findV(clean, "name"), findV(clean, "category"), Double.parseDouble(findV(clean, "rating"))));
            }
        } catch (Exception e) {}
    }

    // String obj - equal to the single object of a business in the JSON file
    // Used to find a certain aspect of a business: i.e. to find its category, we can make key equal to "category"
    private String findV(String obj, String key) {
        // Splits the obj by commas to separate the name, category, and rating
        for (String pair : obj.split(",")) {
            if (pair.contains("\"" + key + "\"")) 
                // If we find what we're looking for, we return it without commas or spaces
                return pair.split(":")[1].replace("\"", "").trim();
        }
        return "";
    }

    // Saves the current list of businesses to the data.json file
    private void saveDataToJson(String filename) {
        try (java.io.PrintWriter writer = new java.io.PrintWriter(new File(filename))) {
            writer.println("[");
            List<Business> list = manager.getTopRated();
            for (int i = 0; i < list.size(); i++) {
                Business b = list.get(i);
                writer.print(String.format("  {\"name\":\"%s\",\"category\":\"%s\",\"rating\":%.1f}%s\n", 
                    b.getName(), b.getCategory(), b.getRating(), (i < list.size() - 1 ? "," : "")));
            }
            writer.println("]");
        } catch (Exception e) {}
    }

    // Bot Verification: Picks a number between 1 and 10
    private boolean runVerification() {
        int n1 = (int)(Math.random() * 10) + 1, n2 = (int)(Math.random() * 10) + 1;
        TextInputDialog tid = new TextInputDialog();
        tid.setTitle("Access Check");
        tid.setHeaderText("Verify you are human: " + n1 + " + " + n2);
        Optional<String> r = tid.showAndWait();
        return r.isPresent() && r.get().equals(String.valueOf(n1+n2));
    }

    public static void main(String[] args) { launch(args); }
}