package org.graphstream.ui.fxViewer.util;

import org.graphstream.graph.Graph;
import org.graphstream.ui.fxViewer.FxViewPanel;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class DefaultApplication extends Application {
	public static Graph graph ;
	public static Stage stage ;
	public static FxViewPanel view ;
	public static boolean isInstance = false;
	public static boolean antiAliasing = false ;
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		
		view = graph.displayFx();
		
		stage = primaryStage ;

        Scene scene = new Scene(view, 800, 600, true, SceneAntialiasing.DISABLED);
        
        primaryStage.setScene(scene);
        
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });
        
        isInstance = true ;
        primaryStage.show();
	}
	
	
	public static void checkTitle() {
		String titleAttr = String.format("ui.%s.title", view.getIdView());
		String title = (String) graph.getLabel(titleAttr);

		if (title == null) {
			title = (String) graph.getLabel("ui.default.title");

			if (title == null)
				title = (String) graph.getLabel("ui.title");
		}

		if (title != null)
			stage.setTitle(title);
		else
			stage.setTitle("GraphStream");
		
	}
	

	public static void setAliasing(boolean antialias) {
		if ( antialias != antiAliasing ) {
			System.out.println("setAlias "+antialias);
			antiAliasing = antialias ;
			
			view.getScene().setRoot(new Region());
			Scene newScene ;
			if (antiAliasing)
				newScene = new Scene(view, 800, 600, true, SceneAntialiasing.BALANCED);
			else
				newScene = new Scene(view, 800, 600, true, SceneAntialiasing.DISABLED);
			
			stage.setScene(newScene);
		}
	}

}
