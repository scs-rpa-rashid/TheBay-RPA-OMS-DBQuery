package bots;

import utils.SystemException;
import utils.UtilClass;

import java.io.IOException;


public class UpdateEmailInDB {
    public static void main(String[] args) throws Exception {
       UtilClass.insertEmailEntryToDB();
    }
}
