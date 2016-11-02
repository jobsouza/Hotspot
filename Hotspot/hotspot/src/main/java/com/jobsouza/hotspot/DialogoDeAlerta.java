package com.jobsouza.hotspot;

import com.jobsouza.hotspot.MainActivity.MyAsyncTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class DialogoDeAlerta {
	
	public DialogoDeAlerta() {
    }

	private Button bOk = null;
	private Button bNao = null;
	
	private ProgressDialog handlerProgressDialog = null;
	private Button bCancelProDialog = null;
	
	
	public ProgressDialog getHandlerProgressDialog() {
		return handlerProgressDialog;
	}
	
	public void showDialogoDeAlerta (final Context c, String msgTitulo, final String msg, String nomeBotaoPos, final String nomeBotaoNeg) {
		
		Log.i("LOG", "showDialogoDeAlerta - msgTitulo=" + msgTitulo + " msg=" + msg + " nomeBotaoPos=" + nomeBotaoPos + " nomeBotaoNeg=" + nomeBotaoNeg);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c)
        		.setTitle(msgTitulo)
				.setIcon(R.drawable.fornecedor2)
        		.setMessage(Html.fromHtml(msg))
				.setCancelable(false) //Bloqueia botão voltar do aparelho
				.setNegativeButton(nomeBotaoNeg, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Log.i("LOG", "showDialogoDeAlerta - onClick - Botão NÃO");
						if (bNao != null) {
							bNao.setBackgroundColor(c.getResources().getColor(R.color.dialogoAlerta_cor_botao_selecionado)); //Botão Negative
						}
						((Activity)c).finish();
					}
				})
				.setPositiveButton(nomeBotaoPos, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Log.i("LOG", "showDialogoDeAlerta - onClick - Botão SIM");
						if (bOk != null) {
							bOk.setBackgroundColor(c.getResources().getColor(R.color.dialogoAlerta_cor_botao_selecionado)); //Botão Positive
						}
						MainActivity.msgErroWifi = false;
						if (msg == c.getString(R.string.cfg_erro_rede)) {
							if (MainActivity.utilMain.isNetworkConnected(c, ConnectivityManager.TYPE_WIFI)) { //Retorna true se acesso rede wi-fi OK
								Log.i("LOG", "showDialogoDeAlerta - Fim, Wi-Fi conectado.");
								MainActivity.msgErroWifi = true;
								if (MainActivity.consulta == "status") {
									MainActivity.consulta = "validarId";
								}
							} else { //Wi-Fi não conectado
								Log.i("LOG", "showDialogoDeAlerta - Fim, Wi-Fi não conectado.");
								((Activity) c).finish(); //Se sem conexão Wi-Fi e usuário apertar botão OK sem antes ter ativado o Wi-Fi, o App é finalizado.
							}
						}

					}
				});

		//Log.i("LOG", "showDialogoDeAlerta - Criar Alert Dialog");
		//Criar Alert Dialog
		AlertDialog alertDialog = alertDialogBuilder.create();
		//Mostrar Alert Dialog
		alertDialog.show();
		//A linha abaixo precisa ser após o show(), para gerar hyperlink
		((TextView) alertDialog.findViewById(android.R.id.message)).setMovementMethod(LinkMovementMethod.getInstance());

		//Log.i("LOG", "showDialogoDeAlerta - Setar cor do botão Positive do AlertDialog");
		//Setar cor do botão Positive do AlertDialog
		bOk = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE); //Ao setar a cor, o botão não muda de cor quando é clicado.
		bOk.setBackgroundColor(c.getResources().getColor(R.color.dialogoAlerta_cor_botao_normal));
		bOk.setTextColor(c.getResources().getColor(R.color.dialogoAlerta_cor_letras_botao));
		bOk.setTextSize(Integer.parseInt(c.getString(R.string.dialogoAlerta_tamanho_letras_botao)));
		bOk.setTypeface(Typeface.create(c.getString(R.string.fonte_textos), Typeface.NORMAL));

		//Log.i("LOG", "showDialogoDeAlerta - Tem botão negativo?");
		if (nomeBotaoNeg.length() > 1) { //Tem botão negativo
			//Setar cor do botão Negative do AlertDialog
			bNao = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE); //Ao setar a cor, o botão não muda de cor quando é clicado.
			bNao.setBackgroundColor(c.getResources().getColor(R.color.dialogoAlerta_cor_botao_normal));
			bNao.setTextColor(c.getResources().getColor(R.color.dialogoAlerta_cor_letras_botao));
			bNao.setTextSize(Integer.parseInt(c.getString(R.string.dialogoAlerta_tamanho_letras_botao)));
			bNao.setTypeface(Typeface.create(c.getString(R.string.fonte_textos), Typeface.NORMAL));
		}

		//Log.i("LOG", "showDialogoDeAlerta - Mudar cor da linha abaixo do título");
		//Mudar cor da linha abaixo do título
  	    int dividerId = alertDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
  	    View divider = alertDialog.findViewById(dividerId);
  	    //divider.setBackgroundColor(c.getResources().getColor(R.color.dialogoAlerta_cor_linha_entre_titulo_message));
		//Log.i("LOG", "showDialogoDeAlerta - FIM");
	}



	public void showBarraDeProgresso (final Context c, String msgTitulo, String msg, String nomeBotaoNeg, final MyAsyncTask assyncTaskPost) {
		  //Mudar cor e tamanho do título do ProgressDialog
		  TextView ssTitulo = new TextView(c);
		  ssTitulo.setText(msgTitulo);
		  ssTitulo.setBackgroundColor(c.getResources().getColor(R.color.progress_cor_titulo));
		  //ssTitulo.setPadding(left, top, right, bottom);
		  ssTitulo.setPadding(10, 10, 10, 10);
		  ssTitulo.setGravity(Gravity.CENTER);
		  ssTitulo.setTextColor(c.getResources().getColor(R.color.progress_cor_letras_titulo));
		  ssTitulo.setTextSize(Integer.parseInt(c.getString(R.string.progress_tamanho_letras_titulo)));
	  
	      //Mudar cor e tamanho da mensagem do ProgressDialog
		  //String msg = msgAlertDialog;
		  SpannableString ssMsg1 = new SpannableString(msg);
		  ssMsg1.setSpan(new RelativeSizeSpan(Float.parseFloat(c.getString(R.string.progress_tamanho_letras_msg))), 0, ssMsg1.length(), 0);
		
		  handlerProgressDialog = new ProgressDialog(c);
		  handlerProgressDialog.setCustomTitle(ssTitulo);
		  handlerProgressDialog.setMessage(ssMsg1);
		  handlerProgressDialog.setCancelable(false);
		  handlerProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, nomeBotaoNeg, new DialogInterface.OnClickListener() {	
			  @Override
			  public void onClick(DialogInterface dialog, int which) {
				  // TODO Auto-generated method stub
				  if (assyncTaskPost != null) {
					  assyncTaskPost.cancel(true);
				  }
				  if (bCancelProDialog != null) {
					  bCancelProDialog.setBackgroundColor(c.getResources().getColor(R.color.progress_cor_botao_selecionado)); //Botão Cancelar
					  bCancelProDialog = null;
				  }
			  }
		  });
		  //Mostra spinner girando (res/drawable/spinner.jpg)
		  handlerProgressDialog.setIndeterminate(true);
		  handlerProgressDialog.setIndeterminateDrawable(c.getResources().getDrawable(R.drawable.meu_progress_dialog));
		
		  handlerProgressDialog.show();
		  
		  //Setar cor e tamanho do botão Cancelar do ProgressDialog
		  bCancelProDialog = handlerProgressDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
		  bCancelProDialog.setBackgroundColor(c.getResources().getColor(R.color.progress_cor_botao_normal)); //Ao setar a cor, o botão não muda de cor quando é tocado, só quando é clicado.
		  bCancelProDialog.setTextColor(c.getResources().getColor(R.color.progress_cor_letras_botao));
		  bCancelProDialog.setTextSize(Integer.parseInt(c.getString(R.string.progress_tamanho_letras_botao)));
		
		  //Mudar cor da linha abaixo do título
		  int dividerId = handlerProgressDialog.getContext().getResources().getIdentifier("android:id/titleDivider", null, null);
		  View divider = handlerProgressDialog.findViewById(dividerId);
		  //divider.setBackgroundColor(c.getResources().getColor(R.color.progress_cor_linha_entre_titulo_msg));
	}
	
}
