package com.example.rfaria.backgrounddata;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<AppsInfo> list;
    private ListAppAdapter adapter;

    private ImageButton icon_1;
    private ImageButton icon_2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        listApps();
    }

    private void listApps() {
        final ListView listApp = (ListView) findViewById(R.id.listApp);

        list = new ArrayList<AppsInfo>();

        final ProgressDialog mProgressDialog = ProgressDialog.show(this,
                getString(R.string.loading), getString(R.string.please));

        AppsListLoader loader = new AppsListLoader(this, new AsyncResponse() {
            @Override
            public void processFinish(List<AppsInfo> appsSyncList) {
                list = appsSyncList;
                adapter = new ListAppAdapter(MainActivity.this, list);
                listApp.setAdapter(adapter);
                mProgressDialog.dismiss();

            }
        });
        loader.execute();

        listApp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!adapter.getItemClickState()) {
                    adapter.setPosition(position);
                    adapter.setItemClickState(true);
                } else {
                    adapter.notifyDataSetChanged();
                    adapter.setItemClickState(false);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
