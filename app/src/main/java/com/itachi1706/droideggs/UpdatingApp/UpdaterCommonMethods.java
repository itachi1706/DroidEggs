package com.itachi1706.droideggs.UpdatingApp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Kenneth on 7/10/2015.
 * for DroidEggs in package com.itachi1706.droideggs.UpdatingApp
 */
public class UpdaterCommonMethods {

    public static String getChangelogFromArrayList(ArrayList<String> changelogArray){
        /* Legend of Stuff
         * 1st Line - Current Version Code check
         * 2nd Line - Current Version Number
         * 3rd Line - Link to New Version
         * # - Changelog Version Number (Bold this)
         * * - Points
         * @ - Break Line
         */
        StringBuilder changelogStringBuilder = new StringBuilder();
        changelogStringBuilder.append("Latest Version: ").append(changelogArray.get(1))
                .append("-b").append(changelogArray.get(0)).append("<br /><br />");

        for (String changelog : changelogArray){
            if (changelog.startsWith("#"))
                changelogStringBuilder.append("<b>").append(changelog.replace('#', ' ')).append("</b><br />");
            else if (changelog.startsWith("*"))
                changelogStringBuilder.append(" - ").append(changelog.replace('*', ' ')).append("<br />");
            else if (changelog.startsWith("@"))
                changelogStringBuilder.append("<br />");
        }

        return changelogStringBuilder.toString();
    }

    public static String getChangelogFromArray(String[] changelog){
        ArrayList<String> changelogArrList = new ArrayList<>();
        Collections.addAll(changelogArrList, changelog);
        return getChangelogFromArrayList(changelogArrList);
    }

    public static String getFilePath(Context context){
        return context.getExternalFilesDir(null) + File.separator;
    }
}
