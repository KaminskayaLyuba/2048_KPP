/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2048;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.Group;
import javafx.scene.layout.*;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 *
 * @author люба
 */
public class Main extends Application {
    boolean levelButtonsExist=false;
    boolean difficulty=true;
    @Override
    public void start(Stage primaryStage) {
        
        Button btnNewGame = new Button();
        btnNewGame.setText("New Game");
        btnNewGame.setLayoutX(100);
        btnNewGame.setLayoutY(100);
        Button btnEasyGame = new Button();
        btnEasyGame.setLayoutX(200);
        btnEasyGame.setLayoutY(200);
        btnEasyGame.setText("Easy");
        btnEasyGame.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                newGame(primaryStage);
            }
        });
        Button btnHardGame = new Button();
        btnHardGame.setLayoutX(300);
        btnHardGame.setLayoutY(300);
        btnHardGame.setText("Hard");
        btnHardGame.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                newGame(primaryStage);
                difficulty=false;
            }
        });
        
        Button btnExit = new Button();
        btnExit.setText("Exit");
        btnExit.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                System.exit(0);
            }
        });
        
        btnExit.setLayoutX(400);
        btnExit.setLayoutY(400);
        
        Group root = new Group();
        btnNewGame.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                if(!levelButtonsExist)
                {
                    root.getChildren().add(btnHardGame);
                    root.getChildren().add(btnEasyGame);
                    primaryStage.show();
                    levelButtonsExist=true;
                }                
            }
        });
        root.getChildren().add(btnExit);
        root.getChildren().add(btnNewGame);
        Scene scene = new Scene(root, 500, 500);
        primaryStage.setTitle("2048");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void newGame(Stage stage)
    {
        GridPane gameTable = new GridPane();
        gameTable.setGridLinesVisible(true);
        gameTable.setHgap(10);
        gameTable.setVgap(10);
        gameTable.setLayoutX(20);
        gameTable.setLayoutY(20);
        
        for(int i=0;i<4;i++)
        {
            gameTable.getColumnConstraints().add(new ColumnConstraints(75));
        }
        
        for(int i=0;i<4;i++)
        {
            gameTable.getRowConstraints().add(new RowConstraints(75));
        }
        
        Label[][] gridLabels=new Label[4][4];                                   //перемещение игрового поля в эту таблицу
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
            {
                gridLabels[i][j]= new Label("    "+Integer.toString(0));
                GridPane.setConstraints(gridLabels[i][j], i, j);
                gameTable.getChildren().addAll(gridLabels[i][j]);
            }
        
        Group root = new Group();
        root.getChildren().add(gameTable);
        Scene scene = new Scene(root, 500, 500);
        stage.setTitle("2048");
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
