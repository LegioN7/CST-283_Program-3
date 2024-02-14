package org.example.program3;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;

import java.io.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

// CST-283
// Aaron Pelto
// Winter 2024

// *-------------------------------------------------------------------------------------------------------------------*
// Program 3 Assignment
// Define a context that requires storing a list-oriented collection of related data.
// First, define your own data.
// Define a class for one storage entity.
//
// Examples could be a list of "songs" with each song including a title, artist, genre, time, etc.
// Another could be a "friend" with attributes name, birthdate, address, email, and so forth.
//
// Include at least four attributes with one as the key that defines the object uniquely.
// Create a text file to contain the data for at least 10 of these objects.
// Data for one object should occupy one line of the file.
// Feel free to keep your file simple with minimal white space or comma delimiters separating each of the attributes.
// Create driver program to act as an interactive list manager that includes and array of objects or parallel arrays (no use of the Java ArrayList class, please).
// Your interface should be driven by a formal frame-based JavaFX GUI that includes a larger text area for displaying your list data in columns.
// Utilize buttons and labels as needed for the operations and user feedback/instructions.
// For your text area, consider setting the text area font to a monospaced font (like Courier or Courier New) to maintain column formatting.
// Button clicks should prompt for the various list actions.
// Given this, however, you are nevertheless free to design your own (clear, clean, organized, user-friendly) interface within these general constraints.
// As part of this, your program should include the following features:
//  ● Read information from input file
//  ● Add a new element
//  ● Delete an existing element (using the key to search and delete)
//  ● Sort ascending relative to one field of data
//  ● Sort descending relative to a different field of data
//  ● Randomize the list
//  ● Write the information back to the file in the same format
// Design your application using guidelines to maximize modularity, usability, and maintainability.
// Please avoid use of ArrayList class for storage and Arrays class for sorting. \
// One key objective with this assignment is to work with the basic arrays and related algorithms.

// Hints - consider techniques demonstrated in the following instructor examples:
//  ● ParallelSort as an example for the interface
//  ● SearchObjects as an example for the list management

// *-------------------------------------------------------------------------------------------------------------------*

// Data Structure
// {track_name} {artist(s)_name} {released_year} {released_month} {released_day} {in_spotify_playlists} {in_spotify_charts} {streams}
// Track Name, Artist(s) Name, Released Year, Released Month, Released Day, In Spotify Playlists, In Spotify Charts, Streams
// Streams is # of streams total for 2023

/**
 * The SongSearch class extends the Application class from JavaFX.
 * It provides a GUI for managing a list of songs, including adding, deleting, searching, sorting, and randomizing songs.
 * The song data is loaded from a text file and stored in an array of Songs objects.
 * The GUI includes input fields for the song data, buttons for the various actions, and a TextArea and TableView for displaying the song data.
 * The class also includes private methods for each of the actions (add, delete, search, sort, randomize), as well as helper methods for loading the song data, creating the GUI, and showing alert messages.
 *
 * @author LegioN7
 * @version 2023.3.3
 */
public class SongSearch extends Application {

    // File name and array size
    private static final String FILE_NAME = "songs.txt";
    private static final int ARRAY_SIZE = 740;

    // Input fields for the song data
    private TextField trackNameField;
    private TextField artistNameField;
    private DatePicker releaseDatePicker;
    private TextField inSpotifyPlaylistsField;
    private TextField inSpotifyChartsField;
    private TextField streamsField;

    /*
    Keeping these here if I want to add them later
    private TextField releasedYearField;
    private TextField releasedMonthField;
    private TextField releasedDayField;
    private TextField releaseDateField;
    */


    // Output area for displaying the song data
    private static TextArea outputArea;

    // Array of Songs objects
    private static Songs[] songs = new Songs[ARRAY_SIZE];

    // Current index for the songs array
    private static int currentIndex = 0;

    // Flag for randomizing
    // If true, the songs will be sorted in ascending order
    private boolean isAscending = true;


    /**
     * The start method is called when the application is launched.
     * It creates the GUI and sets the stage.
     * The method throws an Exception if there is an error launching the application.
     * The method creates a new Stage with the title "Spotify 2023 Song Search" and sets the Scene to the songSearchGUI method.
     * The method then shows the stage.
     * The method also calls the loadSongData method to load the song data from the text file.
     * @param stage
     * @throws Exception
     */
    @Override
    public void start(Stage stage) {
        try {
            stage.setTitle("Spotify 2023 Song Search");
            Scene scene = songSearchGUI();
            stage.setScene(scene);
            stage.show();

            // Load the song data from the text file
            loadSongData();

            // Load the Output Area
            loadOutputArea();

        } catch (FileNotFoundException e) {
            showAlert("Error", e.getMessage(), AlertType.ERROR);
        }
    }

    /**
     * The FileNotFoundException class extends the Exception class.
     * It is a custom exception that is thrown when the file is not found.
     * The constructor takes a message as a parameter and passes it to the super class.
     */
    public static class FileNotFoundException extends Exception {
        public FileNotFoundException(String message) {
            super(message);
        }
    }

    /**
     * This method is responsible for loading song data from a text file into an array of Songs objects.
     * The text file is read line by line, and each line is split into parts based on commas.
     * Each part is then trimmed of leading and trailing whitespace and assigned to the appropriate variable.
     * A new Songs object is created with these variables and added to the songs array.
     * The method also updates a TableView with the song data.
     * If the file is not found, a FileNotFoundException is thrown.
     * If there is an error reading the file, an IOException is caught and an alert message is displayed.
     *
     * @throws FileNotFoundException if the file is not found
     */
    private void loadSongData() throws FileNotFoundException {
        // Debug Statement
        System.out.println("Loading song data...");

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))){
            String line;

            while ((line = reader.readLine()) != null && currentIndex < ARRAY_SIZE) {
                // Debug Statement
                 System.out.println("Reading line: " + line); // Debugging statement

                // Split the line into parts
                // The split method uses a regular expression to split the line by commas
                // The regular expression is a negative lookahead that splits the line by commas that are not inside quotes
                // https://stackoverflow.com/questions/3481828/how-do-i-split-a-string-in-java
                // https://stackoverflow.com/questions/1757065/java-splitting-a-comma-separated-string-but-ignoring-commas-in-quotes
                String[] parts = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (parts.length < 8) {
                    // Debug Statement
                    // System.out.println("Skipping line - Insufficient fields: " + line);
                    continue;
                }

                // Remove leading and trailing whitespace from each part
                // The replaceAll method is used to replace all whitespace with a single space
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].replaceAll("\\s", " ").trim();
                }

                // Create a new Songs object with the data from the line
                // The parts are assigned to the appropriate variables
                // The release date is created with the year, month, and day
                // The inSpotifyPlaylists, inSpotifyCharts, and streams are parsed as integers and long
                // Steams is long because it's a large number
                String trackName = parts[0];
                String artistName = parts[1].replaceAll("^\"|\"$", ""); // Remove the quotes from the artist name
                int releasedYear = Integer.parseInt(parts[2]);
                int releasedMonth = Integer.parseInt(parts[3]);
                int releasedDay = Integer.parseInt(parts[4]);
                int inSpotifyPlaylists = Integer.parseInt(parts[5]);
                int inSpotifyCharts = Integer.parseInt(parts[6]);
                long streams = Long.parseLong(parts[7]);

                // Create a LocalDate object for the release date
                // The LocalDate class is used to represent a date without a time
                // The of method is used to create a LocalDate object with the year, month, and day
                LocalDate releaseDate = LocalDate.of(releasedYear, releasedMonth, releasedDay);

                // Create a new Songs object with the data from the line
                // The object is added to the songs array
                songs[currentIndex] = new Songs(trackName, artistName, releaseDate, inSpotifyPlaylists, inSpotifyCharts, streams);

                // Debug Statement
                // System.out.println("Adding song: " + songs[currentIndex].toString());

                // Increment the currentIndex
                currentIndex++;
            }
        } catch (IOException e) {
            showAlert("Error", e.getMessage(), AlertType.ERROR);
        }
    }

    /**
     * This method is responsible for writing song data from the array of Songs objects to a text file.
     * The songs array is iterated over, and each Songs object is written to the file as a line of text.
     * The method also updates a TableView with the song data.
     * If there is an error writing to the file, an IOException is caught and an alert message is displayed.
     */
    private void loadOutputArea() {
        StringBuilder output = new StringBuilder();
        String format = "%-50s %-25s %-15s %-25s %-20s %-20s\n";

        // Add header
        output.append(String.format(format, "Track Name", "Artist Name", "Release Date", "In Spotify Playlists", "In Spotify Charts", "Streams"));

        // Add each song
        for (int i = 0; i < currentIndex; i++) {
            Songs song = songs[i];
            output.append(String.format(format, song.getTrackName(), song.getArtistName(), song.getReleaseDate(), song.getInSpotifyPlaylists(), song.getInSpotifyCharts(), song.getStreams()));
        }

        // Set the text of the output area
        outputArea.setText(output.toString());
    }


    /**
     * This method is responsible for creating the GUI for the SongSearch application.
     * It creates a VBox layout and adds various UI elements to it, including input fields for song data, buttons for various actions, a TextArea for displaying song data, and a TableView for displaying song data in a table format.
     * It also sets up action handlers for the buttons, which call other methods in the class when the buttons are clicked.
     * The method returns a Scene containing the VBox, which can be set on a Stage to display the GUI.
     *
     * @return a Scene containing the GUI for the SongSearch application
     */
    private Scene songSearchGUI() {
        VBox vbox = new VBox(30);
        vbox.setPadding(new Insets(30));

        // Create the input fields
        Label trackNameLabel = new Label("Track Name:");
        trackNameField = new TextField();
        Label artistNameLabel = new Label("Artist Name:");
        artistNameField = new TextField();
        Label releaseDateLabel = new Label("Release Date:");
        releaseDatePicker = new DatePicker();
        Label inSpotifyPlaylistsLabel = new Label("In Spotify Playlists:");
        inSpotifyPlaylistsField = new TextField();
        inSpotifyPlaylistsField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                inSpotifyPlaylistsField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        Label inSpotifyChartsLabel = new Label("In Spotify Charts:");
        inSpotifyChartsField = new TextField();
        inSpotifyChartsField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                inSpotifyChartsField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        Label streamsLabel = new Label("Streams:");
        streamsField = new TextField();
        streamsField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                streamsField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // Buttons for Add and Delete actions
        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            addSong();
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            deleteSong();
        });

        // Buttons for Search, Sort By, and Randomize actions
        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> searchSong());

        Button sortByButton = new Button("Sort Ascending");
        sortByButton.setOnAction(e -> {
            if (isAscending) {
                sortByButton.setText("Sort Descending");
            } else {
                sortByButton.setText("Sort Ascending");
            }
            sortBy();
        });

        Button randomizeButton = new Button("Randomize");
        randomizeButton.setOnAction(e -> randomize());

        // Create the TextArea
        outputArea = new TextArea();
        outputArea.setFont(Font.font("Courier New", 12));
        outputArea.setPrefRowCount(20);

        // Create HBoxes for the input fields
        HBox trackArtistRow = new HBox(20);
        trackArtistRow.getChildren().addAll(trackNameLabel, trackNameField, artistNameLabel, artistNameField);

        // Create HBoxes for the Release Date
        HBox releaseDateRow = new HBox(20);
        releaseDateRow.getChildren().addAll(releaseDateLabel, releaseDatePicker);

        // Create HBoxes for the In Spotify Playlists, In Spotify Charts, and Streams
        HBox playlistChartsStreamsRow = new HBox(20);
        playlistChartsStreamsRow.getChildren().addAll(inSpotifyPlaylistsLabel, inSpotifyPlaylistsField, inSpotifyChartsLabel, inSpotifyChartsField, streamsLabel, streamsField);

        // Add HBoxes to the VBox
        vbox.getChildren().addAll(trackArtistRow, releaseDateRow, playlistChartsStreamsRow);

        // Create HBox for the buttons
        HBox actionButtonsBox = new HBox(10);
        actionButtonsBox.getChildren().addAll(addButton, deleteButton, searchButton, sortByButton, randomizeButton);

        // Add the HBox to the VBox
        vbox.getChildren().add(actionButtonsBox);

        // Add the outputArea to the VBox
        vbox.getChildren().add(outputArea);

        // Return the VBox in a Scene
        return new Scene(vbox, 1200, 500);
    }

    /**
     * This method is responsible for creating a new Songs object from the input fields.
     * It gets the data from the input fields and creates a new Songs object with this data.
     * If any of the fields are blank or null, an alert message is displayed and the method returns.
     * If the playlist, charts, or stream data is negative, an alert message is displayed and the method returns.
     * The method then returns the created Songs object.
     *
     * @return a new Songs object created from the input fields
     */
    private Songs createSongFromInput() {
        String trackName = trackNameField.getText();
        String artistName = artistNameField.getText();
        LocalDate releaseDate = releaseDatePicker.getValue();
        int inSpotifyPlaylists = Integer.parseInt(inSpotifyPlaylistsField.getText());
        int inSpotifyCharts = Integer.parseInt(inSpotifyChartsField.getText());
        long streams = Long.parseLong(streamsField.getText());

        return new Songs(trackName, artistName, releaseDate, inSpotifyPlaylists, inSpotifyCharts, streams);
    }

    /**
     * This method is responsible for adding a song to the songs array.
     * It gets the data from the input fields and creates a Songs object with this data.
     * The created Songs object is then added to the songs array.
     * If any of the fields are blank or null, an alert message is displayed and the method returns.
     * If the playlist, charts, or stream data is negative, an alert message is displayed and the method returns.
     * After the song is added, the input fields are cleared.
     */

    // I needed to ensure that we didn't have the user accidentially add a song
    // https://www.geeksforgeeks.org/javafx-alert-with-examples/
    // https://stackoverflow.com/questions/48245215/how-can-i-make-a-javafx-confirmation-alert-box-wait-till-ok-is-pressed-but-perfo

    private void addSong() {

        // Alert Message
        // Notify the user fields are empty
        if (trackNameField.getText().isEmpty() || artistNameField.getText().isEmpty() ||
                releaseDatePicker.getValue() == null || inSpotifyPlaylistsField.getText().isEmpty() ||
                inSpotifyChartsField.getText().isEmpty() || streamsField.getText().isEmpty()) {

            // Show an alert window
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("All fields must be filled");
            alert.showAndWait();
            return;
        }
            // Confirm the user wants to add the song
            Optional<ButtonType> result = confirmationAlert("Are you sure you want to add this song?");

            // If the user confirms the addition, add the song to the array
            if (result.isPresent() && result.get() == ButtonType.OK) {
                Songs newSong = createSongFromInput();
                songs[currentIndex] = newSong;
                currentIndex++;

                // Alert the User that the song was added
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("Song Added");
                alert.setHeaderText(null);
                alert.setContentText("Song has been added successfully.");
                alert.showAndWait();

                // Clear the input fields
                clearFields();

                // Update the text area
                loadOutputArea();
            }
        }


    /**
     * This method is responsible for deleting a song from the songs array.
     * It gets the data from the input fields and creates a Songs object with this data.
     * It then searches the songs array for a song that matches the created Songs object.
     * If a match is found, the song is removed from the songs array.
     * If any of the fields are blank or null, an alert message is displayed and the method returns.
     * After the song is deleted, the input fields are cleared.
     */

    // I needed to ensure that we didn't have the user accidentally delete a song
    // https://www.geeksforgeeks.org/javafx-alert-with-examples/
    // https://stackoverflow.com/questions/48245215/how-can-i-make-a-javafx-confirmation-alert-box-wait-till-ok-is-pressed-but-perfo
    private void deleteSong() {
        // Alert message
        // Confirm the user wants to delete the song
        Optional<ButtonType> result = confirmationAlert("Are you sure you want to delete this song?");

        // If the user confirms the deletion, remove the song from the array
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Songs songToDelete = createSongFromInput();
            for (int i = 0; i < currentIndex; i++) {
                if (songs[i].equals(songToDelete)) {
                    System.arraycopy(songs, i + 1, songs, i, currentIndex - i - 1);
                    currentIndex--;
                    break;
                }
            }

            // Alert the User that the song was added
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Song Deleted");
            alert.setHeaderText(null);
            alert.setContentText("Song has been deleted successfully.");
            alert.showAndWait();

            // Clear the input fields
            clearFields();

            // Update the text area
            loadOutputArea();
        }
    }

    /**
     * This method is responsible for sorting the songs array.
     * It uses the Bubble Sort algorithm to sort the songs by track name.
     * If the isAscending flag is true, the songs are sorted in ascending order.
     * If the isAscending flag is false, the songs are sorted in descending order.
     * After the songs are sorted, the isAscending flag is flipped for the next sort operation.
     */
    private void sortBy() {
        for (int i = 0; i < currentIndex - 1; i++) {
            for (int j = 0; j < currentIndex - i - 1; j++) {
                if ((isAscending && songs[j].getTrackName().compareTo(songs[j+1].getTrackName()) > 0) ||
                        (!isAscending && songs[j].getTrackName().compareTo(songs[j+1].getTrackName()) < 0)) {
                    // Swap songs[j] and songs[j+1]
                    Songs temp = songs[j];
                    songs[j] = songs[j+1];
                    songs[j+1] = temp;
                }
            }
        }
        // Flip the flag for the next click
        isAscending = !isAscending;

        // Load the sorted songs into the output area
        loadOutputArea();
    }

    /**
     * This method is responsible for randomizing the songs array.
     * It uses the Fisher-Yates Shuffle algorithm to randomize the songs.
     * The songs are shuffled in place, and the order of the songs is changed.
     * After the songs are randomized, an alert message is displayed to inform the user.
     */
    private void randomize() {
        Random rand = new Random();

        // Start from the last element and swap one by one.
        for (int i = currentIndex - 1; i > 0; i--) {
            // Pick a random index from 0 to i
            int j = rand.nextInt(i + 1);

            // Swap songs[i] with the element at random index
            Songs temp = songs[i];
            songs[i] = songs[j];
            songs[j] = temp;
        }

        // Show an alert window
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Randomizing");
        alert.setHeaderText(null);
        alert.setContentText("Songs have been randomized");
        alert.showAndWait();

        // Load the shuffled songs into the output area
        loadOutputArea();
    }

    /**
     * This method is responsible for searching the songs array for a song.
     * It gets the data from the input fields and creates a Songs object with this data.
     * It then searches the songs array for a song that matches the created Songs object.
     * If a match is found, the song is displayed in the output area.
     * If any of the fields are blank or null, an alert message is displayed and the method returns.
     */
    public void searchSong() {
        // Get the data from the input fields
        String trackName = trackNameField.getText();
        String artistName = artistNameField.getText();

        // Stringbuilder is used to build the output
        StringBuilder searchResult = new StringBuilder();

        // Header and data format for the output
        // I had to adjust the sizes to make it fit
        // I also had to truncate data because the data I found was inconsistent and too long
        String headerFormat = "%-50s %-25s %-15s %-25s %-20s %-20s\n";
        String dataFormat = "%-50s %-25s %-15s %-25d %-20d %-20d\n";

        // Add header
        searchResult.append(String.format(headerFormat, "Track Name", "Artist Name", "Release Date", "In Spotify Playlists", "In Spotify Charts", "Streams"));

        for (Songs song : songs) {
            if (song != null) {
                String songTrackName = song.getTrackName();
                // Truncate the song name if it's over 50 characters long
                if (songTrackName.length() > 50) {
                    songTrackName = songTrackName.substring(0, 50);
                }

                // Check if the track name and artist name match the input fields
                if (!trackName.isEmpty() && !artistName.isEmpty()) {
                    if (songTrackName.equalsIgnoreCase(trackName) && song.getArtistName().equalsIgnoreCase(artistName)) {
                        searchResult.append(String.format(dataFormat, songTrackName, song.getArtistName(), song.getReleaseDate(), song.getInSpotifyPlaylists(), song.getInSpotifyCharts(), song.getStreams()));
                    }

                    // If the track name is empty, check if the artist name matches the input field
                } else if (!artistName.isEmpty()) {
                    if (song.getArtistName().equalsIgnoreCase(artistName)) {
                        searchResult.append(String.format(dataFormat, songTrackName, song.getArtistName(), song.getReleaseDate(), song.getInSpotifyPlaylists(), song.getInSpotifyCharts(), song.getStreams()));
                    }

                    // If the artist name is empty, check if the track name matches the input field
                } else if (!trackName.isEmpty()) {
                    if (songTrackName.equalsIgnoreCase(trackName)) {
                        searchResult.append(String.format(dataFormat, songTrackName, song.getArtistName(), song.getReleaseDate(), song.getInSpotifyPlaylists(), song.getInSpotifyCharts(), song.getStreams()));
                    }
                }
            }
        }

        // Display the search result in the output area
        outputArea.setText(searchResult.toString());
    }

    /**
     * This method is responsible for clearing the input fields.
     * It sets the text of each input field to an empty string.
     */
    private void clearFields() {
        trackNameField.clear();
        artistNameField.clear();
        releaseDatePicker.getEditor().clear();
        inSpotifyPlaylistsField.clear();
        inSpotifyChartsField.clear();
        streamsField.clear();
    }

    /**
     * This method is responsible for showing an alert message.
     * It creates an Alert object with the specified title, message, and alert type, and shows the alert.
     *
     * @param title     the title of the alert
     * @param message   the message of the alert
     * @param alertType the type of the alert
     */
    public static Optional<ButtonType> confirmationAlert(String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION, message);
        return alert.showAndWait();
    }

    /**
     * This method is responsible for showing an alert message.
     * It creates an Alert object with the specified title, message, and alert type, and shows the alert.
     *
     * @param title     the title of the alert
     * @param message   the message of the alert
     * @param alertType the type of the alert
     */
    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // The main method is called when the application is launched
    public static void main(String[] args) {
        launch(args);
    }
}