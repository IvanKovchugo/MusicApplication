package sample.musicapplication;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableView;
import sample.musicapplication.model.Album;
import sample.musicapplication.model.Artist;
import sample.musicapplication.model.DataSource;

public class Controller {

    @FXML
    public ProgressBar progressBar;

    @FXML
    private TableView artistTable;

    @FXML
    public void listArtist() {
        Task<ObservableList<Artist>> task = new GetAllArtistsTask();
        artistTable.itemsProperty().bind(task.valueProperty());
        progressBar.progressProperty().bind(task.progressProperty());

        progressBar.setVisible(true);

        task.setOnSucceeded(e -> progressBar.setVisible(false));
        task.setOnFailed(e -> progressBar.setVisible(false));

        new Thread(task).start();


    }

    @FXML
    public void listAlbumsForArtist() {
        final Artist artist = (Artist) artistTable.getSelectionModel().getSelectedItem();
        if (artist == null) {
            System.out.println("NO ARTIST SELECTED");
            return;
        }

        Task<ObservableList<Album>> task = new Task<ObservableList<Album>>() {
            @Override
            protected ObservableList<Album> call() throws Exception {
                return FXCollections.observableArrayList(DataSource.getInstance().queryAlbumsForArtistId(artist.getId()));
            }
        };
        artistTable.itemsProperty().bind(task.valueProperty());

        new Thread(task).start();
    }

    @FXML
    public void updateArtist() {
        final Artist artist = (Artist) artistTable.getItems().get(2);
//        final Artist artist = (Artist) artistTable.getSelectionModel().getSelectedItem();

        Task<Boolean> task = new Task<Boolean>() {
            @Override
            protected Boolean call() throws Exception {
                return DataSource.getInstance().updateArtistName(artist.getId(), "AC/DC");
            }
        };

        task.setOnSucceeded(e -> {
            if (task.valueProperty().get()) {
                artist.setName("AC/DC");
                artistTable.refresh();
            }
        });

        new Thread(task).start();
    }
}


class GetAllArtistsTask extends Task {
    @Override
    public ObservableList<Artist> call() {
        return FXCollections.observableArrayList(DataSource.getInstance().queryArtists(DataSource.ORDER_BY_ASC));
    }
}