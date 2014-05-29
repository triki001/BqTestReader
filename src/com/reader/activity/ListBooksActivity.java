package com.reader.activity;

import java.util.ArrayList;

import com.reader.ui.EpubGridViewAdapter;
import com.reader.bqtestreader.R;
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
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bq_drop_box_list_books);

		//Inicializamos las variables que gestionaran los eventos del doble click.
		item_clicks = 0;
		last_item_clicked = -1;
		
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
								getString(R.string.title_section2),
								getString(R.string.title_section3), }), this);
		
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
		c_handler = ConnectionHandler.getInstance(this.getApplicationContext());	
	}

	public void onResume()
	{
		super.onResume();
		
		ArrayList<GenericDropboxFile> files = null;
		ArrayList<GenericDropboxFile> ordered_files = null;
		AbstractCriteria criteria = null;
		
		//Por si las moscas, nos aseguramos de que este inicializado ;-).
		if(!c_handler.isReady())
		{
			if(!c_handler.init())
				Debug.showToast(getApplicationContext(), getString(R.string.error_not_init_connection_handler));
		}
		
		file_handler = new FilesHandler(c_handler.getDropboxAccount());
		
		if(!file_handler.init())
			Debug.showToast(getApplicationContext(), getString(R.string.error_not_init_file_handler));
		
		//Obtenemos la lista de ficheros.
		files = file_handler.getFiles();
		FilesHandler.printInformationFiles(files);
		
		grid_view.setAdapter(new EpubGridViewAdapter(this, R.layout.book_grid, files));
		
		//Manejar la libreria epub.
		file_handler.openFile(files.get(0));
		EpubDropboxFile file = (EpubDropboxFile)files.get(0);
		
		//Funciones para obtener el titulo y la imagen del epub seleccionado ;-).
		EpubFileHandler.retrieveEpubTitle(file_handler,file);
		EpubFileHandler.retrieveEpubFrontImage(file_handler, file);
		
		Debug.i("Epub title: "+file.getTitle());
		Debug.i("Epub front image size: "+file.getFrontImage().length+"bytes");
		
		//Testear los criterios de ordenacion.
		try 
		{
			criteria = CriteriaFactory.getShortCriteria(CriteriaFactory.BY_DATE);
			ordered_files = FileShorter.do_short(files, criteria);
			FilesHandler.printInformationFiles(ordered_files);
		} 
		catch (InvalidCriteriaException e) 
		{
			Debug.showToast(getApplicationContext(), e.getMessage());
		}
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
		// When the given dropdown item is selected, show its contents in the
		// container view.
		getFragmentManager()
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();
		return true;
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
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.fragment_bq_drop_box_list_books, container, false);
			TextView textView = (TextView) rootView
					.findViewById(R.id.section_label);
			textView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
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

}
