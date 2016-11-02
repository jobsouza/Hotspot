package com.jobsouza.hotspot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Util {
	
	public Util() {
		
	}
	
	
	public void hideKeyboard (Context c, View v) { //v pode ser, por exemplo, um EdiText.
		// Forçar esconder o teclado virtual do dispositivo (smartphone, tablet, ...) 
		InputMethodManager imm = 
				(InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
	
	
	public void showKeyboard (Context c) {
		// Forçar mostrar o teclado virtual do dispositivo (smartphone, tablet, ...) 
		InputMethodManager imm = 
				(InputMethodManager) c.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}
	
	
	//Retorna true se conexão OK: Se tem conexão wi-fi ou 3G/4G.
	public boolean verificaConexao(Context c)
	{
		ConnectivityManager conectivtyManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean conectado = false;

		if (conectivtyManager.getActiveNetworkInfo() != null  
				&& conectivtyManager.getActiveNetworkInfo().isAvailable()  
				&& conectivtyManager.getActiveNetworkInfo().isConnected()) {
					conectado = true;
		}
		return conectado;  
	}


	//Retorna true se tipo de conexão OK, onde type pode ser: TYPE_MOBILE e TYPE_WIFI. Se tem conexão wi-fi ou 3G/4G.
	public boolean isNetworkConnected(Context c, int type){
		ConnectivityManager connectivityManager = (ConnectivityManager) c.getSystemService(Context.CONNECTIVITY_SERVICE);
    	final NetworkInfo network = connectivityManager.getNetworkInfo(type);
    	if (network != null && network.isAvailable() && network.isConnected()){
        	return true;
    	} else {
        	return false;
   		}
	}


	//Retorna informacoes sobre a conexão Wi-Fi
	//Como saber o gateway da rede wi-fi?
	DhcpInfo retornaInfosWifi (Context c) {
		WifiManager wifii = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo d = wifii.getDhcpInfo();
/*
	String s_gateway="Default Gateway: "+String.valueOf(d.gateway);
	String s_dns1="DNS 1: "+String.valueOf(d.dns1);
	String s_dns2="DNS 2: "+String.valueOf(d.dns2);
	String s_ipAddress="IP Address: "+String.valueOf(d.ipAddress);
	String s_leaseDuration="Lease Time: "+String.valueOf(d.leaseDuration);  //Tempo de concessão do roteador para acesso a rede.
	String s_netmask="Subnet Mask: "+String.valueOf(d.netmask);
	String s_serverAddress="Server IP: "+String.valueOf(d.serverAddress);
*/
		return d;
	}


	//Transforma IP inteiro em IP String no formato x.x.x.x
	public String intToIp(int i) {

		return (i & 0xFF) + "." +
				((i >> 8 ) & 0xFF) + "." +
				((i >> 16 ) & 0xFF) + "." +
				((i >> 24 ) & 0xFF) ;
	}



	//Retorna true se email é válido.
	public boolean validarEndEmail(String email) {
		if (email.length() < 8) {
			Log.i("LOG", "O E-mail '"+email+"' é inválido, tem menos de 8 caracteres.");
	    	return false;
		} else {
		    Pattern p = Pattern.compile("^[\\w-]+(\\.[\\w-]+)*@([\\w-]+\\.)+[a-zA-Z]{2,7}$"); 
		    Matcher m = p.matcher(email); 
		    if (m.find()){
		    	Log.i("LOG", "O email '"+email+"' é valido.");
		    	return true;
		    }
		    else{
		    	Log.i("LOG", "O E-mail '"+email+"' é inválido.");
		    	return false;
		    }
		}
	 }
	
	
    //Retorna true se str tem o caractere cc.
    public boolean stringTemCaracterEsp (String str, char cc) {
    	char[] c = str.toCharArray();
    	boolean resp = false;
    	
    	for (int i=0; i < c.length; i++) {
    		if (c[i] == cc) {
    			resp = true;
    			break;
    		}
    	}
    	return resp;
    }
    
    
    public String retirarEspacosDaStringNoInicioFim (String str) {
		return str.trim(); //Remove espaços apenas no início e no fim da String str.
	}
	
	
	public String retirarEspacosDaStringNoInicioMeioFim (String str) {
		return str.replaceAll(" ", "");  //Remove espaços no início, meio e fim da String str.
	}

	
}
