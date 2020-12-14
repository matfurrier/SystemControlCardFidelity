package com.example.eng221.systemcontrolcardfidelity.Controller;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eng221.systemcontrolcardfidelity.R;
import com.example.eng221.systemcontrolcardfidelity.Util.BancoDadosSingleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegisterPointsClient extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    public static final int VOLTAR = 1;
    public static final int GENERATEPOINTS = 2;
    public int idPR;
    public int idEP;
    public int idCL;
    public int codeResgatado;
    public int pontosResgatar;
    public double reaisC;
    public String nomeEP;
    public String codedigoAlfanumerico;
    public String codigoQRCode;

    public ArrayList<String> pontosValidacao = new ArrayList<>();
    public Map<Integer, Integer> map = new HashMap<Integer, Integer>();
    public ArrayAdapter<String> adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_points_client);

        Spinner s = findViewById(R.id.spinnerPoints);
        s.setOnItemSelectedListener(this); //configura método de seleção
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, pontosValidacao);
        s.setAdapter(adapter);
    }

    protected void onStart() {
        super.onStart();

        adapter.setNotifyOnChange(false);

        try {
            Cursor c = BancoDadosSingleton.getInstance().buscar("pontosResgatar", new String[]{"idPontosResgatar", "idEmpresa", "nomeE", "idCliente", "reais", "pontosGanhar", "codeAlfa", "qrCode", "resgatado"}, "", "nomeE, reais");

            int i = 0;

            while(c.moveToNext()){
                int idPontosResgatar = c.getColumnIndex("idPontosResgatar");
                int idEmpresa = c.getColumnIndex("idEmpresa");
                int idCliente = c.getColumnIndex("idCliente");
                int nomeE = c.getColumnIndex("nomeE");
                int reais = c.getColumnIndex("reais");
                int pontosGanhar = c.getColumnIndex("pontosGanhar");
                int codeAlpfa = c.getColumnIndex("codeAlfa");
                int qrCode = c.getColumnIndex("qrCode");
                int resgatado = c.getColumnIndex("resgatado");

                pontosValidacao.add(c.getString(nomeE) + " - R$ " + c.getDouble(reais));
                map.put(i, c.getInt(idPontosResgatar));
                i++;
            }

            // Habilitar novamente a notificacao
            adapter.setNotifyOnChange(true);
            // Notifica o Spinner de que houve mudanca no modelo
            adapter.notifyDataSetChanged();

            c.close();
        } catch (Exception e) {
            Toast.makeText(this, "Nenhuma validação no momento", Toast.LENGTH_SHORT).show();
        }
    }

    public void onItemSelected(AdapterView parent, View v, int posicao, long id) {
        Integer idPontosR = map.get(posicao);
        if (idPontosR != null) {
            idPR = Integer.parseInt(idPontosR.toString());
        }

        setVariaveis(idPR);

        TextView alfa = findViewById(R.id.codeAlpha);
        alfa.setText(codedigoAlfanumerico);

        Toast.makeText(this, "Item: " + pontosValidacao.get(posicao) + " id: " + idPontosR.toString() , Toast.LENGTH_SHORT).show();
    }

    public void onNothingSelected(AdapterView arg0) { }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuItem item = menu.add(0, VOLTAR, 1, "Voltar");
        menu.add(0, GENERATEPOINTS, 0, "Gerar Pontos");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == GENERATEPOINTS) {
            Intent it = new Intent(this, GeneratePointsClient.class);
            startActivity(it);
            finish();
            return true;
        }

        else if(item.getItemId() == VOLTAR) {
            finish();
            return true;
        }

        return false;
    }

    public void generatePoints(View view) {
        String tag = view.getTag().toString();

        if (tag.equals("alfanumerico")) {

        } else if (tag.equals("qrcode")) {

        }
    }

    public void setVariaveis(int idPR) {
        try {
            Cursor c = BancoDadosSingleton.getInstance().buscar("pontosResgatar", new String[]{"idEmpresa", "nomeE", "idCliente", "reais", "pontosGanhar", "codeAlfa", "qrCode", "resgatado"}, "idPontosResgatar='"+idPR+"'", "");

            while(c.moveToNext()){
                int idEmpresa = c.getColumnIndex("idEmpresa");
                int idCliente = c.getColumnIndex("idCliente");
                int nomeE = c.getColumnIndex("nomeE");
                int reais = c.getColumnIndex("reais");
                int pontosGanhar = c.getColumnIndex("pontosGanhar");
                int codeAlpfa = c.getColumnIndex("codeAlfa");
                int qrCode = c.getColumnIndex("qrCode");
                int resgatado = c.getColumnIndex("resgatado");

                idEP = c.getInt(idEmpresa);
                idCL = c.getInt(idCliente);
                nomeEP = c.getString(nomeE);
                codeResgatado = c.getInt(resgatado);
                pontosResgatar = c.getInt(pontosGanhar);
                reaisC = c.getInt(reais);
                codedigoAlfanumerico = c.getString(codeAlpfa);
                codigoQRCode = c.getString(qrCode);
            }

            c.close();
        } catch (Exception e) {
            Toast.makeText(this, "Problema ao setar variáveis", Toast.LENGTH_SHORT).show();
        }
    }
}