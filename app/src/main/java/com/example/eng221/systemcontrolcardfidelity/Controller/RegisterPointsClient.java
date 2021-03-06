package com.example.eng221.systemcontrolcardfidelity.Controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eng221.systemcontrolcardfidelity.Model.QRCode;
import com.example.eng221.systemcontrolcardfidelity.Model.Cliente;
import com.example.eng221.systemcontrolcardfidelity.Model.ControladoraFachadaSingleton;
import com.example.eng221.systemcontrolcardfidelity.Model.Ponto;
import com.example.eng221.systemcontrolcardfidelity.R;
import com.example.eng221.systemcontrolcardfidelity.Util.BancoDadosSingleton;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.WriterException;

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
    public String codigoAlfanumerico = "";
    public String codigoQRCode = "";

    QRCode qrCode;

    public ArrayList<String> pontosValidacao = new ArrayList<>();
    public Map<Integer, Integer> map = new HashMap<Integer, Integer>();
    public ArrayAdapter<String> adapter;

    public Map<Integer, Ponto> mapPonto = new HashMap<Integer, Ponto>();
    public Cliente cliente = ControladoraFachadaSingleton.getInstance().getCliente();
    Ponto ponto = new Ponto(idCL);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_points_client);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar2);
        setSupportActionBar(myToolbar);

        getWindow().setStatusBarColor(Color.parseColor("#5383E8"));

        // Inicializa Qrcode
        qrCode = new QRCode();

        Spinner s = findViewById(R.id.spinnerPoints);
        s.setOnItemSelectedListener(this); //configura m??todo de sele????o
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
            Toast.makeText(this, "Nenhuma valida????o no momento", Toast.LENGTH_SHORT).show();
        }
    }

    public void onItemSelected(AdapterView parent, View v, int posicao, long id) {
        Integer idPontosR = map.get(posicao);
        if (idPontosR != null) {
            idPR = Integer.parseInt(idPontosR.toString());
        }

        setVariaveis(idPR);

        TextView alfa = findViewById(R.id.codeAlpha);
        alfa.setText(codigoAlfanumerico);

        // Informacoes de qrcode da tela
        ImageView qrCodeImage = (ImageView) findViewById(R.id.imageQRCode);
        TextView codeNumberDescription = (TextView) findViewById(R.id.codeAlphaText);
        //TextView codeNumber = (TextView) findViewById(R.id.codeAlpha);

        if(!codigoAlfanumerico.equals("")) {
            // Gera QrCode
            Log.i("AndroidT","Aqui 2");
            try {
                qrCode.generateQrCode(codigoAlfanumerico);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }

        // Desenha qrcode
        qrCodeImage.setImageBitmap(qrCode.getBitmap());
        codeNumberDescription.setText("C??digo alfanum??rico:");
        //codeNumber.setText(codigoAlfanumerico);

        //Toast.makeText(this, "Item: " + pontosValidacao.get(posicao) + " id: " + idPontosR.toString() , Toast.LENGTH_SHORT).show();
    }

    public void onNothingSelected(AdapterView arg0) { }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        setTitle("");

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

    public void generatePoints(View view) throws WriterException, ChecksumException, NotFoundException, FormatException {
        String tag = view.getTag().toString();

        if (tag.equals("alfanumerico")) {
            if(codeResgatado == 0) {

                try {
                    ponto.setIdCliente(idCL);
                    ponto.setIdEmpresa(idEP);
                    ponto.setPontosRegatar(1);

                    Map<Integer, Ponto> p = cliente.getPonto();
                    Ponto p2 = p.get(idCL);

                    if(p2 != null) {
                        ponto.setPontosTotal(p2.getPontosTotal() +pontosResgatar);
                        ponto.setPontosParaValidar(p2.getPontosParaValidar()+pontosResgatar);
                    } else {
                        ponto.setPontosTotal(pontosResgatar);
                        ponto.setPontosParaValidar(pontosResgatar);
                    }


                    mapPonto.put(idEP, ponto);
                    //map.put(key, map.get(key) + 1);
                    cliente.setPonto(mapPonto);


                    if(!codigoAlfanumerico.equals("")) {


                        cliente.resgatarPontos(idEP,ponto.getPontosTotal(),ponto.getPontosParaValidar());
                        cliente.excluiResgate(idPR);

                        recreate();

                        Toast.makeText(this, "Ponto resgatado na empresa +"+nomeEP+".\nVoce ganhou "+pontosResgatar+" pontos." + "\nVoce tem  "+ponto.getPontosParaValidar()+ " para resgatar.\nVoce tem "+ponto.getPontosTotal()+ " no total!", Toast.LENGTH_LONG).show();
                    }

                    Toast.makeText(this, "Nenhum ponto para validar", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Toast.makeText(this, "Nenhum c??digo para validar", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(this, "C??digo j?? validado", Toast.LENGTH_SHORT).show();
            }
        } else if (tag.equals("qrcode")) {
            if(codeResgatado == 0) {
                if(!codigoAlfanumerico.equals("")) {
                    if(codigoAlfanumerico.equals(qrCode.getCode())){
                        try {
                            ponto.setIdCliente(idCL);
                            ponto.setIdEmpresa(idEP);
                            ponto.setPontosRegatar(1);

                            Map<Integer, Ponto> p = cliente.getPonto();
                            Ponto p2 = p.get(idCL);

                            if(p2 != null) {
                                ponto.setPontosTotal(p2.getPontosTotal() +pontosResgatar);
                                ponto.setPontosParaValidar(p2.getPontosParaValidar()+pontosResgatar);
                            } else {
                                ponto.setPontosTotal(pontosResgatar);
                                ponto.setPontosParaValidar(pontosResgatar);
                            }


                            mapPonto.put(idEP, ponto);
                            //map.put(key, map.get(key) + 1);
                            cliente.setPonto(mapPonto);

                            if(!codigoAlfanumerico.equals("")) {

                                cliente.resgatarPontos(idEP,ponto.getPontosTotal(),ponto.getPontosParaValidar());
                                cliente.excluiResgate(idPR);

                                recreate();

                                Toast.makeText(this, "Ponto resgatado na empresa +"+nomeEP+".\nVoce ganhou "+pontosResgatar+" pontos." + "\nVoce tem  "+ponto.getPontosParaValidar()+ " para resgatar.\nVoce tem "+ponto.getPontosTotal()+ " no total!", Toast.LENGTH_LONG).show();
                            }
                            Log.i("AndroidT","Aqui 11");

                            Toast.makeText(this, "Nenhum ponto para validar", Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Toast.makeText(this, "Nenhum c??digo para validar", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(this, "QR Code invalido", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Nenhum c??digo para validar", Toast.LENGTH_SHORT).show();
                }

            }
            else {
                Toast.makeText(this, "QR Code j?? validado", Toast.LENGTH_SHORT).show();
            }
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
                codigoAlfanumerico = c.getString(codeAlpfa);
                codigoQRCode = c.getString(qrCode);
            }

            c.close();
        } catch (Exception e) {
            Toast.makeText(this, "Problema ao setar vari??veis", Toast.LENGTH_SHORT).show();
        }
    }

    private String geraCodeAlfa() {
        String[] caracteres ={  "0","1","2","3","4","5","6","7","8","9",
                "a","b","c","d","e","f","g","h","i","j",
                "k","l","m","n","o","p","q","r","s","t",
                "u","v","w","x","y","z","A","B","C","D",
                "E","F","G","H","I","J","K","L","M","N",
                "O","P","Q","R","S","T","U","V","W","X",
                "Y","Z"};

        String codigo="";
        for (int x=0; x<40; x++){
            int c = (int) (Math.random()*caracteres.length);
            codigo += caracteres[c];
        }

        return codigo;
    }

}