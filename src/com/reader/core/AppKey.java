package com.reader.core;

/*
 * Clase en la que almacenamos tanto el app_key como el app_secret.
 * Los hacemos privados para asegurarnos que la recoleccion de dichos
 * valores sea SIEMPRE a traves de nuestra interfaz.
 * 
 * Added by: Javier Rodriguez.
 */

public class AppKey
{
	private static final String APP_KEY = "4vetfwm4mrv3b97";
	private static final String APP_SECRET = "lb8jbajmr3wzefk";
	
	public static String getAppKey()
	{
		return APP_KEY;
	}
	
	public static String getAppSecret()
	{
		return APP_SECRET;
	}
}
