package application;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class LibraryController {

    @FXML
    private TableView<?> libraryDisplay;

    @FXML
    private TableColumn<?, ?> library;

    @FXML
    private ScrollBar libraryScroller;

    @FXML
    private Button play;

    @FXML
    private Button backward;

    @FXML
    private Button pause;

    @FXML
    private Button forward;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Button muteUnmute;

    @FXML
    private Slider seek;

}
