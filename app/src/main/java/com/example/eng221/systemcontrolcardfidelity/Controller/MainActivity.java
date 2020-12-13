package com.example.eng221.systemcontrolcardfidelity.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.eng221.systemcontrolcardfidelity.Model.QRCode;
import com.example.eng221.systemcontrolcardfidelity.R;
import com.example.eng221.systemcontrolcardfidelity.Util.BancoDadosSingleton;
import com.google.zxing.WriterException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void choiceActivity(View view) {
        String tag = view.getTag().toString();

        if (tag.equals("cliente")) {
            Intent it = new Intent(getBaseContext(), GeneratePointsClient.class);
            startActivity(it);

        } else if (tag.equals("empresa")) {
            Intent it = new Intent(getBaseContext(), GeneratePointsCompany.class);
            startActivity(it);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BancoDadosSingleton.getInstance().fechar();
    }
}