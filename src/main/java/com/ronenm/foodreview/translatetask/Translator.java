package com.ronenm.foodreview.translatetask;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class translator {

static String FILE_NAME ="in/Reviews.csv";

    private static List getLines(int lineCount, long startLine){
        List<String> list = new ArrayList<>();

        System.out.println("Startin from line " + startLine);
        try (Stream<String> stream = Files.lines(Paths.get(FILE_NAME))) {

            list = stream
                    .skip(startLine)
                    .limit(lineCount)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }



    public static void main(String[] args) {

        try (

            Stream<String> lines = Files.lines(Paths.get(FILE_NAME),Charset.defaultCharset())) {
            long numOfLines = lines.count();
            System.out.println(numOfLines);


            for (int i = 568445; i < numOfLines; i=i+1000) {
                List<String> list = translator.getLines(1000,i);
                list.parallelStream().forEach(line -> System.out.println(line));
            }



        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

}
