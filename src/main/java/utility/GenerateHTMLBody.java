package utility;

import java.util.Map;

public class GenerateHTMLBody{

    String htmlText;
    public String generateHtmlBody(Map<String,String> mapData) throws Exception
    {
        htmlText=Constants.HTML_TABLEHEADER;
        for (Map.Entry<String,String> map : mapData.entrySet())
        {

            htmlText = htmlText + "<tr align='left'>"
                    + "<td style='border: 2px solid black; border-collapse: collapse; width: auto;'>" + map.getKey() + "</td>"
                    + "<td style='border: 2px solid black; border-collapse: collapse; width: auto;'>" + map.getValue() + "</td>"
                    + "</tr>";
        }

        return htmlText;

    }
}
