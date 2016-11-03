package com.jobsouza.hotspot;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/*
Controle de acesso à Internet para visitantes na empresa que utilizam smartphone Android. Este App consulta servidor via http para liberação do acesso e consulta do status da conexão com a Internet.
 */

public class MainActivity extends Activity implements View.OnClickListener {
    public static Context context;
    public static Util utilMain;
    private EditText etId;
    private Button btOk;
    private TextView tvDadosWifi;
    private TextView tvRetorno;

    LinearLayout rootView; //Para detectar se teclado virtual aberto ou não.

    public static MyAsyncTask assyncTaskPost = null;
    private String idUsuario = "";
    private String gatewayWifi = "";

    public static String consulta = "status"; //status, validarId (conectar) ou desconectar

    public String nomeSharedPreferences = "arquivoID";
    public static boolean msgErroWifi = false;
    private int iniciou = 0;
    public boolean tecladoAberto = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("LOG", "onCreate - Inicio");
        context = this;
        utilMain = new Util();

        etId = (EditText) findViewById(R.id.et_cfg_id);
        btOk = (Button) findViewById(R.id.bt_cfg_ok);
        btOk.setOnClickListener(this);
        tvDadosWifi = (TextView) findViewById(R.id.tV_dados_wifi);
        tvRetorno = (TextView) findViewById(R.id.tV_retorno);
        tvRetorno.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);

        rootView = (LinearLayout) findViewById(R.id.linear_layout_main); //Para detectar se teclado virtual aberto ou não.

        idUsuario = lerIDdoArquivo();
        if (idUsuario.length() > 0) {
            etId.setText(idUsuario);
        }

        if (utilMain.isNetworkConnected(this, ConnectivityManager.TYPE_WIFI)) { //Retorna true se acesso rede wi-fi OK
            Log.i("LOG", "OnCreate - Acesso rede de dados OK.");
            mostraDadosRedeWifi();
            if (idUsuario.length() > 0) {
                assyncTaskPost = new MyAsyncTask();
                assyncTaskPost.execute("");
            } else {
                consulta = "validarId"; //Conectar
            }
        } else {
            String nomeErro = context.getString(R.string.cfg_erro_rede);
            Log.i("LOG", "OnCreate - " + nomeErro);
            DialogoDeAlerta dialogo = new DialogoDeAlerta();
            dialogo.showDialogoDeAlerta(context, context.getString(R.string.cfg_campo_titulo), nomeErro, context.getString(R.string.nome_Botao_Sim), context.getString(R.string.nome_Botao_Nao));
        }

        //Setar Foco no EditText
        etId.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setFocusable(true);
                v.setFocusableInTouchMode(true);
                return false;
            }
        });

        //Para detectar se teclado virtual aberto ou não.
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //int heightDiff = rootView.getRootView().getHeight() - rootView.getRootView().getHeight();
                int heightView = rootView.getHeight();
                int widthView = rootView.getWidth();
                int heightDiff = heightView - widthView;

                if (heightDiff < 100) {
                    Log.i("LOG", "keyboard opened - dif=" + heightDiff + " - heightView=" + heightView + " - widthView=" + widthView);
                    if (!tecladoAberto) {
                        tvRetorno.setText(" ");
                        Log.i("LOG", "keyboard opened - Apagado texto de retorno.");
                        tecladoAberto = true;
                    }
                } else {
                    tecladoAberto = false;
                    Log.i("LOG", "keyboard closed - dif=" + heightDiff + " - heightView=" + heightView + " - widthView=" + widthView);
                }
            }
        });

        Log.i("LOG", "onCreate - Fim");
    }


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();

        Log.i("LOG", "MainActivity: onStart fim");
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        Log.i("LOG", "MainActivity: onResume");

    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        Log.i("LOG", "MainActivity: onDestroy");

    }


    @Override
    public void onBackPressed() { //Método chamado quando o botão de retorno (finalizar) do dispositivo móvel é pressionado.
        // TODO Auto-generated method stub
        super.onBackPressed();
        Log.i("LOG", "MainActivity: onBackPressed");
    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.i("LOG", "MainActivity: onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i("LOG", "MainActivity: onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i("LOG", "MainActivity: onRestart");
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        Log.i("LOG", "MainActivity: onPostCreate");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.i("LOG", "MainActivity: onPostResume");
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        Log.i("LOG", "MainActivity: onWindowFocusChanged - hasFocus=" + hasFocus + "consulta=" + consulta);
        //super.onWindowFocusChanged(hasFocus);

        if (iniciou == 1) {
            if (msgErroWifi) {
                msgErroWifi = false;
                Log.i("LOG", "MainActivity: onWindowFocusChanged - Chamando Assync Task.");

                assyncTaskPost = new MyAsyncTask();
                assyncTaskPost.execute("");
            }
        } else if (hasFocus) {
            iniciou = 1;
        }
        Log.i("LOG", "MainActivity: onWindowFocusChanged - FIM");
    }


    public void mostraDadosRedeWifi() {
        DhcpInfo d = utilMain.retornaInfosWifi(this);

        gatewayWifi = utilMain.intToIp(d.gateway);

        String tempoStr = String.valueOf(d.leaseDuration);
        float tempoFlo = Integer.parseInt(tempoStr) / 3600;
        tempoStr = tempoFlo + "";

        String msgHora = " horas";
        if ((int) tempoFlo == 1) {
            msgHora = " hora";
        }

        String msg = "IP: " + utilMain.intToIp(d.ipAddress) +
                //"\nMáscara: " + utilMain.intToIp(d.netmask) +
                "\nGateway: " + gatewayWifi +
                //"\nDNS1: " + utilMain.intToIp(d.dns1) +
                //"\nDNS2: " + utilMain.intToIp(d.dns2) +
                "\nTempo de concessão roteador: " + tempoStr + msgHora; //Tempo de concessão do roteador para acesso a rede.

        tvDadosWifi.setText(msg);
    }


    @Override
    public void onClick(View arg0) {
        Log.i("LOG", "onClick botão OK INICIO");

        tvRetorno.setText(""); //Limpar mensagem de retorno.
        msgErroWifi = false;

        //Ler e validar ID do usuário
        String nomeErro = "";
        idUsuario = etId.getText().toString();
        idUsuario = idUsuario.trim(); //Remove espaços apenas no início e no fim da String

        gravarIDemArquivo(idUsuario);

        Log.i("LOG", "ID digitado=" + idUsuario);

        MainActivity.utilMain.hideKeyboard(context, etId); //Retirar teclado virtual da tela.

        if (utilMain.isNetworkConnected(this, ConnectivityManager.TYPE_WIFI)) { //Retorna true se acesso rede wi-fi OK
            Log.i("LOG", "Acesso rede de dados OK.");

            if (!idUsuario.equals("")) {
                //Enviar dados do usuário para o Servidor

                mostraDadosRedeWifi(); //Ler novamente para obter o gateway (usuario pode ter trocado de rede antes de apertar OK).

                assyncTaskPost = new MyAsyncTask();
                assyncTaskPost.execute("");
            } else {
                tvRetorno.setText("Digitar o ID.");
            }
        } else {
            nomeErro = context.getString(R.string.cfg_erro_rede);
            Log.i("LOG", nomeErro);
            DialogoDeAlerta dialogo = new DialogoDeAlerta();
            dialogo.showDialogoDeAlerta(context, context.getString(R.string.cfg_campo_titulo), nomeErro, context.getString(R.string.nome_Botao_Sim), context.getString(R.string.nome_Botao_Nao));
        }
        Log.i("LOG", "onClick botão OK FIM");
    }

    //=============================== INICIO ======================================================
    //SharedPreferences para Gravar e Ler ID.
    public void gravarIDemArquivo(String idLogin) {
        Log.i("LOG", "Gravar ID=" + idLogin + " em Shared Preferences.");

        SharedPreferences IDGravar = getSharedPreferences(nomeSharedPreferences, MODE_PRIVATE);
        SharedPreferences.Editor editor = IDGravar.edit();

        editor.putString("IdArquivo", idLogin);
        editor.commit();
    }

    public String lerIDdoArquivo() {
        SharedPreferences IDLer = getSharedPreferences(nomeSharedPreferences, MODE_PRIVATE);
        String IdLido = IDLer.getString("IdArquivo", "0");

        Log.i("LOG", "Lido ID=" + IdLido + " de Shared Preferences.");
        return IdLido;
    }
    //================================== FIM ======================================================


    //Acesso Servidor - INICIO
    public class MyAsyncTask extends AsyncTask<String, Integer, String> {
        private DialogoDeAlerta dialogo = null;

        @Override
        protected void onPreExecute() {
            // TODO Auto-generated method stub
            super.onPreExecute();
            String msgTitulo = context.getString(R.string.progr_bar_titulo);
            String msg = context.getString(R.string.progr_bar_msg);
            String nomeBotaoNeg = context.getString(R.string.progr_bar_botao_neg);
            dialogo = new DialogoDeAlerta();
            dialogo.showBarraDeProgresso(context, msgTitulo, msg, nomeBotaoNeg, assyncTaskPost);
        }

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String resp = postData(params[0]);
            return resp;
        }

        protected void onPostExecute(String result) {
            if (dialogo.getHandlerProgressDialog() != null) {
                dialogo.getHandlerProgressDialog().dismiss(); //Fecha diálogo com barra de progresso.
            }
            //Toast.makeText(context, result, Toast.LENGTH_LONG).show();
            Log.i("LOG", "Resposta sem espaços = '" + result + "'");
            result = utilMain.retirarEspacosDaStringNoInicioFim(result);
            Log.i("LOG", "Resposta do Servidor = '" + result + "' - consulta=" + consulta);

            boolean erro = false;
            if (consulta.equals("status")) {
                /* Possíveis respostas:
					 expirado = Cartão expirado
					 1|X  = 1 quer dizer conectado e X é a validade em dias. Sempre número inteiro.
					 0 =desconectado */
                char[] c = result.toCharArray();

                if (result.equals("0")) {
                    tvRetorno.setText("Desconectado");
                    btOk.setText(getString(R.string.nome_Botao_Cnx)); //Mudar o nome do botao para Conectar.
                    consulta = "validarId"; //Setando proxima consulta.
                } else if (result.equals("expirado")) {
                    tvRetorno.setText("Cartão expirado");
                } else if (c[0] == '1') { //1|X  = 1 quer dizer conectado e X é a validade em dias. Sempre número inteiro.
                    if (c[1] == '|') {
						/*  Exemplo de uso do split:
							String string = "004-034556";
							String[] parts = string.split("-");
							String part1 = parts[0]; // 004
							String part2 = parts[1]; // 034556 */

                        //O split não funciona com separador |.
                        //| is a metacharacter in regex. You'd need to escape it with \\:
                        String[] parts = result.split("\\|");
                        String validade = parts[1];
                        String dias = " dias.";
                        if (validade.equals("1")) {
                            dias = " dia.";
                        }
                        tvRetorno.setText("Conectado - Validade: " + validade + dias);
                        btOk.setText(getString(R.string.nome_Botao_Dcnx)); //Mudar o nome do botao para Desconectar.
                        consulta = "desconectar"; //Setando proxima consulta.
                    } else {
                        erro = true;
                    }
                } else {
                    erro = true;
                }
                if (erro) {
                    tvRetorno.setText("Resposta inválida do Servidor.");
                    btOk.setText(getString(R.string.nome_Botao_Cnx)); //Mudar o nome do botao para Conectar.
                    consulta = "validarId"; //Setando proxima consulta.
                }
            } else if (consulta.equals("validarId")) {
                //success = quando conectado com sucesso.
                //diferente de sucess pode considerar erro.
                if (result.equals("success")) {
                    tvRetorno.setText("Conectado com sucesso.");
                    btOk.setText(getString(R.string.nome_Botao_Dcnx)); //Mudar o nome do botao para Desconectar.
                    consulta = "desconectar"; //Setando proxima consulta.
                } else {
                    //tvRetorno.setText("Não foi possível conectar.");
                    tvRetorno.setText(result);
                    btOk.setText(getString(R.string.nome_Botao_Cnx)); //Mudar o nome do botao para Conectar.
                    consulta = "validarId"; //Setando proxima consulta.
                }
            } else if (consulta.equals("desconectar")) {
				/* Retorno:
					 0 = nao foi processado o pedido de desconexão, algum erro interno ou id invalido.. id nao é numero.. etc..
					 1 = desconectado com sucesso. */
                if (result.equals("1")) {
                    tvRetorno.setText("Desconectado com sucesso.");
                    btOk.setText(getString(R.string.nome_Botao_Cnx)); //Mudar o nome do botao para Conectar.
                    consulta = "validarId"; //Setando proxima consulta.
                } else {
                    tvRetorno.setText("Não foi possível desconectar.");
                    btOk.setText(getString(R.string.nome_Botao_Cnx)); //Mudar o nome do botao para Conectar.
                    consulta = "validarId"; //Setando proxima consulta.
                }
            } else {
                tvRetorno.setText("Erro. Refazer a consulta.");
                btOk.setText(getString(R.string.nome_Botao_Cnx)); //Mudar o nome do botao para Conectar.
                consulta = "validarId"; //Setando proxima consulta.
            }
            Log.i("LOG", "Realizado consulta ao Servidor.");
        }

        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
        }


        private String postData(String valueIWantToSend) {
            String resposta = "Sem acesso ao Servidor.";
            // Create a new HttpClient and Post Header
            HttpClient httpclient = new DefaultHttpClient();

            String tipoConsulta = "";
            if (consulta.equals("status")) {
                tipoConsulta = "status.php?id=";
            } else if (consulta.equals("validarId")) {
                tipoConsulta = "valida_login.php?login=";
            } else if (consulta.equals("desconectar")) {
                tipoConsulta = "desconectar.php?id=";
            } else {
                tipoConsulta = "status.php?id=";
                consulta = "status";
            }

            String nomeHost;
            if (gatewayWifi.equals("192.168.0.1")) { //Teste => usa acesso externo
                //Alterar "nomedositedeacessoaoservidor.com.br:8088/cartao" para os dados corretos de acesso ao seu servidor.
                nomeHost = "http://nomedositedeacessoaoservidor.com.br:8088/cartao/" + tipoConsulta + idUsuario;
            } else { //Uso na rede local
                nomeHost = "http://" + gatewayWifi + ":80/cartao/" + tipoConsulta + idUsuario;
            }
            Log.i("LOG", "Nome Host: " + nomeHost);

            HttpPost httppost = new HttpPost(nomeHost);
            try {
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                String responseStr = EntityUtils.toString(response.getEntity());

                if ((responseStr.contains("<head>")) && (responseStr.contains("<body>"))) { //Significa que quem respondeu não foi o Servidor correto.
                    responseStr = "Servidor indisponível.";
                }

                Log.i("LOG", "Resp: '" + responseStr + "'");

                if (responseStr.equals("success")) {
                    Log.i("LOG", "POST OK. Validação ID aceito pelo Servidor.");
                    //resposta = "OK";
                } else {
                    Log.i("LOG", "POST NOK. NÃO aceito ID pelo Servidor.");
                }
                resposta = responseStr;

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                Log.i("LOG", "ERRO ClientProtocolException: " + e);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.i("LOG", "ERRO IOException: " + e);
                //Entra aqui quando não tem rede de dados habilitada: nem Wi-Fi e nem 3G/4G. Ex:
                //ERRO IOException: java.net.UnknownHostException: Unable to resolve host "www.rdstation.com.br": No address associated with hostname
                //Se rede habilitada, porém há problema na rede, e a resposta do servidor não chega, em aproximadamente 40 seg entra aqui.
            }
            return resposta;
        }
    }
    //Acesso Servidor - FIM

}
