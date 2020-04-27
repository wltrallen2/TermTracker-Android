package com.fortysomethingnerd.android.termtracker.utilities;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;

public class FormattedText {

    public static Spanned getHTMLText(String boldMoniker, String normalText) {
        String textWithFormatters = "<b>" + boldMoniker + "</b>" + normalText;

        /* TODO: This is not the most accepted way to handle this code. For more elegant code, I need to
         * rewrite so that I have two custom classes, one with the deprecated method, and one with the
         * updated method.
         */
        Spanned returnText;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // fromHtml(String s, int flags) was added in API level 24
            returnText = Html.fromHtml(textWithFormatters, 0);
        } else {
            // fromHtml(String s) was deprecated in API level 24
            returnText = Html.fromHtml(textWithFormatters);
        }

        return returnText;
    }

}
