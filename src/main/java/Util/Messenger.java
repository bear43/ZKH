package Util;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;

public class Messenger
{
    private static final String ACCOUNT_SID = "AC2da55c794b6530b41d1ae312b3d9b543";

    private static final String AUTH_TOKEN = "ad1d2dd468ce31a96c86d762c1e57371";

    private static final String TWILIO_NUMBER = "+12673624770";

    private static boolean inited;

    private static void checkInited() throws Exception
    {
        if(!inited) throw new Exception("Twilio has been not inited!");
    }

    public static void init()
    {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        inited = true;
    }

    public static MessageCreator createMessageCreator(String recipientNumber, String message) throws Exception
    {
        checkInited();
        return Message.creator(
                new PhoneNumber(recipientNumber),
                new PhoneNumber(TWILIO_NUMBER),
                message);
    }

    public static Message sendMessage(String recipientNumber, String message) throws Exception
    {
        return createMessageCreator(recipientNumber, message).create();
    }
}
