package com.example.statki;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    boolean start = false;
    boolean turn = true;

    List<String> mojeStatki = new ArrayList<>();
    List<String> statkiPrzeciwnika = new ArrayList<>();
    List<String> mojeUsed = new ArrayList<>();
    List<String> computerUsed = new ArrayList<>();
    List<String> mojeZbite = new ArrayList<>();
    List<String> computerZbite = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    Button restartButton;
    Button startButton;

    protected void onStart(){
        super.onStart();

        /**
         * przypisanie przycisków start i restart do zmiennych
         */
        restartButton = findViewById(R.id.button2);
        startButton = findViewById(R.id.start_game);

        /**
         * ukrycie przycisku restart
         * podpięcie pod przycisk restartu funkcji restartGame()
         * podpięcie pod przycisk startu funkcji Start()
         */
        restartButton.setVisibility(View.INVISIBLE);
        restartButton.setOnClickListener(view -> restartGame());
        startButton.setOnClickListener(view -> Start());

        /**
         * Podpięcie funkcji selectShip() pod wszystkie przyciski na moim polu
         * Podpięcie funkcji Shoot() pod wszystkie pola przeciwnika
         */
        areaOperation("moje", 1);
        areaOperation("przeciwnik", 2);
    }

    /**
     * Funckja wybiera wszystkie przyciski z pola mojego lub przeciwnika i wykonuje odpowiednią funkcje która została przypisana do danego numeru
     * @param type typ pola - moje/przeciwnik
     * @param func numer funkcji
     */
    private void areaOperation(String type, int func){
        for(int i=0;i<5;i++){
            for(int j=0;j<9;j++){
                int id = getResources().getIdentifier(type+"_"+i+"_"+j, "id", getPackageName());
                Button b = findViewById(id);

                int x = i;
                int y = j;

                switch(func){
                    case 1:
                        b.setOnClickListener(view -> selectSchip(x, y, b));
                        break;
                    case 2:
                        b.setOnClickListener(view -> Shoot(x, y, b));
                        break;
                    case 3:
                        b.setBackgroundColor(Color.rgb(98, 0, 238));
                        break;
                }
            }
        }
    }

    /**
     * Funkcja startująca rozgrywkę, wykonuje się po kliknięciu przycisku START
     */
    private void Start(){
        if(mojeStatki.size() < 5){
            popup("Zaznacz conajmniej 5 statków");
        }else{
            getComputerSchips();
            start = true;

            startButton.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Wybór pozycji statku
     * @param x
     * @param y
     * @param b
     */
    private void selectSchip(int x, int y, Button b){
        if(start == false) {
            if(!mojeStatki.contains(x+":"+y)){
                if(mojeStatki.size() < 6){
                    mojeStatki.add(x+":"+y);
                    b.setBackgroundColor(Color.GREEN);
                }
            }else{
                mojeStatki.remove(x+":"+y);
                b.setBackgroundColor(Color.rgb(98, 0, 238));
            }
        }
    }

    /**
     * Wygenerowanie statków przeciwnika
     */
    private void getComputerSchips(){
        while(statkiPrzeciwnika.size() < mojeStatki.size()){
            int x = (int)Math.floor(Math.random() * 5);
            int y = (int)Math.floor(Math.random() * 9);

            if(!statkiPrzeciwnika.contains(x+":"+y)){
                statkiPrzeciwnika.add(x+":"+y);
            }
        }
    }

    /**
     * Funkcja wywoływana po kliknięciu w pole przeciwnika - strzał w pole przeciwnika
     * @param x
     * @param y
     * @param b
     */
    private void Shoot(int x, int y, Button b){
        if(start == true && turn == true && !mojeUsed.contains(x+":"+y)){
            if(statkiPrzeciwnika.contains(x+":"+y)){
                b.setBackgroundColor(Color.RED);
                mojeZbite.add(x+":"+y);

                if(mojeZbite.size() == statkiPrzeciwnika.size()){
                    win();
                    return;
                }
            }else{
                b.setBackgroundColor(Color.DKGRAY);
                turn = false;
                computerShoot();
            }
            mojeUsed.add(x+":"+y);
        }
    }

    /**
     * Strzał przeciwnika (generuje losowe pole i z niego korzysta)
     */
    private void computerShoot(){
        if(computerZbite.size() == mojeStatki.size()){
            lose();
            return;
        }

        int x = (int)Math.floor(Math.random() * 5);
        int y = (int)Math.floor(Math.random() * 9);

        if(!computerUsed.contains(x+":"+y)){
            int id = getResources().getIdentifier("moje_"+x+"_"+y, "id", getPackageName());
            Button b = findViewById(id);
            if(mojeStatki.contains(x+":"+y)){
                b.setBackgroundColor(Color.RED);
                computerZbite.add(x+":"+y);
                computerShoot();
            }else{
                b.setBackgroundColor(Color.DKGRAY);
                turn = true;
            }
            computerUsed.add(x+":"+y);
        }else{
            computerShoot();
        }
    }

    private void lose(){
        restartButton.setVisibility(View.VISIBLE);
        popup("Przegrałeś");
    }

    private void win(){
        restartButton.setVisibility(View.VISIBLE);
        popup("Wygrałeś");
        turn = false;
    }

    /**
     * Wyświetlenie okienka (popup) z podaną informacją
     * @param text
     */
    private void popup(String text){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = inflater.inflate(R.layout.popup, null);
        View view = inflater.inflate(R.layout.activity_main, null);

        TextView popupText = popupView.findViewById(R.id.popupText);
        popupText.setText(text);

        int width = LinearLayout.LayoutParams.WRAP_CONTENT;
        int height = LinearLayout.LayoutParams.WRAP_CONTENT;
        boolean focusable = true;

        final PopupWindow popup = new PopupWindow(popupView, width, height, focusable);

        popup.showAtLocation(view, Gravity.CENTER, 0, 0);

        popupView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent e){
                popup.dismiss();
                return true;
            }
        });
    }

    /**
     * Funkcja do restartu gry, zmienia kolor wszystkich przycisków na podstawowy,
     * czyści wszystkie tablice, ustawia zmienne w domyślny stan, pokazuje i ukrywa przyciski od startu i restartu
     */
    private void restartGame(){
        start = false;
        turn = true;

        mojeStatki.clear();
        mojeZbite.clear();
        mojeUsed.clear();
        statkiPrzeciwnika.clear();
        computerZbite.clear();
        computerUsed.clear();

        /**
         * Ustawienie koloru wszystkich przycisków z mojego pola i pola przeciwnika na kolor domyślny
         */
        areaOperation("moje", 3);
        areaOperation("przeciwnik", 3);

        restartButton.setVisibility(View.INVISIBLE);
        startButton.setVisibility(View.VISIBLE);
    }
}