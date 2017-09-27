package com.seoulapp.sandfox.retax;

/**
 * Created by user on 2016-10-12.
 */

public class Constants {
    public static final String ACTION_INIT = "com.seoulapp.sandfox.retax.INIT";
    public static final String ACTION_REFRESH = "com.seoulapp.sandfox.retax.REFRESH";
    public static final String EXTRA_STATUS = "status";
    public static final int STATUS_NOCONNECTIVITY = 2;
    public static final int STATUS_INPROGRESS = 0;
    public static final int STATUS_SUCCESS = 1;
    public static final int STATUS_FAILED = -1;

    public static final int THUMBNAIL = 0;
    public static final int NORMAL_IMG = 1;

    /* Firebase Database Constants*/
    //Root 유입경로
    public static final String INFO_WINDOW = "info";
    public static final String CLUSTER_DIALOG = "cluster_dialog";
    public static final String SEARCH_SUGGEST = "search_suggest";
    public static final String SEARCH_ACTIVITY = "search_activity";

    //type : Market? Refund? == market은 즉시 환급. refund는 사후 환급
    public static final String IMMEDIATE_REFUND = "immediate";
    public static final String DELAYED_REFUND = "delayed";

}
