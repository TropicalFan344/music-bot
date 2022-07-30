package me.tropicalfan344.musicbot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import sun.java2d.opengl.WGLSurfaceData;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Test3 {

    public static void main(String[] args) throws IOException {
        JsonObject out = new JsonObject();
        JsonObject test = new JsonObject();
        JsonArray array = new JsonArray();
        JsonObject arrayobjecttest = new JsonObject();
        arrayobjecttest.addProperty("arrayobject", "test");
        test.addProperty("videoId", "test");
        array.add("arraytest");
        array.add(arrayobjecttest);
        test.add("array", array);
        out.add("video", test);
        Gson process = new GsonBuilder().setPrettyPrinting().create();
        String output = process.toJson(out);
        File outputFile = new File("saves/output.json");
        outputFile.createNewFile();
        FileOutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(output.getBytes(StandardCharsets.UTF_8));
        outputStream.close();

        File saves = new File("saves");
        FileReader reader = new FileReader(outputFile);
        JsonObject input = process.fromJson(reader, JsonObject.class);
        String videoId = input.getAsJsonObject().getAsJsonObject("video").get("videoId").getAsString();
        for (String s : saves.list()) {
            System.out.println(s);
        }
        System.out.println("souted: " + videoId);
    }

}
