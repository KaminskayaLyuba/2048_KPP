/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2048;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.util.Random;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button; 
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.Stage;
enum Direction {UP,DOWN,LEFT,RIGHT}

/**
 *
 * @author люба
 */
public class Main extends Application {
    boolean levelButtonsExist=false;
    boolean difficulty=true;    
    boolean won=false;
    boolean lost=false;
    boolean bot=false;
    Direction direction;
    GridPane gameTable = new GridPane();
    NewThread math=new NewThread();
    FileOutputStream fos_step;
    ObjectOutputStream outStream_step;
    FileInputStream fis_step;
    ObjectInputStream inStream_step;
  
    @Override
    /**
     * Запуск и инициализация приложения и открытие файлов
     * @param primaryStage Окно для отрисовки первоначального меню
     */
    public void start(Stage primaryStage) {
        math.start();
        try{                                                                    //открытие файлов
            fos_step = new FileOutputStream("savedSteps_temp.txt");
            outStream_step = new ObjectOutputStream(fos_step);;
            fis_step = new FileInputStream("savedSteps.txt");
            inStream_step = new ObjectInputStream(fis_step);
        }
        catch (Exception e)
        {            
            e.printStackTrace();
        }
        
        Button btnNewGame = new Button();                                       //создание кнопки "новая игра"
        btnNewGame.setText("New Game");
        btnNewGame.setLayoutX(100);
        btnNewGame.setLayoutY(100);
        Button btnEasyGame = new Button();                                      //создание кнопки легкого уровня сложности
        btnEasyGame.setLayoutX(200);
        btnEasyGame.setLayoutY(200);
        btnEasyGame.setText("Easy");
        btnEasyGame.setOnAction(new EventHandler<ActionEvent>() {               //функция запускающаяся по нажатии на кнопку
            
            @Override
            public void handle(ActionEvent event) {
                difficulty=true;
                newGame(primaryStage);
            }
        });
        Button btnHardGame = new Button();                                      //создание кнопки сложного уровня сложности
        btnHardGame.setLayoutX(300);
        btnHardGame.setLayoutY(300);
        btnHardGame.setText("Hard");
        btnHardGame.setOnAction(new EventHandler<ActionEvent>() {               //функция запускающаяся по нажатии на кнопку
            @Override
            public void handle(ActionEvent event) {
                difficulty=false;
                newGame(primaryStage);
            }
        });
        
        Button btnExit = new Button();                                          //создание кнопки выхода
        btnExit.setLayoutX(400);
        btnExit.setLayoutY(400);
        btnExit.setText("Exit");
        btnExit.setOnAction(new EventHandler<ActionEvent>() {
            
            @Override
            public void handle(ActionEvent event) {
                exit();
            }
        });
        
        Group root = new Group();
        root.getChildren().add(btnExit);                                        //добавление кнопок выхода и новой игры
        root.getChildren().add(btnNewGame);
        btnNewGame.setOnAction(new EventHandler<ActionEvent>() {                //добавить кнопки уровней сложности по нажатию на кнопку "новая игра"
            
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
        
        
        Scene scene = new Scene(root, 500, 500);                                //создание сцены
        primaryStage.setTitle("2048");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Запуск новой игры
     * @param stage Окно для отрисовки игры
     */
    public void newGame(Stage stage)
    {
        
        gameTable.setHgap(10);                                                  //настройка игровой таблицы
        gameTable.setVgap(10);
        gameTable.setLayoutX(20);
        gameTable.setLayoutY(20);
        gameTable.setBorder(new Border(new BorderStroke(null,BorderStrokeStyle.SOLID,CornerRadii.EMPTY,BorderStroke.THICK)));
        
        for(int i=0;i<4;i++)
        {
            gameTable.getColumnConstraints().add(new ColumnConstraints(75));
        }
        
        for(int i=0;i<4;i++)
        {
            gameTable.getRowConstraints().add(new RowConstraints(75));
        }        
        Button btnBotToggle = new Button();                                     //создание кнопки включения бота
        btnBotToggle.setLayoutX(400);
        btnBotToggle.setLayoutY(100);
        btnBotToggle.setText("Bot");
        btnBotToggle.setOnAction(new EventHandler<ActionEvent>() {              //включение бота
            
            @Override
            public void handle(ActionEvent event) {
                Random rnd=new Random();
                for (int i=0;i<10;i++)                                          //бот делает 10 случайных ходов
                {
                    direction = Direction.values()[rnd.nextInt(4)];
                    buttonController();
                    drawGridPane();
                    if (lost||won) endGame(stage);
                }
            }
        });
        
        TextField stepToLoad = new TextField();                                 //создание текстового поля для ввода номера хода который надо загрузить
        stepToLoad.setLayoutX(400);
        stepToLoad.setLayoutY(50);
        stepToLoad.setMaxWidth(80);        
        stepToLoad.setOnKeyPressed(new EventHandler<KeyEvent>() {               //по нажатию кнопки ентер в этом поле идет загрузка
            @Override
        public void handle(KeyEvent event) {
            if(event.getCode()==KeyCode.ENTER)
                {
                    try{
                        int n = Integer.parseInt(stepToLoad.getText());
                        if(n<inStream_step.available()/4 && n>0)
                            for (int i=0;i<n;i++)
                            {                     
                                int t=inStream_step.readInt();
                                direction = Direction.values()[t];
                                buttonController();
                                if (lost||won) endGame(stage);
                            }
                        stepToLoad.clear();
                        drawGridPane();
                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            if (event.getCode().isArrowKey()) {                                 //господи иисусе, прости меня за этот костыль
                    direction = direction.valueOf(event.getCode().name());      //я правда не хотела, он сам
                    buttonController();                                         //прости меня как компилятор меня прощает
                    drawGridPane();
                    if (lost||won) endGame(stage);
                }        
            }
        });
        
        Button btnExit = new Button();                                          //создание кнопки выхода
        btnExit.setText("Exit");
        btnExit.setLayoutX(400);
        btnExit.setLayoutY(450);
        btnExit.setOnAction(new EventHandler<ActionEvent>() {           
            @Override
            public void handle(ActionEvent event) {
                exit();
            }
        });
        
        drawGridPane();                                                 
        
        Group root = new Group();
        root.getChildren().addAll(gameTable,btnBotToggle,stepToLoad,btnExit);   //добавление всех элементов на экран
        Scene scene = new Scene(root, 500, 500);       
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {                    //обработка нажатия стрелок
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().isArrowKey()) {
                    direction = direction.valueOf(event.getCode().name());
                    buttonController();        
                    drawGridPane();
                    if (lost||won) endGame(stage);
                }                               
            }
        });
        
        stage.setTitle("2048");
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * Создание окна проигрыша или выигрыша
     * @param stage Окно для отрисовки текста
     */
    public void endGame(Stage stage)
    {
        Button btnExit = new Button();                                          //создание кнопки выхода
        btnExit.setText("Exit");
        btnExit.setLayoutX(240);
        btnExit.setLayoutY(400);
        btnExit.setOnAction(new EventHandler<ActionEvent>() {                   //запуск функции выхода
            @Override
            public void handle(ActionEvent event) {
                exit();
            }
        });
        Group root = new Group();
        Label text = new Label(won?"YOU WON!!!":"YOU LOST");                    //создание надписи о победе или поражении
        text.setLayoutX(100);
        text.setLayoutY(100);
        text.setFont(new Font(50));
        root.getChildren().addAll(text,btnExit);                                //добавление элементов на экран
        Scene scene = new Scene(root, 500, 500);
        stage.setTitle("2048");
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * Перерисовка таблицы с числами
     */
    public void drawGridPane()
    {
        int[][] toDraw = math.getGameArray();
        Label[][] gridLabels=new Label[4][4];    
        gameTable.getChildren().clear();
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
            {        
                gridLabels[i][j] = new Label("    "+toDraw[i][j]);
                gridLabels[i][j].setFont(new Font("Arial",20));
                gridLabels[i][j].setBackground(new Background(new BackgroundFill(setCellColor(toDraw[i][j]),null,null)));
                gameTable.setConstraints(gridLabels[i][j], i, j);
                gameTable.getChildren().addAll(gridLabels[i][j]);
            }
    }
    
    /**
     * Установка цвета ячейки в зависимости от содержимого
     * @param colorNumber Число для установки цвета
     * @return Цвет ячейки
     */
    public Color setCellColor(int colorNumber)                      
    {
        switch (colorNumber)
                {
                    case 2: return Color.GRAY;
                    case 4: return Color.BURLYWOOD;
                    case 8: return Color.STEELBLUE;
                    case 16: return Color.DARKKHAKI;
                    case 32: return Color.YELLOWGREEN;
                    case 64: return Color.CHOCOLATE;
                    case 128: return Color.TURQUOISE;
                    case 256: return Color.DARKORANGE;
                    case 512: return Color.SKYBLUE;
                    case 1024: return Color.POWDERBLUE;
                    case 2048: return Color.BLACK;
                    default: return Color.WHITE;
                }
    }
    
    /**
     * Обработка нажатия на стрелку
     */
    public void buttonController()
    {
        Random rnd = new Random();
        if (!difficulty&&rnd.nextInt(100)<30) direction = Direction.values()[rnd.nextInt(4)];
        
        try{
        outStream_step.writeInt(direction.ordinal());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        switch (direction) {
            case UP:    math.btn_up_prsed(); break;
            case DOWN:  math.btn_down_prsed(); break;
            case LEFT:  math.btn_left_prsed(); break;
            case RIGHT: math.btn_right_prsed(); break;
        }
        check_lost();
        check_won();
        if(!lost&&!won) math.add_random();
    }
    
    /**
     *
     * Проверка на проигрыш
     */
    public void check_lost()                                                 //проверка на проигрыш
    {
        int i,j;
        int[][] numbers = math.getGameArray();
        for(i=0;i<4;i++)
            for(j=0;j<4;j++)
                if (numbers[i][j]==0){ lost = false; return;}
        lost = true;
    }
    
    /**
     *
     * Проверка на выигрыш
     */
    public void check_won()                                                  //проверка на выигрыш
    {
        int i,j;
        int[][] numbers = math.getGameArray();
        for(i=0;i<4;i++)
            for(j=0;j<4;j++)
                if (numbers[i][j]==2048) won = true;
    }
    
    /**
     * Безопасный выход
     */
    public void exit()
    {
        try
        {
            outStream_step.close();
            inStream_step.close();
            fis_step.close();
            fos_step.close();
        }
        catch(IOException e)
        {
           e.printStackTrace();
        }
        try
        {
            File source = new File("savedSteps_temp.txt");
            File dest = new File("savedSteps.txt");
            dest.delete();
            Files.copy(source.toPath(),dest.toPath());
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        System.exit(0);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        launch(args);                     
    }
    
}
