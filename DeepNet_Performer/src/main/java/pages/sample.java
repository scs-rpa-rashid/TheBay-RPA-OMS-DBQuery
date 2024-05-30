package pages;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.openqa.selenium.json.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class sample {

        public static void main(String[] args) {
            Date currentDate = new Date();

            // Define the desired date format
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");

            // Format the date into a string
            String formattedDate = dateFormat.format(currentDate);

            // Print the formatted date
            System.out.println("Formatted Date: " + formattedDate);
        }
    }


