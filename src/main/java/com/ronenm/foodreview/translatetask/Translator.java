package com.ronenm.foodreview.translatetask;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.stubbing.StubMapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@Component
public class Translator {

    @Value("${reviews.file}")
    private String fileName;

    @Value("${max.parallel.translations}")
    private String parallelTranslationProp;
    private Integer parallelTranslation = 0;
    private WireMockServer wireMockServer;


    @PostConstruct
    public void init() {
        parallelTranslation = Integer.valueOf(parallelTranslationProp);
        wireMockServer = new WireMockServer();
        wireMockServer.start();
    }

    @PreDestroy
    public void destory() {
        wireMockServer.stop();
    }


    public void doTranslate() {

        try (
                //Count number of reviews
                Stream<String> lines = Files.lines(Paths.get(fileName), Charset.forName("UTF-8"))) {
            long numOfLines = lines.count();
            lines.close();
            System.out.println("Total lines to translate: " + numOfLines);

            StopWatch stopWatch = new StopWatch(parallelTranslation + " reviews chunks timer");
            // Read lines and translate in parallel
            for (int i = 0; i < numOfLines; i = i + parallelTranslation) {
                List<String> list = getLines(parallelTranslation, i);
                stopWatch.start("Reviews " + i + " to " + i + parallelTranslation);
                ForkJoinPool myPool = new ForkJoinPool(parallelTranslation);
                myPool.submit(() ->
                        list.parallelStream().forEach(line ->
                                {
                                    try {
                                        String[] arrText = line.split("\\s*,\\s*", -1);
                                        StringBuffer textBuffer= new StringBuffer(arrText[9]);
                                        for(int x=10;x<arrText.length;x++){
                                            textBuffer.append(arrText[x]) ;
                                        }
                                        String translatedText = callTranslateService("https://api.google.com/translate", "en", "fr", textBuffer.toString());
                                        arrText[9] = translatedText;
                                        textBuffer= new StringBuffer();
                                        for(int x=0;x<10;x++){
                                            textBuffer.append(arrText[x]).append(",");
                                        }
                                        System.out.println( textBuffer.toString());
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                        )).get();
                stopWatch.stop();
            }
            wireMockServer.stop();
            System.out.println(stopWatch.prettyPrint());

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


    private List<String> getLines(int lineCount, long offsetLine) {
        List<String> list = new ArrayList<>();

        System.out.println("Starting from line " + offsetLine);
        try (Stream<String> stream = Files.lines(Paths.get(fileName), Charset.forName("UTF-8"))) {

            list = stream
                    .skip(offsetLine)
                    .limit(lineCount)
                    .collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


    private String callTranslateService(String url, String inputLang, String outputLang, String text) throws InterruptedException {

        String response = "Salut Jean, comment vas tu?";
        StubMapping responseValid = stubFor(post(urlEqualTo(url))
                .withQueryParam("input_lang", equalTo(inputLang))
                .withQueryParam("output_lang", equalTo(outputLang))
                .withQueryParam("text", equalTo(text))
                .withHeader("Content-Type", equalTo("application/json"))
                .willReturn(aResponse().withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(response)));
        Thread.sleep(200);
        return responseValid.getResponse().getBody();
    }

}