/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg2048;

import java.util.Random;

/**
 * 
 * @author lyuba
 */
public class NewThread extends Thread{
    int[][] gameArray = new int[4][4];
    
    @Override
    public void start() {
  
    }

    /**
     * Инициализация таблицы с числами
     */
    public NewThread() {
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
                gameArray[i][j]=0;
    }
    
    /**
     * Возврат таблицы с числами
     * @return Таблица 4*4 с целыми числами
     */
    public int[][] getGameArray() {
        return gameArray;
    }
    
    /**
     * Добавить 2 в случайную пустую ячейку
     */
    public void add_random() {
        int x,y;
        Random rnd = new Random();
        for (int i=0;i<1;i++) {
            x=rnd.nextInt(4);
            y=rnd.nextInt(4);
            if (gameArray[x][y]==0)
                gameArray[x][y]=2;
            else
                i--;
        }    
    }
    
    /**
     * Смещение таблицы вверх
     */
    public void btn_up_prsed() {
        int i,j,temp;
        for (i=0;i<4;i++)                                                       //для каждой клетки
            for (j=1;j<4;j++)
                if (gameArray[i][j]!=0) {
                    temp=j-1;                                                   //проверить можно ли сместить
                    while(gameArray[i][temp]==0&&temp>0)                        //и на сколько
                        temp--;                      
                    if (gameArray[i][temp]==gameArray[i][j]) {
                        gameArray[i][j]=0;
                        gameArray[i][temp]*=2;
                    } else {
                        if (gameArray[i][temp]==0) {
                            gameArray[i][temp]=gameArray[i][j];
                            gameArray[i][j]=0;
                        } else {
                            if (gameArray[i][temp+1]==0) {
                                gameArray[i][temp+1]=gameArray[i][j];
                                gameArray[i][j]=0;
                            } 
                        }
                    }
                }

        
    }
    
    /**
     * Смещение таблицы вниз
     */
    public void btn_down_prsed() {                                               //аналогично обработка нажатия кнопки вниз
        int i,j,temp;

        for (i=0;i<4;i++)
            for (j=2;j>=0;j--)
                if (gameArray[i][j]!=0) {
                    temp=j+1;
                    while(gameArray[i][temp]==0&&temp<3) 
                        temp++;
                    if (gameArray[i][temp]==gameArray[i][j]) {
                        gameArray[i][j]=0;
                        gameArray[i][temp]*=2;
                    } else {
                        if (gameArray[i][temp]==0) {
                            gameArray[i][temp]=gameArray[i][j];
                            gameArray[i][j]=0;
                        } else {
                           if (gameArray[i][temp-1]==0) {
                                gameArray[i][temp-1]=gameArray[i][j];
                                gameArray[i][j]=0;
                            } 
                        }
                    }
                }
    }
    
    /**
     * Смещение таблицы влево
     */
    public void btn_left_prsed() {                                               //аналогично обработка нажатия кнопки влево
        int i,j,temp;
        for (i=1;i<4;i++)
            for (j=0;j<4;j++)
                if (gameArray[i][j]!=0) {
                    temp=i-1;
                    while(gameArray[temp][j]==0&&temp>0) 
                        temp--;
                    if (gameArray[temp][j]==gameArray[i][j]) {
                        gameArray[i][j]=0;
                        gameArray[temp][j]*=2;
                    } else {
                        if (gameArray[temp][j]==0) {
                            gameArray[temp][j]=gameArray[i][j];
                            gameArray[i][j]=0;
                        } else {
                           if (gameArray[temp+1][j]==0) {
                                gameArray[temp+1][j]=gameArray[i][j];
                                gameArray[i][j]=0;
                            } 
                        }
                    }
                }
    }
    
    /**
     * Нажатие кнопки вправо
     */
    public void btn_right_prsed() {                                               //аналогично обработка нажатия кнопки вправа
        int i,j,temp;

        for (i=2;i>=0;i--)
            for (j=0;j<4;j++)
                if (gameArray[i][j]!=0) {
                    temp=i+1;
                    while(gameArray[temp][j]==0&&temp<3) 
                        temp++;
                    if (gameArray[temp][j]==gameArray[i][j]) {
                        gameArray[i][j]=0;
                        gameArray[temp][j]*=2;
                    } else {
                        if (gameArray[temp][j]==0) {
                            gameArray[temp][j]=gameArray[i][j];
                            gameArray[i][j]=0;
                        } else {
                           if (gameArray[temp-1][j]==0) {
                                gameArray[temp-1][j]=gameArray[i][j];
                                gameArray[i][j]=0;
                            } 
                        }
                    }
                }
    }
    
}
