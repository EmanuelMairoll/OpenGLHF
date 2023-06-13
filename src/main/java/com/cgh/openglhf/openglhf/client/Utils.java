package com.cgh.openglhf.openglhf.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Utils {

    public static String loadResource(String fileName) throws IOException {
        StringBuilder result = new StringBuilder();

        try (InputStream in = Utils.class.getResourceAsStream(fileName)) {
            if (in == null) {
                throw new IOException("Resource not found: " + fileName);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line).append("\n");
                }
            }
        }

        return result.toString();
    }

    public static float[] doublesToFloat(double[] array) {
        float[] inFloatForm = new float[array.length];
        for (int i = 0; i < array.length; i++)
            inFloatForm[i] = (float) array[i];
        return inFloatForm;
    }
}
