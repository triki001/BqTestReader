package com.reader.activity;

import java.util.ArrayList;

import com.dropbox.sync.android.DbxException;
import com.reader.ui.EpubGridViewAdapter;
import com.reader.bqtestreader.R;
import com.reader.core.ApplicationCoreHandler;
import com.reader.core.ConnectionHandler;
import com.reader.core.Debug;
import com.reader.core.EpubFileHandler;
import com.reader.core.FilesHandler;
import com.reader.criteria.AbstractCriteria;
import com.reader.criteria.CriteriaFactory;
import com.reader.exception.InvalidCriteriaException;
import com.reader.file.EpubDropboxFile;
import com.reader.file.GenericDropboxFile;
import com.reader.shorter.FileShorter;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;

public class ListBooksActivity extends Activity implements
		ActionBar.OnNavigationListener {

	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";
	private GridView grid_view;
	private ConnectionHandler c_handler;
	private FilesHandler file_handler;
	private int item_clicks;
	private int last_item_clicked;
	private ProgressDialog pDialog;
	private ArrayList<GenericDropboxFile> file_list = null;
	private boolean is_sync;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bq_drop_box_list_books);

		//Inicializamos las variables que gestionaran los eventos del doble click.
		item_clicks = 0;
		last_item_clicked = -1;
		is_sync = false;
		
		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(actionBar.getThemedContext(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, new String[] {
								getString(R.string.title_section1),
								getString(R.string.title_section2), }), this);
		
		Log.i(Debug.TAG,"> ListBooks onCreate()");
		
		grid_view = (GridView)findViewById(R.id.gridView);
		
		/*
		 * Incluimos el adaptador al gridView definido para mostrar los libros.
		 * Los libros se mostraran con un icono generico.
		 * 
		 * Se incluye un listener para ser capaces de capturar el evento
		 * de cuando se seleccione un elemento del GridView.
		 * 
		 * Hemos implementado el metodo del listener para que solo se pueda "activar"
		 * cuando se ha hecho un doble click sobre el mismo icono.
		 * 
		 * Cuando esto ocurra, se lanzara un activity mostrando la portada del libro
		 * seleccionado.
		 */
		//grid_view.setAdapter(new ImageAdapter(this));
        grid_view.setOnItemClickListener(new OnItemClickListener() 
        {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) 
            {	
            	/*
            	 * Con esta condicion controlamos si se ha pulsado sobre
            	 * el mismo elemento, en caso negativo, se pone el contador
            	 * a 0. De esta forma podemos controlar que el doble click
            	 * sea siempre sobre el mismo icono.
            	 */
            	if(last_item_clicked != position)
            	{
            		last_item_clicked = position;
            		item_clicks = 0;
            	}

            	item_clicks++;
            	
            	if(item_clicks == 2) //Doble click hack ;-)
            	{
            		//TODO: Aqui lanzariamos el nuevo activity para mostrar la foto.
            		Debug.i("> [debug] selected item: "+position);
            		item_clicks = 0;
            	}
            }
        });
		
		//Recuperamos la instancia.
        c_handler = ApplicationCoreHandler.getConnectionHandler(getApplicationContext());
		
		//Por si las moscas, nos aseguramos de que este inicializado ;-).
		if(!c_handler.isReady())
		{
			if(!c_handler.init())
				Debug.showToast(getApplicationContext(), getString(R.string.error_not_init_connection_handler));
		}
		
		file_handler = ApplicationCoreHandler.getFilesHandler(c_handler);
		
		if(!file_handler.init())
			Debug.showToast(getApplicationContext(), getString(R.string.error_not_init_file_handler));
	}

	public void onResume()
	{		
		if(isAppSync())
		{
			retrieveFileList();
		}
		else
			doSync();
		
		super.onResume();
	}
	
	public void onBackPressed ()
	{
		super.onBackPressed();
		c_handler.unlinkAccount();
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.bq_drop_box_list_books, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		AbstractCriteria criteria = null;
		// When the given dropdown item is selected, show its contents in the
		// container view.
		getFragmentManager()
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();
		
		if(!isAppSync())
			return false;
		
		try
		{
			switch(position)
			{
				case 0: //Por nombre de archivo
					criteria = CriteriaFactory.getShortCriteria(CriteriaFactory.BY_NAME);
				break;
				case 1: //Por ultima modificacion.
					criteria = CriteriaFactory.getShortCriteria(CriteriaFactory.BY_DATE);
				break;
				default:
					criteria = CriteriaFactory.getShortCriteria(CriteriaFactory.BY_TITLE);
			}
			
			doShortFileList(criteria);
		}
		catch(InvalidCriteriaException e)
		{
			Debug.showToast(getApplicationContext(), e.getMessage());
		}
		
		return true;
	}

	/*
	 * Funciones definidas por el programador.
	 * 
	 * Estas funciones encapsulan el funcionamiento de la activity.
	 */
	
	/*
	 * Funcion para actualizar la vista que muestra los ficheros epub.
	 */
	private void updateGridView()
	{
		grid_view.setAdapter(new EpubGridViewAdapter(this, R.layout.book_grid, file_list));
	}
	
	/*
	 * Funcion que lanza una asynctask para obtener los ficheros
	 * del servidor. 
	 */
	private void retrieveFileList()
	{
        pDialog = new ProgressDialog(ListBooksActivity.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(true);
        pDialog.setMax(100);
        
        ListarFicherosTask task = new ListarFicherosTask();
        task.execute();
	}
	
	/*
	 * Esta funcion verifica que nos hemos sincronizado correctamente
	 * con el servidor. Hasta que no estemos sincronizados y nos hayamos
	 * descargado la cache no deberiamos de poder hacer nada, asi que 
	 * esta funcion y su atributo controlan esto.
	 * 
	 */
	
	private boolean isAppSync()
	{
		return is_sync;
	}
	
	/*
	 * La funcion doShortFileList lanza una AsyncTask para ordenar la lista
	 * de ficheros obtenida previamente. En este caso, al no especificar
	 * un criterio, utilizaremos el criterio por defecto que es el titulo.
	 */
	
	private void doShortFileList()
	{
        pDialog = new ProgressDialog(ListBooksActivity.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(true);
        pDialog.setMax(100);
        
        ShortListTask task = new ShortListTask();
        try 
        {
			task.execute(CriteriaFactory.getShortCriteria(CriteriaFactory.BY_TITLE));
		} 
        catch (InvalidCriteriaException e) 
        {
			Debug.showToast(getApplicationContext(), e.getMessage());
		}			
	}

	/*
	 * La funcion doShortFileList lanza una AsyncTask para ordenar la lista
	 * de ficheros obtenida previamente. En este caso, la ordenacion se hace
	 * a traves del criterio especificado.
	 * 
	 * Esta funcion se llama cuando se selecciona uno de 2 criterios de seleccion
	 * definidos en los requisitos.
	 */
	private void doShortFileList(AbstractCriteria criteria)
	{
        pDialog = new ProgressDialog(ListBooksActivity.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(true);
        pDialog.setMax(100);
        
        ShortListTask task = new ShortListTask();
        task.execute(criteria);	
	}
	
	/*
	 * Nada mas arrancar la app es necesario realizar una sincronizacion
	 * para que la app se descargue la cache del directorio (nombres, deltas, etc...)
	 * Esta tarea es bastante pesada (y hasta que no termina, la funcion de 
	 * listar ficheros se vuelve bloqueante) hemos implementado una AsyncTask.
	 */
	
	private void doSync()
	{
        pDialog = new ProgressDialog(ListBooksActivity.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setCancelable(true);
        pDialog.setMax(100);
        
        DropboxSyncTask task = new DropboxSyncTask();
        task.execute();		
	}
	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static PlaceholderFragment newInstance(int sectionNumber) {
			PlaceholderFragment fragment = new PlaceholderFragment();
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
		{
			View rootView = inflater.inflate(
					R.layout.fragment_bq_drop_box_list_books, container, false);

			return rootView;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == ConnectionHandler.getRequestResultCode()) {
	        if (resultCode == Activity.RESULT_OK) {
	            Log.i(Debug.TAG,"> OK.");
	        } else {
	            // ... Link failed or was cancelled by the user.
	        }
	    } else {
	        super.onActivityResult(requestCode, resultCode, data);
	    }
	}

	/*
	 * Definicion de las asynctasks.
	 * 
	 * La creacion de AsyncTasks responde a la necesidad de solventar de una manera
	 * no bloqueante tareas pesadas. En nuestro caso, tenemos 3 tareas que son o
	 * podrian ser bloqueantes y harian que nuestra app funcionase mal o que 
	 * Android la cerrase. 
	 * 
	 * Las tareas son:
	 * 1) Listar, obtener titulos de los libros.
	 * 2) Sincronizarse con dropbox.
	 * 3) Ordenar una lista (es lento si la lista es larga, complejidad O(n^2).
	 * 
	 * Ademas, aprovechando la potencia de las AsyncTasks, hemos definido un pequeño
	 * flujo de trabajo que nos ha hecho simplificar mucho el codigo de la Activity.
	 * 
	 * Existen 2 flujos:
	 * 
	 * 1) flujo que se lanza al ejecutar onResume().
	 * 2) flujo que se lanza al ejecutar onNavigationItemSelected().
	 * 
	 * flujo #1.1 [Si no esta sincronizado]
	 * onResume() -> doSync() -> onResume() -> retrieveFileList() -> doShortFileList() -> updateGridView().
	 * 
	 * flujo #1.2 [Si esta sincronizado]
	 * onResume() -> retrieveFileList() -> doShortFileList() -> updateGridView().
	 * 
	 * fujo #2.
	 * onNavigationItemSelected() -> doShortFileList(criteria) -> updateGridView().
	 * 
	 */
	
	private class ListarFicherosTask extends AsyncTask<Void, String, Boolean> 
	{
		@Override
		protected Boolean doInBackground(Void... params)
		{
			getFileList();
			getEpubTitles();
			file_handler.printInformationFiles();
			return true;
		}

		private void getFileList()
		{
			publishProgress("Obteniendo lista de libros ...");
			file_list = file_handler.getFiles();
		}
		
		private void getEpubTitles()
		{
			if(file_list == null)
				return;
			
			int i_ct;
			int i_size = file_list.size();
			
			for(i_ct=0;i_ct<i_size;i_ct++)
			{
				publishProgress("Obteniendo titulo ... "+(i_ct+1)+" de "+i_size);
				Debug.i("> [debug] Obteniendo titulo ... "+(i_ct+1)+" de "+i_size);
				EpubFileHandler.retrieveEpubTitle(file_handler,(EpubDropboxFile)file_list.get(i_ct));
			}
		}
			
		@Override
		protected void onProgressUpdate(String... message) 
		{
			pDialog.setMessage(message[0]);
		}

		@Override
		protected void onPreExecute() 
		{
			//Habilitamos el flag que permite a Android no apagar la pantalla.
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			
			pDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) 
				{
					ListarFicherosTask.this.cancel(true);
				}
			});
			
			pDialog.setProgress(0);
			pDialog.show();
		
		}

		@Override
		protected void onPostExecute(Boolean result) 
		{
			//Deshabilitamos el flag que permite a Android no apagar la pantalla.
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			
			if(result) 
			{
				Debug.i("> [debug] listarFicherosAsyncTask result OK.");
				pDialog.dismiss();
				doShortFileList();
			}
		}

		@Override
		protected void onCancelled() 
		{
			Debug.showToast(getApplicationContext(), "Tarea cancelada");
		}
	}
	
	/*
	 * Tarea asincrona para realizar la sincronizacion del dispositivo.
	 */
	
	private class DropboxSyncTask extends AsyncTask<Void, String, Boolean> 
	{
		@Override
		protected Boolean doInBackground(Void... params)
		{
			publishProgress("Sincronizando ... ");
			
			try 
			{
				file_handler.awaitFirstSync();
			} 
			catch (DbxException e) 
			{
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onProgressUpdate(String... message) 
		{
			pDialog.setMessage(message[0]);
		}

		@Override
		protected void onPreExecute() 
		{
			//Habilitamos el flag que permite a Android no apagar la pantalla.
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			
			pDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) 
				{
					DropboxSyncTask.this.cancel(true);
				}
			});

			pDialog.setProgress(0);
			pDialog.show();
		}

		@Override
		protected void onPostExecute(Boolean result) 
		{
			//Deshabilitamos el flag que permite a Android no apagar la pantalla.
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			
			if(result) 
			{
				pDialog.dismiss();
				is_sync = true;
				onResume();
			}
			else
			{
				Debug.showToast(getApplicationContext(), "Error. No se pudo sincronizar con el servidor");
			}
		}

		@Override
		protected void onCancelled() 
		{
			Debug.showToast(getApplicationContext(), "Tarea cancelada");
		}
	}

	private class ShortListTask extends AsyncTask<AbstractCriteria, String, Boolean> 
	{
		@Override
		protected Boolean doInBackground(AbstractCriteria... criteria)
		{
			publishProgress("Ordenando lista ...");
			file_list = FileShorter.do_short(file_list, criteria[0]);
			return true;
		}

		@Override
		protected void onProgressUpdate(String... message) 
		{
			pDialog.setMessage(message[0]);
		}

		@Override
		protected void onPreExecute() 
		{
			//Habilitamos el flag que permite a Android no apagar la pantalla.
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			
			pDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface dialog) 
				{
					ShortListTask.this.cancel(true);
				}
			});

			pDialog.setProgress(0);
			pDialog.show();
		}

		@Override
		protected void onPostExecute(Boolean result) 
		{
			//Deshabilitamos el flag que permite a Android no apagar la pantalla.
			getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
			
			if(result) 
			{
				pDialog.dismiss();
				updateGridView();
			}
		}

		@Override
		protected void onCancelled() 
		{
			Debug.showToast(getApplicationContext(), "Tarea cancelada");
		}
	}
}
