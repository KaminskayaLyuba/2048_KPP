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

    /**
     * Инициализация таблицы с числами
     */
    public NewThread()
    {
        for(int i=0;i<4;i++)
            for(int j=0;j<4;j++)
                gameArray[i][j]=0;
    }
    
    /**
     * Возврат таблицы с числами
     * @return Таблица 4*4 с целыми числами
     */
    public int[][] getGameArray()
    {
        return gameArray;
    }
    
    /**
     * Добавить 2 в случайную пустую ячейку
     */
    public void add_random()
    {
        int x,y;
        Random rnd = new Random();
        for (int i=0;i<1;i++)                                                   //добавить 1 2
        {
            x=rnd.nextInt()%4;
            if (x<0) x=(-1)*x;
            y=rnd.nextInt()%4;
            if (y<0) y=(-1)*y;
            if (gameArray[x][y]==0)
                gameArray[x][y]=2;
            else
                i--;
        }    
    }
    
    /**
     * Смещение таблицы вверх
     */
    public void btn_up_prsed()                                                  //обработка нажатия кнопки вверх
    {
        int i,j,jp;

        for (i=0;i<4;i++)                                                       //для каждой клетки
            for (j=1;j<4;j++)
                if (gameArray[i][j]!=0)                                         //если в ней не 0
                {
                    jp=j-1;                                                     //проверить можно ли сместить
                    while(gameArray[i][jp]==0&&jp>0) jp--;                      //и на сколько
                    if (gameArray[i][jp]==gameArray[i][j])                      //если есть возможность соеденить
                    {
                        gameArray[i][j]=0;
                        gameArray[i][jp]*=2;
                    }
                    else
                    {
                        if (gameArray[i][jp]==0)
                        {
                            gameArray[i][jp]=gameArray[i][j];
                            gameArray[i][j]=0;
                        }
                        else
                        {
                           if (gameArray[i][jp+1]==0)
                            {
                                gameArray[i][jp+1]=gameArray[i][j];
                                gameArray[i][j]=0;
                            } 
                        }
                    }
                }

        
    }
    
    /**
     * Смещение таблицы вниз
     */
    public void btn_down_prsed()                                                //аналогично обработка нажатия кнопки вниз
    {
        int i,j,jp;

        for (i=0;i<4;i++)
            for (j=2;j>=0;j--)
                if (gameArray[i][j]!=0)
                {
                    jp=j+1;
                    while(gameArray[i][jp]==0&&jp<3) jp++;
                    if (gameArray[i][jp]==gameArray[i][j])
                    {
                        gameArray[i][j]=0;
                        gameArray[i][jp]*=2;
                    }
                    else
                    {
                        if (gameArray[i][jp]==0)
                        {
                            gameArray[i][jp]=gameArray[i][j];
                            gameArray[i][j]=0;
                        }
                        else
                        {
                           if (gameArray[i][jp-1]==0)
                            {
                                gameArray[i][jp-1]=gameArray[i][j];
                                gameArray[i][j]=0;
                            } 
                        }
                    }
                }
    }
    
    /**
     * Смещение таблицы влево
     */
    public void btn_left_prsed()                                                //аналогично обработка нажатия кнопки влево
    {
        int i,j,jp;

        for (i=1;i<4;i++)
            for (j=0;j<4;j++)
                if (gameArray[i][j]!=0)
                {
                    jp=i-1;
                    while(gameArray[jp][j]==0&&jp>0) jp--;
                    if (gameArray[jp][j]==gameArray[i][j])
                    {
                        gameArray[i][j]=0;
                        gameArray[jp][j]*=2;
                    }
                    else
                    {
                        if (gameArray[jp][j]==0)
                        {
                            gameArray[jp][j]=gameArray[i][j];
                            gameArray[i][j]=0;
                        }
                        else
                        {
                           if (gameArray[jp+1][j]==0)
                            {
                                gameArray[jp+1][j]=gameArray[i][j];
                                gameArray[i][j]=0;
                            } 
                        }
                    }
                }
    }
    
    /**
     * Нажатие кнопки вправо
     */
    public void btn_right_prsed()                                               //аналогично обработка нажатия кнопки вправо
    {
        int i,j,jp;

        for (i=2;i>=0;i--)
            for (j=0;j<4;j++)
                if (gameArray[i][j]!=0)
                {
                    jp=i+1;
                    while(gameArray[jp][j]==0&&jp<3) jp++;
                    if (gameArray[jp][j]==gameArray[i][j])
                    {
                        gameArray[i][j]=0;
                        gameArray[jp][j]*=2;
                    }
                    else
                    {
                        if (gameArray[jp][j]==0)
                        {
                            gameArray[jp][j]=gameArray[i][j];
                            gameArray[i][j]=0;
                        }
                        else
                        {
                           if (gameArray[jp-1][j]==0)
                            {
                                gameArray[jp-1][j]=gameArray[i][j];
                                gameArray[i][j]=0;
                            } 
                        }
                    }
                }
    }
    
}
