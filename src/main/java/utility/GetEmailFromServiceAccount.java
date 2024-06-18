package utility;

import exceptionutil.ApplicationException;
import logutil.Log;

import javax.mail.*;
import java.io.IOException;
import java.util.*;
import javax.mail.internet.InternetAddress;
import javax.mail.search.*;
import java.util.regex.*;
public class GetEmailFromServiceAccount {
     String strOtp;
     String strExceptionMessage;
    Session session;
    Store store;
    Folder inboxFolder;
    SearchTerm sender;
    Flags seen;
    FlagTerm unseenFlagTerm;
    Message[] messages;

    SearchTerm searchTerm;
    ArrayList<Message> listmessage;
    Pattern regexpattern;
    Matcher matchregex;
    public String GetMailOTP(String partialOtp) throws Exception {
         session = Session.getDefaultInstance(new Properties());
        try {
            store = session.getStore(Constants.MAIL_PROTOCOL);
            store.connect(Constants.IMAP_SERVER,  Constants.IMAP_PORT, Constants.IMAP_USERNAME,Constants.IMAP_PASSWORD);
            inboxFolder = store.getFolder(Constants.MAILBOX_FOLDER);
            System.out.println(inboxFolder.getUnreadMessageCount());
            inboxFolder.open(Folder.READ_WRITE);
            sender = new FromTerm(new InternetAddress(Constants.SENDER_MAILADDRESS));
            //fetch only unread messages
            seen = new Flags(Flags.Flag.SEEN);
             unseenFlagTerm = new FlagTerm(seen, false);
            //searching sender
            //combine search term
             searchTerm = new AndTerm(unseenFlagTerm, sender);
           messages = inboxFolder.search(searchTerm);
            Log.info("Getting OTP mail...");
            Log.info("Email count: "+messages.length);

           listmessage = new ArrayList<Message>();
            // Reverse the array so that we get latest unread emails
            Collections.reverse(Arrays.asList(messages));
            //Get the top 30 emails
            for(int i=0;i<=30;i++) {
                regexpattern =Pattern.compile(partialOtp+"[0-9]{6}");
                matchregex =regexpattern.matcher(messages[i].getContent().toString());
                if (messages[i].getSubject().contains(Constants.OTP_MAIL_SUBJECT)) {
                    Log.info("Mail body: "+messages[i].getContent().toString());
                    //System.out.println(messages[i].getContent());
                    messages[i].setFlag(Flags.Flag.SEEN, true);
                    listmessage.add(messages[i]);
                }

                if (matchregex.find()) {

                    strOtp =matchregex.group(0);
                    Log.info("OTP captured from mail body: "+ strOtp);
                    break;
                }
            }

      }
        catch (Exception e) {
            strExceptionMessage="Failure in getting OTP mail. Exception message: "+e.getMessage()+'\n'+"Exception source: "+e;
            Log.error(strExceptionMessage);
            throw new ApplicationException(strExceptionMessage);
        }
        return strOtp;
    }
}







