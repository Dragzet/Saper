package com.example.saper;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.concurrent.ThreadLocalRandom;


public class MainActivity extends AppCompatActivity {

    static Button[][] field;
    int flagsCount = 30;
    TextView res;
    Button restart;

    TextView mineCountShow;
    static int WIDTH = 15;
    int currentMinesCounter = 30;
    final int absMinesCounter = 30;
    static int HEIGHT = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        res = findViewById(R.id.res);
        restart = findViewById(R.id.again);
        mineCountShow = (TextView) findViewById(R.id.minesCounter);
        mineCountShow.setText("Количество мин: " + currentMinesCounter + " из " + absMinesCounter + "\nКоличество флажков:" + flagsCount);
        generate();
    }


    @Override
    protected void onResume() {
        super.onResume();

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generate();
            }
        });

    }

    public String getMinesCount(Button[][] area, int x, int y){
        int res = 0;
        for (int i = x - 1; i <= x + 1; i++){
            for (int j = y - 1; j <= y + 1; j++){
                try{
                    if (area[i][j].getTag() == "mine") res++;
                }catch (Exception e){
                    continue;
                }
            }
        }
        if (res == 0) return "";
        return String.valueOf(res);
    }

    public void deleteFreeFields(Button[][] area, int x, int y, int counter, int need){
        if (counter >= 3) return;
        for (int i = x - 1; i <= x + 1; i++){
            for (int j = y - 1; j <= y + 1; j++){
                need++;
                try{
                    if (area[i][j].getTag() == "free") {
                        if (need % 2 == 0) {
                            String temp = getMinesCount(area, i, j);
                            if (temp != "") area[i][j].setText(temp);
                            area[i][j].setBackgroundColor(Color.DKGRAY);
                            area[i][j].setTextColor(Color.WHITE);
                            deleteFreeFields(area, i, j, counter + 1, need);
                        }
                    }
                    else counter++;
                }
                catch (Exception e) {
                    continue;
                }
            }
        }
    }

    public void generate(){

        field = new Button[HEIGHT][WIDTH];
        GridLayout layout = findViewById(R.id.grid);
        layout.removeAllViews();
        layout.setColumnCount(WIDTH);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        if (res.getVisibility() == View.VISIBLE){
            res.setVisibility(View.INVISIBLE);
            restart.setVisibility(View.INVISIBLE);
        }

        for (int row = 0; row < HEIGHT; row++){
            for (int column = 0; column < WIDTH; column++){
                field[row][column] = (Button) inflater.inflate(R.layout.field, layout, false);
            }
        }

        for (int i = 0; i < 30; i++){
            int randomNum = ThreadLocalRandom.current().nextInt(0, 15);
            int randomNum2 = ThreadLocalRandom.current().nextInt(0, 15);
            while (field[randomNum][randomNum2].getTag() == "mine"){
                randomNum = ThreadLocalRandom.current().nextInt(0, 15);
                randomNum2 = ThreadLocalRandom.current().nextInt(0, 15);
            }
            field[randomNum][randomNum2].setTag("mine");
            //field[randomNum][randomNum2].setBackgroundColor(Color.RED);
        }
        boolean flag = true;
        for (int row = 0; row < HEIGHT; row++){
            for (int column = 0; column < WIDTH; column++){
                if(field[row][column].getTag() != "mine"){
                    if (flag){
                        flag = false;
                        field[row][column].setBackgroundColor(Color.BLUE);
                    }
                    field[row][column].setTag("free");
                }
                field[row][column].setOnLongClickListener(view -> {

                    if(view.getTag() == "mine"){
                        currentMinesCounter--;
                        if (currentMinesCounter == 0){
                            res.setText("Вы выиграли!");
                            res.setVisibility(View.VISIBLE);
                            restart.setVisibility(View.VISIBLE);
                        }
                    }else{
                        flagsCount--;

                        if (flagsCount == 0){
                            res.setText("Вы проиграли!");
                            res.setVisibility(View.VISIBLE);
                            restart.setVisibility(View.VISIBLE);
                        }
                        view.setTag("unMined");
                        view.setBackgroundColor(Color.BLUE);
                    }
                    mineCountShow.setText("Количество мин: " + currentMinesCounter + " из " + absMinesCounter + "\nКоличество флажков:" + flagsCount);
                    return true;
                });
                int finalColumn = column;
                int finalRow = row;
                field[row][column].setOnClickListener(view -> {
                    if (view.getTag() == "mine"){
                        res.setText("Вы проиграли!");
                        res.setVisibility(View.VISIBLE);
                        restart.setVisibility(View.VISIBLE);
                        for (int i = 0; i < HEIGHT; i++) {
                            for (int j = 0; j < WIDTH; j++) {
                                if (field[i][j].getTag() == "mine") field[i][j].setBackgroundColor(Color.RED);
                            }
                        }
                    } else if (view.getTag() == "free") {
                        field[finalRow][finalColumn].setTextColor(Color.WHITE);
                        field[finalRow][finalColumn].setText(getMinesCount(field, finalRow, finalColumn));
                        view.setBackgroundColor(Color.DKGRAY);
                        deleteFreeFields(field, finalRow, finalColumn, 1, 1);
                    }

                });

                layout.addView(field[row][column]);
            }

        }
    }

}