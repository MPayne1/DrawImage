import java.io.File;
import java.awt.image.RenderedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Allows user to draw an image 
 * @author Matthew Payne
 */
public class DrawImage extends Application {
	private WritableImage profileImage;
	private String imagePath;

	private static final int CANVAS_WIDTH = 400;
	private static final int CANVAS_HEIGHT = 400;
	private static final int WINDOW_HEIGHT = 450;
	private static final int WINDOW_WIDTH = 600;
	private static final int PADDING = 15;
	private static final int SLIDER_MIN = 1;
	private static final int SLIDER_MAX = 25;

	// Mouse coordinates.
	private double fromX;
	private double fromY;
	private double toX;
	private double toY;

	private Canvas canvas;
	private Stage primaryStage;
	private ColorPicker colour;
	private VBox sidebar;
	private Button straightBtn;
	private Button particleTraceBtn;
	private Button saveBtn;
	private Button EraseBtn;
	private Slider sizeSlider;

	private double strokeSize;

	/**
	 * Construct DrawImage.
	 */
	public DrawImage() {

	}

	/**
	 * Get the profile image.
	 * 
	 * @return The profile image.
	 */
	public WritableImage getprofileImage() {
		return this.profileImage;
	}

	/**
	 * Build the GUI.
	 * 
	 * @return The Pane
	 */
	public Pane buildGUI() {
		BorderPane border = new BorderPane();

		this.canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);

		// Make canvas white.
		StackPane holder = new StackPane();
		holder.getChildren().add(this.canvas);
		holder.setStyle("-fx-background-color: white");
		border.setCenter(holder);

		// Add sidebar for the buttons,slider and colour picker.
		this.sidebar = new VBox();
		this.sidebar.setSpacing(PADDING);
		this.sidebar.setPadding(new Insets(PADDING, PADDING, PADDING, PADDING));
		border.setLeft(this.sidebar);

		createButtons();
		setEventHandlers();

		return border;
	}

	/**
	 * Create the Stage.
	 */
	public void start(Stage stage) {
		Pane root = buildGUI();
		this.primaryStage = stage;
		Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);
		this.primaryStage.setTitle("Profile Image");
		this.primaryStage.setScene(scene);
		this.primaryStage.show();

	}

	/**
	 * Create the buttons,slider, and colour picker.
	 */
	private void createButtons() {
		// Set button texts.
		this.saveBtn = new Button("Save");
		this.straightBtn = new Button("Straight Line");
		this.particleTraceBtn = new Button("Particle Trace");
		this.EraseBtn = new Button("Erase");

		// Set button widths.
		this.saveBtn.setMaxWidth(Double.MAX_VALUE);
		this.straightBtn.setMaxWidth(Double.MAX_VALUE);
		this.particleTraceBtn.setMaxWidth(Double.MAX_VALUE);
		this.EraseBtn.setMaxWidth(Double.MAX_VALUE);

		// Set slider.
		this.sizeSlider = new Slider();
		this.sizeSlider.setMaxWidth(Double.MAX_VALUE);
		this.sizeSlider.setShowTickLabels(true);
		this.sizeSlider.setMax(SLIDER_MAX);
		this.sizeSlider.setMin(SLIDER_MIN);

		// Set ColorPicker.
		this.colour = new ColorPicker();
		this.colour.setMaxWidth(Double.MAX_VALUE);

		// Add all buttons,slider,ColorPicker to the sidebar
		this.sidebar.getChildren().addAll(this.straightBtn, this.particleTraceBtn, this.colour, this.sizeSlider,
				this.EraseBtn, this.saveBtn);

	}

	/**
	 * Set event Handlers.
	 */
	private void setEventHandlers() {

		// Straight line button event handler
		this.straightBtn.setOnAction(lineBtn -> {
			this.canvas.setOnMousePressed(press -> {
				this.fromX = press.getX();
				this.fromY = press.getY();
			});

			this.canvas.setOnMouseDragged(drag -> {

			});

			this.canvas.setOnMouseReleased(event -> {
				drawStraightLine(event);
			});

		});

		// Particle trace button event handler
		this.particleTraceBtn.setOnAction(traceBtn -> {
			this.canvas.setOnMousePressed(press -> {
				this.fromX = press.getX();
				this.fromY = press.getY();
			});
			this.canvas.setOnMouseDragged(drag -> {
				this.fromX = drag.getX();
				this.fromY = drag.getY();
				drawParticleTrace();
			});
			this.canvas.setOnMouseReleased(release -> {

			});
		});

		// Save button event handler
		this.saveBtn.setOnAction(save -> {
			try {
				saveImage();
			} catch (IOException e) {
				e.printStackTrace();
			}

		});

		// Size slider event handler.
		this.sizeSlider.valueProperty().addListener(size -> {
			this.strokeSize = sizeSlider.getValue();
		});

		// Erase button event handler.
		this.EraseBtn.setOnAction(erase -> {
			this.colour.setValue(Color.WHITE);

			this.canvas.setOnMousePressed(press -> {
				this.fromX = press.getX();
				this.fromY = press.getY();
			});
			this.canvas.setOnMouseDragged(drag -> {
				this.fromX = drag.getX();
				this.fromY = drag.getY();
				drawParticleTrace();
			});
			this.canvas.setOnMouseReleased(release -> {

			});
		});

	}

	/**
	 * Draw a straight line.
	 */
	private void drawStraightLine(MouseEvent to) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.setStroke(colour.getValue());
		gc.setLineWidth(this.strokeSize);

		this.toX = to.getX();
		this.toY = to.getY();

		gc.strokeLine(this.fromX, this.fromY, this.toX, this.toY);
	}

	/**
	 * Draw a particle trace.
	 */
	private void drawParticleTrace() {
		GraphicsContext gc = this.canvas.getGraphicsContext2D();
		gc.setStroke(this.colour.getValue());
		gc.setFill(this.colour.getValue());
		if (this.strokeSize < 3) {
			gc.fillOval(this.fromX, this.fromY, this.strokeSize + 2, this.strokeSize + 2);
		} else {
			gc.fillOval(this.fromX, this.fromY, this.strokeSize, this.strokeSize);
		}

	}

	/**
	 * Save the profile image.
	 * 
	 * @throws IOException
	 */
	private void saveImage() throws IOException {
		// Capture what's on the canvas.
		SnapshotParameters spa = new SnapshotParameters();
		this.profileImage = this.canvas.snapshot(spa, this.profileImage);

		FileChooser fileChooser = new FileChooser();

		// Save to a file and set the image path.
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("png files (*.png)", "*.png");
		fileChooser.getExtensionFilters().add(extFilter);

		File file = fileChooser.showSaveDialog(this.primaryStage);

		if (file != null) {
			RenderedImage renderedImage = SwingFXUtils.fromFXImage(this.profileImage, null);
			ImageIO.write(renderedImage, "png", file);
			setImagePath("file:///" + file.getAbsolutePath());
		}

	}

	/**
	 * Get the profile image.
	 * 
	 * @return The profile image.
	 */
	public WritableImage getImage() {
		return this.profileImage;
	}

	/**
	 * Set the image path.
	 * 
	 * @param path
	 */
	public void setImagePath(String path) {
		this.imagePath = path;
	}

	/**
	 * Get the image path.
	 * 
	 * @return The image path.
	 */
	public String getImagePath() {
		return this.imagePath;
	}

	/**
	 * Get the window height.
	 * 
	 * @return The window height.
	 */
	public int getWindowHeight() {
		return WINDOW_HEIGHT;
	}

	/**
	 * Get the window width.
	 * 
	 * @return The window width.
	 */
	public int getWindowWidth() {
		return WINDOW_WIDTH;
	}

}