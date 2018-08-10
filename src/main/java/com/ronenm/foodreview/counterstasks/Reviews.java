package com.ronenm.foodreview.counterstasks;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.sql.*;
import org.springframework.util.StopWatch;

import java.util.Arrays;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.desc;


public class Reviews {

    public void doCounts(){
        StopWatch stopWatch = new StopWatch("Counters tasks timing");
        Logger.getLogger("org").setLevel(Level.ERROR);
        SparkSession session = SparkSession.builder().appName("StackOverFlowSurvey").master("local[1]").getOrCreate();

        DataFrameReader dataFrameReader = session.read();

        Dataset<Row> responses = dataFrameReader.option("header", "true").csv("in/Reviews.csv");

        System.out.println("=== Print out schema ===");
        responses.printSchema();

        System.out.println("=== Print 20 records of responses table ===");
        responses.show(20);


        stopWatch.start("1000 most active users");
        System.out.println("=== 1000 most active users  ===");
        responses.groupBy(col("ProfileName")).count().orderBy(desc("count")).limit(1000).show(1000);
        stopWatch.stop();


        stopWatch.start("1000 most commented food items");
        System.out.println("=== 1000 most commented food items  ===");
        responses.groupBy(col("ProductId")).count().orderBy(desc("count")).limit(1000).show(1000);
        stopWatch.stop();


        stopWatch.start("1000 most used words in the reviews");
        System.out.println("=== 1000 most used words in the reviews  ===");
        Dataset<Row> textDS = responses.select("Text");

        Dataset<String> words = textDS.flatMap(row -> {
            return Arrays.asList(
                    row.mkString()
                            .toUpperCase()
                            .replace("\"", "")
                            .split(" "))
                    .iterator();
        }, Encoders.STRING())
                .filter(s -> !s.isEmpty())
                .coalesce(1);

        words.groupBy("value").count()
                .toDF("word", "count")
                .orderBy(desc("count"))
                .limit(1000)
                .orderBy("word")
                .show(1000);
        stopWatch.stop();

        System.out.println(stopWatch.prettyPrint());

    }
}
