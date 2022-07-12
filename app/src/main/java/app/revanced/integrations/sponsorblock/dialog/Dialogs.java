package app.revanced.integrations.sponsorblock.dialog;

import static app.revanced.integrations.sponsorblock.StringRef.str;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.LightingColorFilter;
import android.net.Uri;
import android.os.Build;

import app.revanced.integrations.settings.SettingsEnum;
import app.revanced.integrations.utils.ReVancedUtils;
import app.revanced.integrations.utils.SharedPrefHelper;

public class Dialogs {
    // Inject call from YT to this
    public static void showDialogsAtStartup(Activity activity) {
        sbFirstRun(activity);
    }

    private static void sbFirstRun(Activity activity) {
        Context context = ReVancedUtils.getContext();
        boolean enabled = SettingsEnum.SB_ENABLED_BOOLEAN.getBoolean();
        boolean hintShown = SettingsEnum.SB_SPONSOR_BLOCK_HINT_SHOWN_BOOLEAN.getBoolean();

        // If SB is enabled or hint has been shown, exit
        if (enabled || hintShown) {
            // If SB is enabled but hint hasn't been shown, mark it as shown
            if (enabled && !hintShown) {
                SettingsEnum.SB_SPONSOR_BLOCK_HINT_SHOWN_BOOLEAN.saveValue(true);
            }
            return;
        }

        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(activity);
        }
        builder.setTitle(str("vanced_sb"));
        builder.setIcon(ReVancedUtils.getIdentifier("ic_sb_logo", "drawable"));
        builder.setCancelable(false);
        builder.setMessage(str("vanced_sb_firstrun"));
        builder.setPositiveButton(str("vanced_enable"),
                (dialog, id) -> {
                    SettingsEnum.SB_SPONSOR_BLOCK_HINT_SHOWN_BOOLEAN.saveValue(true);
                    SettingsEnum.SB_ENABLED_BOOLEAN.saveValue(true);
                    dialog.dismiss();
                });

        builder.setNegativeButton(str("vanced_disable"),
                (dialog, id) -> {
                    SettingsEnum.SB_SPONSOR_BLOCK_HINT_SHOWN_BOOLEAN.saveValue(true);
                    SettingsEnum.SB_ENABLED_BOOLEAN.saveValue(false);
                    dialog.dismiss();
                });

        builder.setNeutralButton(str("vanced_learnmore"), null);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Set black background
        dialog.getWindow().getDecorView().getBackground().setColorFilter(new LightingColorFilter(0xFF000000, ReVancedUtils.getIdentifier("ytBrandBackgroundSolid", "color")));

        // Set learn more action (set here so clicking it doesn't dismiss the dialog)
        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(v -> {
            Uri uri = Uri.parse("https://sponsor.ajay.app/");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            activity.startActivity(intent);
        });
    }
}