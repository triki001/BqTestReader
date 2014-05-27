package com.reader.core;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/*
 * Clase de debugueo. Incluimos una constante para identificar nuestra
 * app en del DDMS.
 * 
 * Agrupamos todos las funciones de generacion de Logs, tanto las que se hacen a traves de DDMS
 * como las que se hacen a traves de un toast.
 * 
 * Added by: Javier Rodriguez.
 */

public class Debug 
{
	public static final String TAG = "BqTestReader";
	private static final int toast_duration = 5;
	
	public static void i(String message)
	{
		Log.i(Debug.TAG,message);
	}
	
	public static void w(String message)
	{
		Log.w(Debug.TAG,message);
	}
	
	public static void e(String message)
	{
		Log.e(Debug.TAG,message);
	}
	
	public static void showToast(Context ctx,String message)
	{
		Toast.makeText(ctx, message, toast_duration).show();
	}
}
