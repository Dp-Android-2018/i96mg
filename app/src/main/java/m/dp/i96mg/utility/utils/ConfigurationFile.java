package m.dp.i96mg.utility.utils;

import android.text.format.DateFormat;

import java.util.Date;

public class ConfigurationFile {

    public static class UrlConstants {
        public static final String BASE_URL = "http://www.i96mg.com";
//        public static final String BASE_URL = "http://151.106.52.107:3030";
//        public static final String BASE_URL = "http://151.106.13.61:80";

    }

    public static class Constants {

        public static final String API_KEY = "49182-7180383-9e24-4b94-27e37ec19c94";
        public static final String CONTENT_TYPE = "application/json";
        public static final String ACCEPT = "application/json";
        public static String ACCEPT_LANGUAGE = "ar";
        public static String AUTHORIZATION = "token";
        public static final String BEARER_STRING = "Bearer ";


        public static String REGISTER1DATA = "REGISTER1Data";
        public static int DEFAULT_INTEGER_VALUE = 0;
        public static int WAIT_VALUE = 2000;

        public static final String ACCEPT_LANGUAGE_ARABIC = "ar";
        public static final String ACCEPT_LANGUAGE_ENGLISH = "en";
        public static final String ACCEPT_LANGUAGE_FRENCH = "fr";
        public static final String DEFAULT_LANGUAGE_STRING = "0";
        public static final String ENGLISH_LANGUAGE_STRING = "English";
        public static final String ARABIC_LANGUAGE_STRING = "العربية";
        public static final String FRENCH_LANGUAGE_STRING = "français";

        public static final String MARKET_URL = "market://details?id=";
        public static final String PLAYSTORE_URL = "http://play.google.com/store/apps/details?id=";

        static final String name = DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
        public static final String SERVICEPROVIDER_DIRECTORY_NAME = name;

        public static final String PAYMENT_METHOD = "COD";
        public static final String COUNTRY_ID = "country id";
        public static final String DATE_FROM = "from";
        public static final String DATE_TO = "DATA";
        public static final String REQUEST_ID = "request id";
        public static final String REQUEST_TYPE = "request type";
        public static final String FROM_REQUESTS_TYPE = "request";
        public static final String TRIPS_TYPE_PAST = "past";
        public static final String TRIPS_TYPE_UPCOMING = "upcoming";
        public static final String OFFERS_TYPE_ACTIVITY = "offer activity";
        public static final String OFFER_PRICE = "offer price";
        public static final String TRIPS_DATA = "trips data";
        public static final String REQUEST_DATA = "request data";
        public static final String ACTIVE_REQUEST_DATA = "active request data";
        public static final String FIREBASE_REQUEST_DATA = "firebase request data";
        public static final String START_TRIP_TYPE = "trip from";
        public static final String FROM_REQUEST_DETAILS = "from request details";


        public static final String CAR_TYPE = "car";
        public static final String ON_FOOT_TYPE = "onfoot";
        public static final String MOTORBIKE_TYPE = "motorcycle";
        public static final String VAN_TYPE = "van";
        public static final String TUKTUK_TYPE = "tuk_tuk";
        public static final String STEAGECOACH_TYPE = "stage_coach";
        public static final String YACHAT_TYPE = "yacht";
        public static final String GOLFCAR_TYPE = "golf_car";
        public static final String JETSKI_TYPE = "jet_ski";

        public static final int CAR_ID = 2;
        public static final int BIKE_ID = 3;
        public static final int VAN_ID = 4;
        public static final int TUKTUK_ID = 5;
        public static final int STEAGECOACH_ID = 6;
        public static final int ONFOOT_ID = 7;
        public static final int YACHAT_ID = 8;
        public static final int JETSKI_ID = 9;
        public static final int GOLFCAR_ID = 10;

        public static final String BRACKET_BEFORE = "(";
        public static final String BRACKET_AFTER = ")";
        public static final String PERCENT = "%";
        public static final String SPACE = " ";

        public static final String USER_DATA = "User data";
        public static final String TRIP_ID = "trip Id";
        public static final int VIEW_TYPE_MESSAGE_SENT=1;
        public static final int VIEW_TYPE_MESSAGE_RECEIVED=2;

        //links
        public static final String FACEBOOK_NAME = "Facebook";
        public static final String TWITTER_NAME = "Twitter";
        public static final String INSTAGRAM_NAME = "Instagram";
        public static final String WATSAPP_NAME = "watsapp";
        public static final String MAIL_NAME = "Mail";
        public static final String BROWSER_NAME = "Browser";
        public static final String FACEBOOK_PACKAGE_NAME = "com.facebook.katana";
        public static final String FACEBOOK_PACKAGE_NAME_URL = "fb://facewebmodal/f?href=";
        public static final String TWITTER_PACKAGE_NAME = "com.twitter.android";
        public static final String WATSAPP_PACKAGE_NAME = "com.whatsapp";
        public static final String INSTAGRAM_PACKAGE_NAME = "com.instagram.android";
        public static final String INTENT_MAIL_TYPE = "message/rfc822";
        public static final String MEDIA_TEXT_TYPE = "text/plain";

        //intents types
        public static final String ACTIVATION_TYPE = "Activation Type";
        public static final String LANGUAGE_TYPE = "language Type";
        public static final String PRODUCT_DETAIL = "product detail";
        public static final String PRODUCT_ID = "product id";
        public static final String Category_ID = "category id";
        public static final String ORDER_ID = "order id";
        public static final String PENDING_ORDERS_ACTIVITY = "pending orders activity";
        public static final String ORDERS_ACTIVITY = "orders activity";
        public static final int SALE_ID = -5;
        public static final String WISHLIST_TYPE = "wish list";
        public static final String DEFAULT_TYPE = "default";
        public static final String PRODUCTS_LIST = "products list";
        public static final String PRODUCTS_INFO = "products info";
        public static final String FAVORITE_PRODUCTS_LIST = "favorite products";
        public static final String ORDER_REQUEST = "order request";
        public static final String VOUCHER_VALUE = "voucher";
        public static final String ACTIVITY_NAME = "activity name";
        public static final String SHOP_DETAILS_ACTIVITY = "shop activity";
        public static final String PRODUCT_DETAILS_ACTIVITY = "product activity";
        public static final String MAIN_ACTIVITY = "main activity";
        public static final String EMAIL_EDITTEXT = "email";
        public static final String PASSWORD_EDITTEXT = "password";
        public static final String PRODUCTTYPE_EDITTEXT = "product type";
        public static final String WHATSAPP_EDITTEXT = "whatsapp";
        public static final String GOOGLE_MAPS_PACKAGE_NAME = "com.google.android.apps.maps";
        public static final String GOOGLE_MAPS_URI_DATA = "geo:0,0?q=";

        public static final int DEFAULT_PAGE_ID = 1;
        public static final int DEFAULT_QUANTITY = 1;
        public static final int CREDIT_ID = 3;
        public static final int BANK_ACCOUNT_ID = 6;
        public static final String CREDIT_CARD = "CREDIT_CARD";
        public static final int PAYBAL_ID = 5;
        public static final String PAYBAL = "PAYPAL";
        public static final String DEFAULT_CATEGORY_ID = "";
        public static final String KEY_SUCCESS = "success";
        public static final String KEY_TRUE = "true";
        public static final String FIRST_SEND = "1";
        public static final String SECOND_SEND = "2";

        public static final int SUCCESS_CODE_FROM = 200;
        public static final int SUCCESS_CODE_TO = 300;
        public static final int SUCCESS_CODE = 200;
        public static final int SUCCESS_CODE_SECOND = 201;
        public static final int SUCCESS_CODE_Third = 202;
        public static final int NOT_ACTIVATED_CODE = 417 ;
        public static final int LOGGED_IN_BEFORE_CODE = 401 ;
        public static final int WAIT_CODE = 429;
        public static final int INVALED_DATA_CODE = 422;
        public static final String DEFAULT_STRING_VALUE = "";


    }

    public static class SharedPrefConstants {
        public static final String SHARED_PREF_NAME = "I96MG_SHARED_PREF";
        public static final String NewDataToSave = "NewDataToSave";
    }

}
