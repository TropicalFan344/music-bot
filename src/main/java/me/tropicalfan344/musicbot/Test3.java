package me.tropicalfan344.musicbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Test3 {

    public static void main(String[] args) throws IOException {
        JsonObject out = new JsonObject();
        JsonObject test = new JsonObject();
        File saves = new File("saves");
        if(!saves.exists()) saves.mkdir();
        JsonArray array = new JsonArray();
        JsonObject arrayobjecttest = new JsonObject();
        File outputFile = new File("saves/output.json");
        Gson process = new GsonBuilder().setPrettyPrinting().create();
        FileOutputStream outputStream = new FileOutputStream(outputFile);

//        arrayobjecttest.addProperty("arrayobject", "test");
//        test.addProperty("videoId", "test");
//        array.add("arraytest");
//        array.add(arrayobjecttest);
//        test.add("array", array);
//        out.add("video", test);
//        out.addProperty("feuhf", "fwiehf");
        arrayobjecttest.addProperty("dwudhwu", "dygwyd");
        arrayobjecttest.addProperty("dwudhwfewu", "dygwyd");
        arrayobjecttest.addProperty("dwudwfewefhwu", "dygwyd");
        arrayobjecttest.addProperty("dwudwefwefwefhwu", "dygwyd");
        arrayobjecttest.addProperty("dwudfewfefwefwefwefhwu", "dygwyd");
        arrayobjecttest.addProperty("dwufewfwefwefewfwefewfwdhwu", "dygwyd");
        array.add(arrayobjecttest);
        array.add(arrayobjecttest);
        array.add(arrayobjecttest);
        array.add(arrayobjecttest);
        array.add(arrayobjecttest);
        out.add("test", array);

        String output = process.toJson(out);

        outputFile.createNewFile();

        outputStream.write(output.getBytes(StandardCharsets.UTF_8));
        outputStream.close();

//        FileReader reader = new FileReader(outputFile);
//        JsonObject input = process.fromJson(reader, JsonObject.class);
//        String videoId = input.getAsJsonObject().getAsJsonObject("video").get("videoId").getAsString();
//        for (String s : saves.list()) {
//            System.out.println(s);
//        }
//        System.out.println("souted: " + videoId);
    }

}
