package barcodeReader;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.util.Pair;
 
public class BarcodeReader extends Application {
	Connection connection = null;
	String databaseLoc = "jdbc:sqlite:..//sorting.db";
	File file = new File("..\\cit.png");
    Image citImage = new Image(file.toURI().toString());
    File file2 = new File("..\\barcode_scanner.png");
    Image barcodeImage = new Image(file2.toURI().toString());
	
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
    	connection = connectDatabase();
        createTable();
        
        primaryStage.setWidth(300);
        primaryStage.setHeight(350);
        
        GridPane grid = new GridPane();
    	grid.setAlignment(Pos.TOP_CENTER);
    	grid.setHgap(10);
    	grid.setVgap(10);
    	grid.setPadding(new Insets(25, 25, 25, 25));
    	
    	primaryStage.getIcons().add(barcodeImage);
    	ImageView imageView = new ImageView(citImage);
    	imageView.setFitHeight(175);
    	imageView.setFitWidth(225);
    	grid.add(imageView, 0, 0, 3, 1);
    	
    	Text scenetitle = new Text("       Barcode Reader");
    	scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
    	grid.add(scenetitle, 0, 2, 2, 1);

    	Label userName = new Label("Enter Barcode:");
    	grid.add(userName, 0, 4);

    	TextField input = new TextField();
    	grid.add(input, 1, 4);
    	
    	/*Button btn = new Button("Hello!");
    	grid.add(btn, 1, 2);*/

 
    	Scene scene = new Scene(grid, 300, 275);
    	primaryStage.setScene(scene);
    	
    	input.setOnKeyPressed(new EventHandler<KeyEvent>()
        {
            @Override
            public void handle(KeyEvent ke)
            {
                if (ke.getCode().equals(KeyCode.ENTER))
                {
                	String newValue = input.getText().toUpperCase();
                	System.out.println("TextField Text Changed (newValue: " + newValue + ")");
        			String binString = null, sectionString = null;
        			binString = getBin(newValue);
        			if(binString == null){
        				input.clear();
        				return;
        			}
        			sectionString = getSection(newValue);
        			
        			GridPane gridPopup = new GridPane();
        	    	gridPopup.setAlignment(Pos.CENTER);
        	    	gridPopup.setHgap(10);
        	    	gridPopup.setVgap(10);
        	    	gridPopup.setPadding(new Insets(25, 25, 25, 25));
        			
        	    	Text location = new Text("Location");
        	    	location.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        	    	location.setUnderline(true);
        	    	gridPopup.add(location, 1, 1);
        	    	
        	    	Label bin = new Label("Bin: " + binString);
        	    	bin.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        	    	gridPopup.add(bin, 1, 3);
        	    	
        	    	Label section = new Label("Section: " + sectionString);
        	    	section.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        	    	gridPopup.add(section, 1, 5);
        	    	
        	    	Stage phoneDetailStage = new Stage();
                	phoneDetailStage.setScene(new Scene(gridPopup, 250, 250));
                	phoneDetailStage.getIcons().add(barcodeImage);
                	phoneDetailStage.show();
                    Timeline timeline = new Timeline();
                    timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(1),
                        new EventHandler<ActionEvent>() {

                            @Override
                            public void handle(ActionEvent event) {
                            	phoneDetailStage.hide();
                            }
                        }));
                    timeline.play();
                    Platform.runLater(() -> { 
                        input.clear(); 
                    }); 
                }
            }
        });
    	
        /*btn.setOnAction(new EventHandler<ActionEvent>() {
 
            @Override
            public void handle(ActionEvent event) {
                connection = connectDatabase();
                createTable();
                
                Stage phoneDetailStage = new Stage();
            	phoneDetailStage.setScene(new Scene(new Label("Hello"), 250, 250));
            	phoneDetailStage.show();
                Timeline timeline = new Timeline();
                timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(2),
                    new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent event) {
                        	phoneDetailStage.hide();
                        }
                    }));
                timeline.play();
            }
        });*/
        
        primaryStage.show();
    }
    
    public Connection connectDatabase(){
    	Connection c = null;
        try {
          Class.forName("org.sqlite.JDBC");
          c = DriverManager.getConnection(databaseLoc);
        } catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() );
          System.exit(0);
        }
        System.out.println("Opened database successfully1");
        return c;
    }
    
    public void createTable(){
    	Connection c = null;
	    Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection(databaseLoc);
	      System.out.println("Opened database successfully2");

	      stmt = c.createStatement();
	      String sql;
	      /*sql = "DROP TABLE Phones;";
	      stmt.execute(sql);
	      System.out.println("Table Deleted");*/
	      
	      sql = "CREATE TABLE Phones " +
	                   "(Serial TEXT PRIMARY KEY     NOT NULL," +
	                   " Bin           TEXT    NOT NULL, " + 
	                   " Section        TEXT     NOT NULL);"; 
	      stmt.executeUpdate(sql);
	      stmt.close();
	      c.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	    }
	    System.out.println("Table created successfully");
	 }
    
    private String getBin(String serial){
    	Connection c = null;
        Statement stmt = null;
        String bin = null;
        int count = 0;
        try {
          Class.forName("org.sqlite.JDBC");
          c = DriverManager.getConnection(databaseLoc);
          c.setAutoCommit(false);
          System.out.println("Opened database successfully");

          stmt = c.createStatement();
          ResultSet rs = stmt.executeQuery( "SELECT Bin FROM Phones WHERE Serial = '" + serial + "';" );
          while ( rs.next() ) {
             bin = rs.getString("Bin");
             
             System.out.println( "Bin = " + bin );
             count++;
          }
          rs.close();
          stmt.close();
          c.close();
        } catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        System.out.println("Operation done successfully");
        if(count == 0){
        	try{
        		bin = addPhone(serial).getKey();
        	} catch(Exception e){
        		System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        	}
        }
        return bin;
    }
    
    private String getSection(String serial){
    	Connection c = null;
        Statement stmt = null;
        String section = null;
        int count = 0;
        try {
          Class.forName("org.sqlite.JDBC");
          c = DriverManager.getConnection(databaseLoc);
          c.setAutoCommit(false);
          System.out.println("Opened database successfully");

          stmt = c.createStatement();
          ResultSet rs = stmt.executeQuery( "SELECT Section FROM Phones WHERE Serial = '" + serial + "';" );
          while ( rs.next() ) {
             section = rs.getString("Section");
             
             System.out.println( "Section = " + section );
             count++;
          }
          rs.close();
          stmt.close();
          c.close();
        } catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        System.out.println("Operation done successfully");
        if(count == 0){
        	section = addPhone(serial).getValue();
        }
        return section;
    }

	private Pair<String, String> addPhone(String serial) {
		Pair<String, String> output = null;
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Phone Not Found");
		dialog.setHeaderText("Phone Not Found: " + serial);
		
		Stage dialogStage = (Stage) dialog.getDialogPane().getScene().getWindow();
		dialogStage.getIcons().add(barcodeImage);

		// Set the icon (must be included in the project).
		/*File file = new File("barcode_scanner.png");
        Image image = new Image(file.toURI().toString());
        ImageView myImage = new ImageView();
        myImage.setImage(image);
		dialog.setGraphic(myImage);*/

		// Set the button types.
		ButtonType submitButtonType = new ButtonType("Submit", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField bin = new TextField();
		bin.setPromptText("Enter a bin");
		TextField section = new TextField();
		section.setPromptText("Enter a section");

		grid.add(new Label("Bin:"), 0, 0);
		grid.add(bin, 1, 0);
		grid.add(new Label("Section:"), 0, 1);
		grid.add(section, 1, 1);

		// Enable/Disable login button depending on whether a bin was entered.
		Node submitButton = dialog.getDialogPane().lookupButton(submitButtonType);
		submitButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		bin.textProperty().addListener((observable, oldValue, newValue) -> {
			submitButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		// Request focus on the bin field by default.
		Platform.runLater(() -> bin.requestFocus());

		// Convert the result to a bin-section-pair when the login button is clicked.
		dialog.setResultConverter(dialogButton -> {
		    if (dialogButton == submitButtonType) {
		        return new Pair<>(bin.getText(), section.getText());
		    }
		    return null;
		});

		Optional<Pair<String, String>> result = dialog.showAndWait();
		try{
			output = result.get();
		} catch(Exception e){
			return output;
		}

		result.ifPresent(binSection -> {
		    System.out.println("Bin=" + binSection.getKey() + ", Section=" + binSection.getValue());
		});
		
		addEntry(serial, output);
		return output;
	}

	private void addEntry(String serial, Pair<String, String> output) {
		Connection c = null;
	    Statement stmt = null;
	    try {
	      Class.forName("org.sqlite.JDBC");
	      c = DriverManager.getConnection(databaseLoc);
	      c.setAutoCommit(false);
	      System.out.println("Opened database successfully");

	      stmt = c.createStatement();
	      String sql = "INSERT INTO Phones (Serial,Bin,Section) " +
	                   "VALUES ('" + serial + "','" +  output.getKey() + "','" + output.getValue() + "');"; 
	      System.out.println(sql);
	      stmt.executeUpdate(sql);

	      stmt.close();
	      c.commit();
	      c.close();
	    } catch ( Exception e ) {
	      System.err.println( e.getClass().getName() + ": " + e.getMessage() );
	      System.exit(0);
	    }
	    System.out.println("Record created successfully");
		
	}
}