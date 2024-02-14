package org.example.program3;


import java.time.LocalDate;

// Data Structure
// {track_name} {artist(s)_name} {released_year} {released_month} {released_day} {in_spotify_playlists} {in_spotify_charts} {streams}
// Track Name, Artist(s) Name, Released Year, Released Month, Released Day, In Spotify Playlists, In Spotify Charts, Streams
public class Songs {

    public String trackName;
    public String artistName;
    public int releasedYear;
    public int releasedMonth;
    public int releasedDay;
    public int inSpotifyPlaylists;
    public int inSpotifyCharts;
    public long streams;
    private LocalDate releaseDate;

    public Songs(String trackName, String artistName, LocalDate releaseDate, int inSpotifyPlaylists, int inSpotifyCharts, long streams) {
        this.trackName = trackName;
        this.artistName = artistName;
        this.releaseDate = releaseDate;
        this.inSpotifyPlaylists = inSpotifyPlaylists;
        this.inSpotifyCharts = inSpotifyCharts;
        this.streams = streams;
    }

    public static Songs createSong(String trackName, String artistName, LocalDate releaseDate, int inSpotifyPlaylists, int inSpotifyCharts, long streams) {
        return new Songs(trackName, artistName, releaseDate, inSpotifyPlaylists, inSpotifyCharts, streams);
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public int getReleasedYear() {
        return releasedYear;
    }

    public void setReleasedYear(int releasedYear) {
        this.releasedYear = releasedYear;
    }

    public int getReleasedMonth() {
        return releasedMonth;
    }

    public void setReleasedMonth(int releasedMonth) {
        this.releasedMonth = releasedMonth;
    }

    public int getReleasedDay() {
        return releasedDay;
    }

    public void setReleasedDay(int releasedDay) {
        this.releasedDay = releasedDay;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public int getInSpotifyPlaylists() {
        return inSpotifyPlaylists;
    }

    public void setInSpotifyPlaylists(int inSpotifyPlaylists) {
        this.inSpotifyPlaylists = inSpotifyPlaylists;
    }

    public int getInSpotifyCharts() {
        return inSpotifyCharts;
    }

    public void setInSpotifyCharts(int inSpotifyCharts) {
        this.inSpotifyCharts = inSpotifyCharts;
    }

    public long getStreams() {
        return streams;
    }

    public void setStreams(long streams) {
        this.streams = streams;
    }

    @Override
    public String toString() {
        return trackName + " " + artistName + " " + releaseDate + " " + inSpotifyPlaylists + " " + inSpotifyCharts + " " + streams;
    }

}
