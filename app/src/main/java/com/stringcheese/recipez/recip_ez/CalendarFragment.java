package com.stringcheese.recipez.recip_ez;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Toast;
import android.view.Gravity;
import android.content.Context;
import android.graphics.Color;
import android.widget.CalendarView.OnDateChangeListener;

import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import static android.widget.Toast.LENGTH_LONG;


public class CalendarFragment extends Fragment implements View.OnClickListener{


    public CalendarFragment() {
        // Required empty public constructor ,
    }

    Button test;
    CalendarView cal;
    GregorianCalendar date, selectedDate;
    int i = 0;

    Button save, edit;


    int flag;

    File file;
    String filename;
    String meals;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_calendar, container, false);

        //meals = getArguments().getString("message");

        filename = "calendarrecipes.txt";

        cal = (CalendarView) v.findViewById(R.id.calendarView);

        FloatingActionButton addMealsButton = (FloatingActionButton) v.findViewById(R.id.cal_add);
        addMealsButton.setOnClickListener((View.OnClickListener) this);

        GregorianCalendar gregorianCalendar=new GregorianCalendar();
        String month=String.valueOf(gregorianCalendar.get(GregorianCalendar.MONTH));
        String day=String.valueOf(gregorianCalendar.get(GregorianCalendar.DAY_OF_MONTH));
        String year=String.valueOf(gregorianCalendar.get(GregorianCalendar.YEAR));

        new CalendarRecipes();

        int y = Integer.parseInt(year);
        int d = Integer.parseInt(day);
        int m = Integer.parseInt(month);
        date = new GregorianCalendar(y, m, d);

        flag = 0;

        file = getActivity().getFileStreamPath(filename);


        if(file.exists())
        {
            try
            {
                Scanner getLine = new Scanner(file);
                Scanner tokenizer;

                while (getLine.hasNextLine()) {
                    String line = getLine.nextLine();
                    tokenizer = new Scanner(line);
                    System.out.println(line);

                    while (tokenizer.hasNextInt()) {
                        int y1 = tokenizer.nextInt();
                        int m1 = tokenizer.nextInt();
                        int d1 = tokenizer.nextInt();

                        GregorianCalendar date = new GregorianCalendar(y1, m1, d1);

                        MealData data = new MealData();
                        while (tokenizer.hasNext()) {
                            String type = tokenizer.next();

                            if (type.equals("b")) {
                                String recipeName = "";

                                while (tokenizer.hasNext()) {
                                    String tmp = tokenizer.next();

                                    if (tmp.equals("l") || tmp.equals("d")) {
                                        type = tmp;
                                        break;
                                    }

                                    if (tmp.equals("|")) {
                                        data.setBreakfastItems(recipeName);
                                        CalendarRecipes.recipes.put(date, data);
                                        recipeName = "";
                                        continue;
                                    }

                                    recipeName += " " + tmp;
                                }
                            }

                            if (type.equals("l")) {
                                String recipeName = "";

                                while (tokenizer.hasNext()) {
                                    String tmp = tokenizer.next();

                                    if (tmp.equals("d")) {
                                        type = tmp;
                                        break;
                                    }

                                    if (tmp.equals("|")) {
                                        data.setLunchItems(recipeName);
                                        CalendarRecipes.recipes.put(date, data);
                                        recipeName = "";
                                        continue;
                                    }

                                    recipeName += " " + tmp;
                                }
                            }

                            if (type.equals("d")) {
                                String recipeName = "";

                                while (tokenizer.hasNext()) {
                                    String tmp = tokenizer.next();

                                    if (tmp.equals("|")) {
                                        data.setDinnerItems(recipeName);
                                        CalendarRecipes.recipes.put(date, data);
                                        recipeName = "";
                                        continue;
                                    }

                                    recipeName += " " + tmp;
                                }
                            }
                        }
                    }
                    tokenizer.close();
                }
                getLine.close();
            }

            catch(IOException e) {
                e.printStackTrace();
            }
        }
        else
        {
            file = new File(getActivity().getFilesDir(), filename);
        }



        cal.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                //Toast.makeText(getActivity(), "Date = " + year + " " + month + " " + dayOfMonth, Toast.LENGTH_SHORT).show();

                flag = 1;
                selectedDate = new GregorianCalendar(year, month, dayOfMonth);

                if(CalendarRecipes.recipes.get(selectedDate)!=null)
                {

                    //update the next field that need to be there


                }

                else {

                }
            }
        });
        // Inflate the layout for this fragment
        return v;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cal_add:
                Intent intent = new Intent(getActivity(), meals_display.class);
                startActivity(intent);
                break;
        }
    }




    private void writeToFile() {
        try
        {
            PrintWriter writer = new PrintWriter(file, "UTF-8");
            for (GregorianCalendar gregObject: CalendarRecipes.recipes.keySet()) {
                int bflag = 1, lflag = 1, dflag = 1;

                MealData values = CalendarRecipes.recipes.get(gregObject);

                int year = gregObject.get(Calendar.YEAR);
                int month = gregObject.get(Calendar.MONTH);
                int day = gregObject.get(Calendar.DAY_OF_MONTH);

                writer.printf("%d %d %d ", year, month, day);

                for (String b : values.getBreakfastItems()) {
                    if (bflag == 1) {
                        writer.printf("b ");
                        bflag = 0;
                    }

                    writer.printf("%s |", b);
                }

                for (String l : values.getBreakfastItems()) {
                    if (lflag == 1) {
                        writer.printf("l ");
                        lflag = 0;
                    }

                    writer.printf("%s |", l);
                }

                for (String d : values.getBreakfastItems()) {
                    if (dflag == 1) {
                        writer.printf("d ");
                        dflag = 0;
                    }

                    writer.printf("%s |", d);
                }

                writer.print("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
