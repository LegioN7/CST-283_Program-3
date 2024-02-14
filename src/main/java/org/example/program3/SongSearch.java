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
public class SongSearch extends Application {
    private static final String FILE_NAME = "songs.txt";
    private static final int ARRAY_SIZE = 952;

    private TextField trackNameField;
    private TextField artistNameField;
    private TextField releasedYearField;
    private TextField releasedMonthField;
    private TextField releasedDayField;
    private TextField releaseDateField;
    private DatePicker releaseDatePicker;
    private TextField inSpotifyPlaylistsField;
    private TextField inSpotifyChartsField;
    private TextField streamsField;

    private static TextArea outputArea;
    private static Songs[] songs = new Songs[ARRAY_SIZE];
    private static int currentIndex = 0;

    private boolean isAscending = true;

    private TableView<Songs> tableView;


    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Spotify 2023 Song Search");
        Scene scene = songSearchGUI();
        stage.setScene(scene);
        stage.show();
        loadSongData(); // Now load the data
    }

    public static class FileNotFoundException extends Exception {
        public FileNotFoundException(String message) {
            super(message);
        }
    }

    private void loadSongData() throws FileNotFoundException {
        System.out.println("Loading song data...");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME));
            String line;

            while ((line = reader.readLine()) != null && currentIndex < ARRAY_SIZE) {
                System.out.println("Reading line: " + line); // Debugging statement

                // Use a regular expression to split the line into parts, correctly handling fields that contain commas
                String[] parts = line.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (parts.length < 8) {
                    System.out.println("Skipping line - Insufficient fields: " + line);
                    continue; // Skip to the next line
                }

                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].replaceAll("\\s", " ").trim();
                }

                String trackName = parts[0];
                String artistName = parts[1].replaceAll("^\"|\"$", ""); // Remove the quotes from the artist name
                int releasedYear = Integer.parseInt(parts[2]);
                int releasedMonth = Integer.parseInt(parts[3]);
                int releasedDay = Integer.parseInt(parts[4]);
                int inSpotifyPlaylists = Integer.parseInt(parts[5]);
                int inSpotifyCharts = Integer.parseInt(parts[6]);
                long streams = Long.parseLong(parts[7]);

                LocalDate releaseDate = LocalDate.of(releasedYear, releasedMonth, releasedDay);
                songs[currentIndex] = new Songs(trackName, artistName, releaseDate, inSpotifyPlaylists, inSpotifyCharts, streams);

                ObservableList<Songs> songList = FXCollections.observableArrayList();
                songList.addAll(Arrays.asList(songs).subList(0, currentIndex));
                tableView.setItems(songList);

                currentIndex++;
            }
            reader.close();

        } catch (java.io.FileNotFoundException e) {
            throw new FileNotFoundException("The file " + FILE_NAME + " was not found.");
        } catch (IOException e) {
            showAlert("Error", e.getMessage(), AlertType.ERROR);
        }
    }

    private Scene songSearchGUI() {
        VBox vbox = new VBox(30);
        vbox.setPadding(new Insets(30));

        Label trackNameLabel = new Label("Track Name:");
        trackNameField = new TextField();
        Label artistNameLabel = new Label("Artist Name:");
        artistNameField = new TextField();
        Label releaseDateLabel = new Label("Release Date:");
        releaseDatePicker = new DatePicker();
        Label inSpotifyPlaylistsLabel = new Label("In Spotify Playlists:");
        inSpotifyPlaylistsField = new TextField();
        Label inSpotifyChartsLabel = new Label("In Spotify Charts:");
        inSpotifyChartsField = new TextField();
        Label streamsLabel = new Label("Streams:");
        streamsField = new TextField();

        // Buttons for Add, Delete, Sort By, and Randomize actions
        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {
            Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to add?");
            alert.showAndWait();
            addSong();
        });

        Button deleteButton = new Button("Delete");
        deleteButton.setOnAction(e -> {
            Alert alert = new Alert(AlertType.CONFIRMATION, "Are you sure you want to remove?");
            alert.showAndWait();
            deleteSong();
        });

        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> searchSong());

        Button sortByButton = new Button("Sort By");
        sortByButton.setOnAction(e -> sortBy());

        Button randomizeButton = new Button("Randomize");
        randomizeButton.setOnAction(e -> randomize());

        outputArea = new TextArea();
        outputArea.setFont(Font.font("Courier New", 12));
        outputArea.setPrefRowCount(10);

        // Create the TableView
        // This TableView will display the songs in a table format
        // The TableView will have 6 columns: Track Name, Artist Name, Release Date, In Spotify Playlists, In Spotify Charts, Streams
        tableView = new TableView<>();

        // Create the TableColumns
        
        TableColumn<Songs, String> trackNameColumn = new TableColumn<>("Track Name");
        trackNameColumn.setCellValueFactory(new PropertyValueFactory<>("trackName"));
        trackNameColumn.setMinWidth(340);

        TableColumn<Songs, String> artistNameColumn = new TableColumn<>("Artist Name");
        artistNameColumn.setCellValueFactory(new PropertyValueFactory<>("artistName"));
        artistNameColumn.setMinWidth(200);

        TableColumn<Songs, Integer> releaseDateColumn = new TableColumn<>("Release Date");
        releaseDateColumn.setCellValueFactory(new PropertyValueFactory<>("releaseDate"));
        releaseDateColumn.setMinWidth(150);

        TableColumn<Songs, Integer> inSpotifyPlaylistsColumn = new TableColumn<>("In Spotify Playlists");
        inSpotifyPlaylistsColumn.setCellValueFactory(new PropertyValueFactory<>("inSpotifyPlaylists"));
        inSpotifyPlaylistsColumn.setMinWidth(150);

        TableColumn<Songs, Integer> inSpotifyChartsColumn = new TableColumn<>("In Spotify Charts");
        inSpotifyChartsColumn.setCellValueFactory(new PropertyValueFactory<>("inSpotifyCharts"));
        inSpotifyChartsColumn.setMinWidth(150);

        TableColumn<Songs, Long> streamsColumn = new TableColumn<>("Streams");
        streamsColumn.setCellValueFactory(new PropertyValueFactory<>("streams"));
        streamsColumn.setMinWidth(150);

        // Add the TableColumns to the TableView
        tableView.getColumns().add(trackNameColumn);
        tableView.getColumns().add(artistNameColumn);
        tableView.getColumns().add(releaseDateColumn);
        tableView.getColumns().add(inSpotifyPlaylistsColumn);
        tableView.getColumns().add(inSpotifyChartsColumn);
        tableView.getColumns().add(streamsColumn);



        HBox searchSortRandomizeBox = new HBox(10);
        searchSortRandomizeBox.getChildren().addAll(searchButton, sortByButton, randomizeButton);

        HBox addRemoveBox = new HBox(10);
        addRemoveBox.getChildren().addAll(addButton, deleteButton);

        vbox.getChildren().addAll(searchSortRandomizeBox, addRemoveBox);

        // Create HBoxes for each row
        HBox trackArtistRow = new HBox(20);
        trackArtistRow.getChildren().addAll(trackNameLabel, trackNameField, artistNameLabel, artistNameField);;

        HBox releaseDateRow = new HBox(20);
        releaseDateRow.getChildren().addAll(releaseDateLabel, releaseDatePicker);

        HBox playlistChartsStreamsRow = new HBox(20);
        playlistChartsStreamsRow.getChildren().addAll(inSpotifyPlaylistsLabel, inSpotifyPlaylistsField, inSpotifyChartsLabel, inSpotifyChartsField, streamsLabel, streamsField);

        // Add HBoxes to the VBox
        vbox.getChildren().addAll(trackArtistRow, releaseDateRow, playlistChartsStreamsRow);


        vbox.getChildren().add(outputArea);
        vbox.getChildren().add(tableView);

        return new Scene(vbox, 1200, 1080);
    }

    private void addSong() {
        String trackName = trackNameField.getText();
        String artistName = artistNameField.getText();
        LocalDate releaseDate = releaseDatePicker.getValue();
        String inSpotifyPlaylists = inSpotifyPlaylistsField.getText();
        String inSpotifyCharts = inSpotifyChartsField.getText();
        String streams = streamsField.getText();

        // Check if any of the fields are blank or null
        if (trackName.isBlank() || artistName.isBlank() || releaseDate == null || inSpotifyPlaylists.isBlank() || inSpotifyCharts.isBlank() || streams.isBlank()) {
            showAlert("Error", "None of the fields can be blank.", AlertType.ERROR);
            return;
        }

        // Check if the playlist, charts, or stream data is negative
        if (Integer.parseInt(inSpotifyPlaylists) < 0 || Integer.parseInt(inSpotifyCharts) < 0 || Long.parseLong(streams) < 0) {
            showAlert("Error", "Playlist, charts, or stream data cannot be negative.", AlertType.ERROR);
            return;
        }

        // Get the year, month, and day from the release date
        int releasedYear = releaseDate.getYear();
        int releasedMonth = releaseDate.getMonthValue();
        int releasedDay = releaseDate.getDayOfMonth();

        songs[currentIndex] = Songs.createSong(trackName, artistName, releaseDate, Integer.parseInt(inSpotifyPlaylists), Integer.parseInt(inSpotifyCharts), Long.parseLong(streams));
        songs[currentIndex].setReleasedYear(releasedYear);
        songs[currentIndex].setReleasedMonth(releasedMonth);
        songs[currentIndex].setReleasedDay(releasedDay);
        currentIndex++;

        clearFields();
    }

    private void deleteSong() {
        String trackName = trackNameField.getText();
        String artistName = artistNameField.getText();
        LocalDate releaseDate = releaseDatePicker.getValue();
        String inSpotifyPlaylists = inSpotifyPlaylistsField.getText();
        String inSpotifyCharts = inSpotifyChartsField.getText();
        String streams = streamsField.getText();

        if (trackName.isBlank() || artistName.isBlank() || releaseDate == null || inSpotifyPlaylists.isBlank() || inSpotifyCharts.isBlank() || streams.isBlank()) {
            showAlert("Error", "None of the fields can be blank.", AlertType.ERROR);
            return;
        }

        if (Integer.parseInt(inSpotifyPlaylists) < 0 || Integer.parseInt(inSpotifyCharts) < 0 || Long.parseLong(streams) < 0) {
            showAlert("Error", "Playlist, charts, or stream data cannot be negative.", AlertType.ERROR);
            return;
        }

        Songs songToDelete = Songs.createSong(trackName, artistName, releaseDate, Integer.parseInt(inSpotifyPlaylists), Integer.parseInt(inSpotifyCharts), Long.parseLong(streams));

        for (int i = 0; i < currentIndex; i++) {
            if (songs[i].equals(songToDelete)) {
                System.arraycopy(songs, i + 1, songs, i, currentIndex - i - 1);
                currentIndex--;
                break;
            }
        }
        clearFields();
    }

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
    }

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
    }

    public void searchSong() {
        String trackName = trackNameField.getText();
        String artistName = artistNameField.getText();

        StringBuilder searchResult = new StringBuilder();
        String headerFormat = "%-50s %-25s %-15s %-25s %-20s %-20s\n";
        String dataFormat = "%-50s %-25s %-15s %-25d %-20d %-20d\n";

        // Add header
        searchResult.append(String.format(headerFormat, "Track Name", "Artist Name", "Release Date", "In Spotify Playlists", "In Spotify Charts", "Streams"));

        for (Songs song : songs) {
            if (song != null) {
                String songTrackName = song.getTrackName();
                // Truncate the song name if it's over 200 characters long
                if (songTrackName.length() > 50) {
                    songTrackName = songTrackName.substring(0, 50);
                }

                if (!trackName.isEmpty() && !artistName.isEmpty()) {
                    if (songTrackName.equalsIgnoreCase(trackName) && song.getArtistName().equalsIgnoreCase(artistName)) {
                        searchResult.append(String.format(dataFormat, songTrackName, song.getArtistName(), song.getReleaseDate(), song.getInSpotifyPlaylists(), song.getInSpotifyCharts(), song.getStreams()));
                    }
                } else if (!artistName.isEmpty()) {
                    if (song.getArtistName().equalsIgnoreCase(artistName)) {
                        searchResult.append(String.format(dataFormat, songTrackName, song.getArtistName(), song.getReleaseDate(), song.getInSpotifyPlaylists(), song.getInSpotifyCharts(), song.getStreams()));
                    }
                } else if (!trackName.isEmpty()) {
                    if (songTrackName.equalsIgnoreCase(trackName)) {
                        searchResult.append(String.format(dataFormat, songTrackName, song.getArtistName(), song.getReleaseDate(), song.getInSpotifyPlaylists(), song.getInSpotifyCharts(), song.getStreams()));
                    }
                }
            }
        }

        outputArea.setText(searchResult.toString());
    }

    private void clearFields() {
        trackNameField.clear();
        artistNameField.clear();
        releaseDatePicker.getEditor().clear();
        inSpotifyPlaylistsField.clear();
        inSpotifyChartsField.clear();
        streamsField.clear();
    }

    private static void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}