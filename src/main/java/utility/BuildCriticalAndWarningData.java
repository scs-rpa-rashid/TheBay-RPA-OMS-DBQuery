package utility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
public class BuildCriticalAndWarningData {
    Map<String, Object> criticaldata = new HashMap<>();
    Map<String, Object> warningdata = new HashMap<>();
    public void buildCriticalAndWarningData(String [][] strarrayoutputdata) {
        for (int i = 1; i < Arrays.stream(strarrayoutputdata).count(); i++) {
            if (strarrayoutputdata[i][11].isBlank() || strarrayoutputdata[i][11].isEmpty()) {
                System.out.println("Result value is blank for Alert: " + strarrayoutputdata[i][1]);
            } else {
                if ((Integer.parseInt(strarrayoutputdata[i][11]) <= Integer.parseInt(strarrayoutputdata[i][5])) && (Integer.parseInt(strarrayoutputdata[i][11]) > Integer.parseInt(strarrayoutputdata[i][6]))) {
                    //Add Alert name to Warning table
                    warningdata.put(strarrayoutputdata[i][0],
                            strarrayoutputdata[i][11]);
                } else if ((Integer.parseInt(strarrayoutputdata[i][11]) <= Integer.parseInt(strarrayoutputdata[i][6]))) {
                    //Add Alert name to Warning table
                    criticaldata.put(strarrayoutputdata[i][0],
                            strarrayoutputdata[i][11]);
                }
            }
        }
    }
    public java.util.Map<String, Object> getCriticaldata() {
        return criticaldata;
    }
    public java.util.Map<String, Object> getWarningdata() {
        return warningdata;
    }
}


