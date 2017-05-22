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
import static java.lang.Thread.sleep;
import java.nio.file.Files;
import java.util.Random;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
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
import javafx.scene.text.*;
import javafx.stage.Stage;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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
    FileOutputStream fis_step_new;
    ObjectOutputStream inStream_step_new;
    AnimationTimer gameLoop;
    static int[] filesSize = new int[10000];
    static int[] filesName = new int[10000];
    
    /**
     * Запуск и инициализация приложения и открытие файлов
     * @param primaryStage Окно для отрисовки первоначального меню
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setResizable(false);
        try{                                                                    //открытие файлов
            fis_step_new = new FileOutputStream("savedSteps_new_annotacion.txt");
            inStream_step_new = new ObjectOutputStream(fis_step_new);
            fos_step = new FileOutputStream("savedSteps_temp.txt");
            outStream_step = new ObjectOutputStream(fos_step);
            fis_step = new FileInputStream("savedSteps.txt");
            inStream_step = new ObjectInputStream(fis_step);
        } catch (Exception e) {            
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

        Button btnSort = new Button();                                      //создание кнопки сортировки
        btnSort.setLayoutX(100);
        btnSort.setLayoutY(400);
        btnSort.setText("Sort");
        btnSort.setOnAction(new EventHandler<ActionEvent>() {               //функция запускающаяся по нажатии на кнопку

            @Override
            public void handle(ActionEvent event) {
                stageSort(primaryStage);
                levelButtonsExist=false;
            }
        });

        Button btnStatistic = new Button();                                      //создание кнопки сттистики
        btnStatistic.setLayoutX(100);
        btnStatistic.setLayoutY(450);
        btnStatistic.setText("Statistics");
        btnStatistic.setOnAction(new EventHandler<ActionEvent>() {               //функция запускающаяся по нажатии на кнопку

            @Override
            public void handle(ActionEvent event) {
                statistics(primaryStage);
                levelButtonsExist=false;
            }
        });

        ImageView background= new ImageView(new Image("file:title.bmp"));
        
        Group root = new Group();
        root.getChildren().addAll(background,btnNewGame,btnExit,btnSort,btnStatistic);
        btnNewGame.setOnAction(new EventHandler<ActionEvent>() {                //добавить кнопки уровней сложности по нажатию на кнопку "новая игра"
            
            @Override
            public void handle(ActionEvent event) {
                if(!levelButtonsExist) {
                    root.getChildren().add(btnHardGame);
                    root.getChildren().add(btnEasyGame);
                    primaryStage.show();
                    levelButtonsExist=true;
                }                
            }
        });
        
        
        Scene scene = new Scene(root, 490, 490);                                //создание сцены
        
        primaryStage.setTitle("2048");
        primaryStage.setScene(scene);
        primaryStage.show();

    }



    class ThreadForReplay extends Thread {
        Stage stage;
        Random random;
        int steps=0;
        int x=0;
        boolean isLoading = false;
        boolean isLoadingBest = false;
        boolean isLoadingWorst = false;

        /**
         * Поток для замедленной отрисовки
         * @param stage текущее окно игры
         * @param steps количество шагов для отрисовки
         * @param isLoading выбор источника последовательности ходов: загрузка из файла или генерация
         */
        ThreadForReplay(Stage stage, int steps, boolean isLoading) {
            this.stage = stage;
            this.random=new Random();
            this.steps = steps;
            this.isLoading = isLoading;
        }

        public void run() {
            AnimationTimer timer = new AnimationTimer() {                       //создание нового таймера
                @Override
                public void handle(long now) {                                  //метод который будет выполняться каждый тик таймера
                    try {
                        if(!(steps<inStream_step.available()/4 && steps>0)&&isLoading) steps=1;
                        direction = Direction.values()[isLoading?inStream_step.readInt():random.nextInt(4)];                                  
                        sleep(360);                                             //пауза между отрисовками
                    } catch (Exception e) {
                            e.printStackTrace();
                    }
                    buttonController();
                    if (lost||won) endGame(stage);
                    Platform.runLater(Main.this::drawGridPane);
                    x++;
                    if (x>=steps) this.stop();
                }
            };
            timer.start();
        }
    }
    
    /**
     * Запуск потока замедленной отрисовки
     * @param stage текущее окно игры
     * @param steps количество шагов для отрисовки
     * @param isLoading выбор источника последовательности ходов: загрузка из файла или генерация
     */

    void autoGame(Stage stage, int steps, boolean isLoading)
    {
        ThreadForReplay aiPlay = new ThreadForReplay(stage, steps, isLoading);
        aiPlay.start();
        try {
            aiPlay.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * Запуск новой игры
     * @param stage Окно для отрисовки игры
     */
    public void newGame(Stage stage) {

        math.start();
        gameTable.setHgap(10);                                                  //настройка игровой таблицы
        gameTable.setVgap(10);
        gameTable.setLayoutX(20);
        gameTable.setLayoutY(20);
        gameTable.setBorder(new Border(new BorderStroke(null,BorderStrokeStyle.SOLID,CornerRadii.EMPTY,BorderStroke.THICK)));
        
        for(int i=0;i<4;i++) 
            gameTable.getColumnConstraints().add(new ColumnConstraints(75));              
        for(int i=0;i<4;i++) 
            gameTable.getRowConstraints().add(new RowConstraints(75));                

        TextField stepToLoad = new TextField();                                 //создание текстового поля для ввода номера хода который надо загрузить
        stepToLoad.setLayoutX(400);
        stepToLoad.setLayoutY(50);
        stepToLoad.setMaxWidth(80);        
        stepToLoad.setOnKeyPressed(new EventHandler<KeyEvent>() {               //по нажатию кнопки ентер в этом поле идет загрузка
            @Override
        public void handle(KeyEvent event) {
            if(event.getCode()==KeyCode.ENTER) {
                autoGame(stage, Integer.parseInt(stepToLoad.getText()), true);
                stepToLoad.clear();
            }
            if (event.getCode().isArrowKey()) {                                 //господи иисусе, прости меня за этот костыль
                direction = direction.valueOf(event.getCode().name());          //я правда не хотела, он сам
                buttonController();                                             //прости меня как компилятор меня прощает
                drawGridPane();
                if (lost||won) endGame(stage);
            }        
        }
        });
        
        Button btnBotToggle = new Button();                                     //создание кнопки включения бота
        btnBotToggle.setLayoutX(400);
        btnBotToggle.setLayoutY(100);
        btnBotToggle.setText("Bot");
        btnBotToggle.setOnAction(new EventHandler<ActionEvent>() {              //включение бота
            @Override
            public void handle(ActionEvent event) {

                autoGame(stage, Integer.parseInt(stepToLoad.getText()), false);
            }
        });

        Button btnBestGame = new Button();                                     //создание кнопки загрузки лучшей игры
        btnBestGame.setLayoutX(400);
        btnBestGame.setLayoutY(200);
        btnBestGame.setText("Best game");
        btnBestGame.setOnAction(new EventHandler<ActionEvent>() {              //включение бота
            @Override
            public void handle(ActionEvent event) {
                String filename = new String();
                filename = "saved" + Integer.toString(filesName[9999]) + ".txt";
                try {
                    inStream_step.close();
                    fis_step.close();
                    fis_step = new FileInputStream(filename);
                    inStream_step = new ObjectInputStream(fis_step);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                autoGame(stage, 1, true);
            }
        });

        Button btnWorstGame = new Button();                                     //создание кнопки загрузки лучшей игры
        btnWorstGame.setLayoutX(400);
        btnWorstGame.setLayoutY(250);
        btnWorstGame.setText("Worst game");
        btnWorstGame.setOnAction(new EventHandler<ActionEvent>() {              //включение бота
            @Override
            public void handle(ActionEvent event) {
                String filename = new String();
                filename = "saved" + Integer.toString(filesName[0]) + ".txt";
                try {
                    inStream_step.close();
                    fis_step.close();
                    fis_step = new FileInputStream(filename);
                    inStream_step = new ObjectInputStream(fis_step);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                autoGame(stage, 1, true);
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
        math.add_random();
        drawGridPane();                                                 
        ImageView background= new ImageView(new Image("file:title.bmp"));
        Group root = new Group();
        root.getChildren().addAll(background,gameTable,btnBotToggle,stepToLoad,btnExit,btnWorstGame,btnBestGame);   //добавление всех элементов на экран
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
    public void endGame(Stage stage) {
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
        ImageView background= new ImageView(new Image("file:"+(lost?"LOST":"WON")+".bmp"));  //создание надписи о победе или поражении
        root.getChildren().addAll(background,btnExit);                          //добавление элементов на экран
        Scene scene = new Scene(root, 500, 500);
        stage.setTitle("2048");
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     * Перерисовка таблицы с числами
     */
    public void drawGridPane() {
        int[][] toDraw = math.getGameArray();
        ImageView[][] gridLabels=new ImageView[4][4];  
        gameTable.getChildren().clear();
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++) {        
                gridLabels[i][j] = new ImageView(new Image(setCellColor(toDraw[i][j])));
                gameTable.add(gridLabels[i][j], i, j);
            }
    }
    
    /**
     * Установка цвета ячейки в зависимости от содержимого
     * @param colorNumber Число для установки цвета
     * @return Цвет ячейки
     */
    public String setCellColor(int colorNumber) {
        return "file:"+Integer.toString(colorNumber)+".bmp";
    }
    
    /**
     * Обработка нажатия на стрелку
     */
    public void buttonController() {
        Random rnd = new Random();
        if (!difficulty&&rnd.nextInt(100)<30) 
            direction = Direction.values()[rnd.nextInt(4)];
      
        try {
        outStream_step.writeInt(direction.ordinal());
        inStream_step_new.writeChars(direction.name()+ " -> ");
        } catch (IOException e) {
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
    public void check_lost() {
        int i,j;
        int[][] numbers = math.getGameArray();
        for(i=0;i<4;i++)
            for(j=0;j<4;j++)
                if (numbers[i][j]==0){ 
                    lost = false; return;
                }
        lost = true;
    }
    
    /**
     *
     * Проверка на выигрыш
     */
    public void check_won() {
        int i,j;
        int[][] numbers = math.getGameArray();
        for(i=0;i<4;i++)
            for(j=0;j<4;j++)
                if (numbers[i][j]==2048) won = true;
    }
    
    /**
     * Безопасный выход
     */
    public void exit() {
        try {
            outStream_step.close();
            inStream_step.close();
            inStream_step_new.close();
            fis_step.close();
            fos_step.close();
            fis_step_new.close();

        } catch(IOException e) {
           e.printStackTrace();
        }
        try {
            File source = new File("savedSteps_temp.txt");
            File dest = new File("savedSteps.txt");
            dest.delete();
            Files.copy(source.toPath(),dest.toPath());
        } catch(IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
    
    public static void sortJava(int low, int high) {
        int i = low, j = high;
        int pivot = filesSize[(high + low) / 2];
        while (i <= j) {
            while (filesSize[i] < pivot) {
                i++;
            }
            while (filesSize[j] > pivot) {
                j--;
            }
            if (i <= j) {
                int temp = filesSize[i];
                filesSize[i] = filesSize[j];
                filesSize[j] = temp;
                temp = filesName[i];
                filesName[i] = filesName[j];
                filesName[j] = temp;
                i++;
                j--;
            }
        }
        if (low < j) {
            sortJava(low, j);
        }
        if (i < high) {
            sortJava(i, high);
        }
    }
    
    public void stageSort(Stage stage){
        for (int i = 0; i < 10000; i++) {
            String filename = new String();
            filename = "saved" + Integer.toString(i) + ".txt";
            try {
                fis_step = new FileInputStream(filename);
                inStream_step = new ObjectInputStream(fis_step);

                filesSize[i] = inStream_step.available() / 4;
                inStream_step.close();
                fis_step.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Button btnSortJava = new Button();                                          //создание кнопки сортировка на Java
        btnSortJava.setText("Java");
        btnSortJava.setLayoutX(240);
        btnSortJava.setLayoutY(200);
        btnSortJava.setOnAction(new EventHandler<ActionEvent>() {                   
            @Override
            public void handle(ActionEvent event) {
                long timeout = System.currentTimeMillis();
                for (int i = 0; i < 10000; i++) {
                    filesName[i] = i;
                }
                sortJava(0, 9999);
                try {
                    fos_step = new FileOutputStream("savedarr_sorted_java.txt");
                    outStream_step = new ObjectOutputStream(fos_step);
                    for (int i = 0; i < 10000; i++) {
                        outStream_step.writeChars(Integer.toString(filesName[i]) + ", ");
                    }
                    outStream_step.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                timeout = System.currentTimeMillis() - timeout;
                System.out.print(" Java QS:");
                System.out.print(timeout);
            }
        });
        
        Button btnSortScala = new Button();                                          //создание кнопки сортировка на Scala
        btnSortScala.setText("Scala");
        btnSortScala.setLayoutX(240);
        btnSortScala.setLayoutY(300);
        btnSortScala.setOnAction(new EventHandler<ActionEvent>() {                   
            @Override
            public void handle(ActionEvent event) {
                sort scalaSort = new sort();
                scalaSort.main(filesSize);
            }
        });
        
        Button btnExit = new Button();                                          //создание кнопки выхода
        btnExit.setText("Exit");
        btnExit.setLayoutX(240);
        btnExit.setLayoutY(400);
        btnExit.setOnAction(new EventHandler<ActionEvent>() {                   //запуск функции выхода
            @Override
            public void handle(ActionEvent event) {
                start(stage);
            }
        });
        
        ImageView background= new ImageView(new Image("file:title.bmp"));
        Group root = new Group();
        root.getChildren().addAll(background,btnExit,btnSortScala,btnSortJava);                          //добавление элементов на экран
        Scene scene = new Scene(root, 500, 500);
        stage.setTitle("2048");
        stage.setScene(scene);
        stage.show();
    }

    public void statistics(Stage stage){

            long timeout = System.currentTimeMillis();
            float[] st = new float[4];
            float sum = 0;
            for (int i = 0; i < 10000; i++) {
                String filename = new String();
                filename = "saved" + Integer.toString(i) + ".txt";
                try {
                    fis_step = new FileInputStream(filename);
                    inStream_step = new ObjectInputStream(fis_step);

                    filesSize[i] = inStream_step.available() / 4;
                    sum += filesSize[i] / Integer.BYTES;
                    for (int j = 0; j < filesSize[i] / Integer.BYTES; j++) {
                        st[inStream_step.readInt()]++;
                    }
                    inStream_step.close();
                    fis_step.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            timeout = System.currentTimeMillis() - timeout;
            System.out.print(" Java STAT:");
            System.out.print(timeout);
            Label[] lb = new Label[4];
            lb[0] = new Label("Up " + Float.toString(st[0] / sum * 100) + "%");
            lb[1] = new Label("Down " + Float.toString(st[1] / sum * 100) + "%");
            lb[2] = new Label("Left " + Float.toString(st[2] / sum * 100) + "%");
            lb[3] = new Label("Rigth " + Float.toString(st[3] / sum * 100) + "%");
            lb[1].setLayoutX(50);
            lb[2].setLayoutX(250);
            lb[3].setLayoutX(50);
            lb[0].setLayoutX(250);
            lb[1].setLayoutY(50);
            lb[2].setLayoutY(50);
            lb[3].setLayoutY(250);
            lb[0].setLayoutY(250);



            Statistics statistics = new Statistics();
            statistics.main();

            Button btnExit = new Button();                                          //создание кнопки выхода
            btnExit.setText("Exit");
            btnExit.setLayoutX(240);
            btnExit.setLayoutY(400);
            btnExit.setOnAction(new EventHandler<ActionEvent>() {                   //запуск функции выхода
                @Override
                public void handle(ActionEvent event) {
                    start(stage);
                }
            });
            ImageView background= new ImageView(new Image("file:title.bmp"));
            Group root = new Group();
            root.getChildren().addAll(background,lb[0], lb[1], lb[2], lb[3],btnExit );
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
