package com.reader.core;

/*
 * Esta clase nos ayudara a interaccionar con la clase encargada del enlazado con la cuenta.
 * Al utilizar el API "Sync" solo es posible hacer el login a traves de la web, considero que
 * esto por ahora es suficiente, llegado el momento estaria interesante echarle un vistazo al
 * API "core" en el que segun parece provee clases relacionada con el autenticado Web sin necesidad
 * de utilizar el navegador.
 * 
 * Hemos utilizado el patron "Singleton" para asegurarnos de que solo existe una instancia en toda la APP.
 * Asi mismo, podemos utilizarla en cualquier punto de la APP.
 * 
 * Added by: Javier Rodriguez.
 */

import com.dropbox.sync.android.DbxAccount;
import com.dropbox.sync.android.DbxAccountManager;
import com.dropbox.sync.android.DbxAccountManager.ConfigurationMismatchException;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

public class ConnectionHandler 
{
	private DbxAccountManager acc_manager;
	private Context context;
	private static final int REQUEST_LINK_TO_DBX = 0;
	private static ConnectionHandler instance = null;
	private boolean isReady;
	
	/*
	 * Constructor privado, requisito impresicindible :-P.
	 */
	private ConnectionHandler(Context context)
	{
		this.context = context;
		isReady = false;
	}
	
	/*
	 * He separado el init del constructor porque soy un poco maniatico y no me gusta
	 * que el constructor pueda tener comportamientos "anómalos" al lanzarse excepciones.
	 * Pero tener controlado yo funcionamiento.
	 * 
	 * Para controlar el que haya sido o no inicializada la variable, utilizamos un booleano
	 * y su correspondiente método "consultor".
	 */
	public boolean init()
	{
		try
		{
			acc_manager = DbxAccountManager.getInstance(context, AppKey.getAppKey(),AppKey.getAppSecret());
			Log.i(Debug.TAG,"> Init done.");
			isReady = true;
		}
		catch(ConfigurationMismatchException ex)
		{
			Log.e(Debug.TAG,"Error. No se pudo inicializar el account manager.");
			isReady = true;
		}
		
		return isReady;
	}
	
	public void linkAccount(Activity next_activity)
	{
		if(isAlreadyLinked())
			unlinkAccount();
		
		acc_manager.startLink(next_activity, REQUEST_LINK_TO_DBX);
		isReady = true;
	}
	
	public void unlinkAccount()
	{
		acc_manager.unlink();
	}
	
	public boolean isAlreadyLinked()
	{
		if(acc_manager.hasLinkedAccount())
		{
			Log.i(Debug.TAG,"> LIIIIIIIIIIIIINKED");
			return true;
		}
		return false;
	}
	
	public boolean isReady()
	{
		return isReady;
	}
	
	public static int getRequestResultCode()
	{
		return REQUEST_LINK_TO_DBX;
	}
	
	//Funcion para recuperar la cuenta de usuario.
	public DbxAccount getDropboxAccount()
	{
		return acc_manager.getLinkedAccount();
	}
	
	//Funcion para recuperar la instancia, un "must" dentro del patron singleton.
	public static ConnectionHandler getInstance(Context context)
	{
		if(instance == null)
			instance = new ConnectionHandler(context);
		
		return instance;
	}
}
