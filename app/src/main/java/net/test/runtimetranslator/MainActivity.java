package net.test.runtimetranslator;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import test.geekstools.rrt.RRT;

public class MainActivity extends Activity {
    /*"http://m4jid2k.info/translate/translator.php?text="
            + rawRes
    + "&from="+ "en" + "&to=" + "fr"*/

    RRT rrt;
    TextView info;
    String rawRes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        info = (TextView)findViewById(R.id.infoView);

        rrt = new RRT(getApplicationContext(), getPackageName(), R.string.class.getFields());
        rrt.doTranslateOperation();
        String val1 = rrt.setupStringResources(getResources().getResourceEntryName(R.string.info), getResources().getString(R.string.info));

        info.append("Returned Result RRT >> " + val1);
    }
    @Override
    public void onStart(){
        super.onStart();
    }
    @Override
    public void onResume(){
        super.onResume();
    }
    @Override
    public void onPause(){
        super.onPause();
    }
}
