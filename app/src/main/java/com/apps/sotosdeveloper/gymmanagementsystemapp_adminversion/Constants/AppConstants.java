package com.apps.sotosdeveloper.gymmanagementsystemapp_adminversion.Constants;

public interface AppConstants {

    int ADD_RECEIPT_ACTIVITY_REQUEST_CODE = 100;
    int ADD_MESSAGE_ACTIVITY_REQUEST_CODE = 101;

    // for Add receipt activity
    String ADD_RECEIPT_ACTIVITY_RESULT_OK               = "receipt_created";
    String ADD_RECEIPT_ACTIVITY_RESULT_ERROR            = "receipt_error_on_creation";
    String ADD_RECEIPT_ACTIVITY_RESULT_USER_CANCELED    = "receipt_error_user_interrupt";


    // for Add message activity
    String ADD_MESSAGE_ACTIVITY_RESULT_OK               = "message_created";
    String ADD_MESSAGE_ACTIVITY_RESULT_ERROR            = "message_error_on_creation";
    String ADD_MESSAGE_ACTIVITY_RESULT_USER_CANCELED    = "message_error_user_interrupt";

    // for PreviewMessage activity
    String PREVIEW_MESSAGE_ACTIVITY_MESSAGE_ID_EXTRAS   = "message_id";
    String PREVIEW_MESSAGE_EXTRAS_TITLE                 = "message_title";
    String PREVIEW_MESSAGE_EXTRAS_BODY                  = "message_body";
    String PREVIEW_MESSAGE_EXTRAS_DATE                  = "message_date";

    //for App Internal Database
    String APP_PREFERENCE_EMAIL                         = "app_preference_email";
    String APP_PREFERENCE_HAS_LOGGED_IN                 = "app_preference_has_logged_in";
}
