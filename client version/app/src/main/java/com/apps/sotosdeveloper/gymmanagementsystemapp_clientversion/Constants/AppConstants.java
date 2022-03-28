package com.apps.sotosdeveloper.gymmanagementsystemapp_clientversion.Constants;

public interface AppConstants {

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

    //for App Internal Database
    String APP_PREFERENCE_EMAIL                         = "app_preference_email";
    String APP_PREFERENCE_HAS_LOGGED_IN                 = "app_preference_has_logged_in";
    String APP_PREFERENCE_USER_ID                       = "app_preference_user_id";

    // for main activity
    String APP_EXTRAS_USER_ID                           = "userID";

    // for preview message
    String PREVIEW_MESSAGE_EXTRAS_TITLE                 = "previewMessageTitle";
    String PREVIEW_MESSAGE_EXTRAS_BODY                  = "previewMessageBody";
    String PREVIEW_MESSAGE_EXTRAS_DATE                  = "previewMessageDate";
}
