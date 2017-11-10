package com.example.rfaria.backgrounddata;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ListAppAdapter extends ArrayAdapter<AppsInfo> {

    private Context context = null;
    private List<AppsInfo> listApp = null;
    private PackageManager packageManager;
    private ViewHolder holder;
    private View rowView;
    private AppsInfo appsSync;
    private int positionItem;
    private boolean itemClick;

    public ListAppAdapter(Context context, List<AppsInfo> list) {
        super(context, R.layout.list_app, list);

        this.context = context;
        this.listApp = list;
        packageManager = context.getPackageManager();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            rowView = inflater.inflate(R.layout.list_app, parent, false);

            holder = new ViewHolder();
            holder.textView = (TextView) rowView.findViewById(R.id.nameApp);
            holder.pkgView = (TextView) rowView.findViewById(R.id.namePkg);
            holder.imageView = (ImageView) rowView.findViewById(R.id.iconApp);
            holder.hPosition = position;

            rowView.setTag(holder);
        } else {
            holder = (ViewHolder) rowView.getTag();
        }

        appsSync = getItem(position);

        if (appsSync != null) {

            final ApplicationInfo applicationInfo = appsSync.getApplicationInfo();

            holder.textView.setText(applicationInfo.loadLabel(packageManager));
            holder.pkgView.setText(applicationInfo.packageName);
            holder.imageView.setImageDrawable(applicationInfo.loadIcon(packageManager));

            holder.icon_1 = (ImageButton) rowView.findViewById(R.id.icon_1);
            holder.icon_1.setTag(position);
            holder.icon_1.setOnClickListener(iconListener);

            holder.icon_2 = (ImageButton) rowView.findViewById(R.id.icon_2);
            holder.icon_2.setTag(position);
            holder.icon_2.setOnClickListener(iconListener);

        }

        return rowView;
    }

    @Override
    public AppsInfo getItem(int position) {
        return listApp.get(position);
    }

    @Override
    public int getCount() {
        return listApp.size();
    }

    View.OnClickListener iconListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int pos = (Integer) v.getTag();
            String pkg = listApp.get(pos).getPakage();

            if (R.id.icon_1 == v.getId()) {
                openSettings(android.provider.Settings.
                        ACTION_APPLICATION_DETAILS_SETTINGS, pkg);
            } else {
                openSettings(android.provider.Settings.
                        ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS, pkg);
            }
        }
    };

    public static class ViewHolder {
        private ImageView imageView;
        private TextView textView;
        private TextView pkgView;
        private ImageButton icon_1;
        private ImageButton icon_2;
        int hPosition;
    }

    public void openSettings(String action, String pkg) {
        Intent intent = new Intent(action);
        intent.setData(Uri.parse("package:"+ pkg));
        try {
            this.context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            if (android.provider.Settings.ACTION_IGNORE_BACKGROUND_DATA_RESTRICTIONS_SETTINGS
                    .equals(action)) {
                showDialog();
            }
        }
    }

    private void showDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(this.context).create();
        alertDialog.setTitle("Warning");
        alertDialog.setMessage("The option to show the app data usage settings " +
                "is only available in Android 7.0 Nougat");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void setPosition(int position) {
        positionItem = position;
    }

    public void setItemClickState(boolean state) {
        this.itemClick = state;
    }

    public boolean getItemClickState() {
        return itemClick;
    }
}
