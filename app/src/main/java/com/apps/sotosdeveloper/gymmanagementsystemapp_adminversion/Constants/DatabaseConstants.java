package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Constants;

public interface DatabaseConstants {

    // all collection names
    String FIREBASE_COLLECTION_RECEIPTS             = "receipts";
    String FIREBASE_COLLECTION_OPENING_HOURS        = "gym_opening_hours";
    String FIREBASE_COLLECTION_USERS                = "users";
    String FIREBASE_COLLECTION_MESSAGES             = "messages";

    // for receipts
    String FIREBASE_DOCUMENT_RECEIPT_ID             = "RECEIPT_ID";
    String FIREBASE_DOCUMENT_RECEIPT_DATE           = "DATE";
    String FIREBASE_DOCUMENT_RECEIPT_TO_USER        = "TO_USER";
    String FIREBASE_DOCUMENT_RECEIPT_PAID           = "PAID";
    String FIREBASE_DOCUMENT_RECEIPT_AMOUNT         = "AMOUNT";
    String FIREBASE_DOCUMENT_RECEIPT_MODE           = "MODE";

    String FIREBASE_DOCUMENT_RECEIPT_MODE_SINGLE    = "SINGLE";
    String FIREBASE_DOCUMENT_RECEIPT_MODE_MONTHLY   = "MONTHLY";
    String FIREBASE_DOCUMENT_RECEIPT_MODE_ANNUALLY  = "ANNUALLY";


    // for users
    String FIREBASE_DOCUMENT_USERS_EMAIL            = "EMAIL";
    String FIREBASE_DOCUMENT_USERS_FIRST_NAME       = "FIRST_NAME";
    String FIREBASE_DOCUMENT_USERS_LAST_NAME        = "LAST_NAME";
    String FIREBASE_DOCUMENT_USERS_PASSWORD         = "PASSWORD";
    String FIREBASE_DOCUMENT_USERS_TYPE             = "TYPE";

    // for messages
    String FIREBASE_MESSAGES_OPTION_ALL             = "to_everyone";
    String FIREBASE_DOCUMENT_MESSAGES_TITLE         = "TITLE";
    String FIREBASE_DOCUMENT_MESSAGES_SUMMARY       = "SUMMARY";
    String FIREBASE_DOCUMENT_MESSAGES_TO_USER       = "TO_USER";
    String FIREBASE_DOCUMENT_MESSAGE_DATE           = "DATE";
    String FIREBASE_DOCUMENT_MESSAGE_IS_IMPORTANT   = "IS_IMPORTANT";
    String FIREBASE_DOCUMENT_MESSAGE_ID             = "MESSAGE_ID";

    String FIREBASE_USER_TYPE_1                     = "CLIENT";
    String FIREBASE_USER_TYPE_2                     = "ADMIN";


    // for opening hours
    String FIREBASE_DOCUMENT_OP_HOURS_MONDAY        = "Monday";
    String FIREBASE_DOCUMENT_OP_HOURS_TUESDAY       = "Tuesday";
    String FIREBASE_DOCUMENT_OP_HOURS_WEDNESDAY     = "Wednesday";
    String FIREBASE_DOCUMENT_OP_HOURS_THURSDAY       = "Thursday";
    String FIREBASE_DOCUMENT_OP_HOURS_FRIDAY        = "Friday";
    String FIREBASE_DOCUMENT_OP_HOURS_SATURDAY      = "Saturday";
    String FIREBASE_DOCUMENT_OP_HOURS_SUNDAY        = "Sunday";

    String FIREBASE_DOCUMENT_OP_HOURS_OPEN          = "Open";
    String FIREBASE_DOCUMENT_OP_HOURS_CLOSED        = "Close";
    String FIREBASE_DOCUMENT_OP_HOURS_DAY_OFF       = "Day_off";

}
