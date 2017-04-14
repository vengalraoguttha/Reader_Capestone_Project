package com.vengalrao.android.reader;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.vengalrao.android.reader.Utilities.Book;
import com.vengalrao.android.reader.Utilities.NetworkUtilities;
import com.vengalrao.android.reader.data.BookContrack;
import com.vengalrao.android.reader.ui.BookAdapter;
import com.vengalrao.android.reader.ui.DetailActivity;
import com.vengalrao.android.reader.ui.SearchQueryDialog;
import com.vengalrao.android.reader.ui.Settings;

import java.net.URL;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>,BookAdapter.GridItemClickListener,SharedPreferences.OnSharedPreferenceChangeListener{

    Toolbar toolbar;
    RecyclerView mRecyclerView;
    TextView mTextView;
    Book[] books;
    BookAdapter adapter;
    private static final String KEY="QUERY";
    private static final String KEY_IMG="QUERY_IMG";
    private static final String KEY_STYPE="S_TYPE";
    private static String INTENT_SEND="Data";
    private static String SAVE_BUNDLE="SaveBundle";
    public static int LOADER_ID=111;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=(Toolbar)findViewById(R.id.toolBar_main);
        setSupportActionBar(toolbar);
        mRecyclerView=(RecyclerView)findViewById(R.id.book_main_recycler_view);
        mTextView=(TextView)findViewById(R.id.no_net);
        adapter=new BookAdapter(this,this);
        sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        int columns;
        if(this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT){
            columns=2;
        }else{
            columns=3;
        }
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,columns);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);
        if(savedInstanceState==null){
            boolean flag=sharedPreferences.getBoolean(getString(R.string.display_fav_key),false);
            if(flag){
                fromFavBase();
            }else {
                String s1=sharedPreferences.getString(getString(R.string.book_search_pref_key),getString(R.string.book_search_default_val));
                String s2=sharedPreferences.getString(getString(R.string.list_search_by_key),getString(R.string.search_by_author_key));
                searchQuery(s1,s2,"Harry Potter",false);
            }
        }
        else {
            books=toBooksArray(savedInstanceState.getParcelableArray(SAVE_BUNDLE));
            adapter.setData(books);
        }
        //Bundle bundle=new Bundle();
        //bundle.putString(KEY,"Novels");
        //service can be implemented by uncommenting given lines.
        /*FirebaseJobDispatcher jobDispatcher=new FirebaseJobDispatcher(new GooglePlayDriver(this));
        Job bookLoadJob=jobDispatcher.newJobBuilder()
                .setService(MyBookJobService.class)
                .setTag("BOOKS_JOB")
                .setRecurring(false)
                .setLifetime(Lifetime.FOREVER)
                .setTrigger(Trigger.executionWindow(0,600))
                .setExtras(bundle)
                .build();
        jobDispatcher.mustSchedule(bookLoadJob);
        fromDataBase();*/
    }

    public Book[] toBooksArray(Parcelable[] parcelables){
        if(parcelables!=null){
            Book[] b=new Book[parcelables.length];
            for (int i=0;i<parcelables.length;i++){
                b[i]=new Book();
                b[i]=(Book)parcelables[i];
            }
            return b;
        }else {
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    public void button(View view){
        new SearchQueryDialog().show(getFragmentManager(),"Search Dialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        if(id==R.id.action_setting){
            Intent intent=new Intent(MainActivity.this, Settings.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArray(SAVE_BUNDLE,books);
    }

    public void fromFavBase(){
        Cursor cursor=getContentResolver().query(BookContrack.BookFavorite.CONTENT_FAV_URI,
                null,
                null,
                null,
                null);
        if(cursor!=null) {
            books = new Book[cursor.getCount()];
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getCount(); i++) {
                books[i] = new Book();
                books[i].setId(cursor.getString(cursor.getColumnIndex(BookContrack.BookFavorite.BOOK_ID)));
                books[i].setTitle(cursor.getString(cursor.getColumnIndex(BookContrack.BookFavorite.BOOK_TITLE)));
                books[i].setAuthors(cursor.getString(cursor.getColumnIndex(BookContrack.BookFavorite.BOOK_AUTHOR)));
                books[i].setPublishedDate(cursor.getString(cursor.getColumnIndex(BookContrack.BookFavorite.BOOK_PUBLISHED_DATE)));
                books[i].setDescription(cursor.getString(cursor.getColumnIndex(BookContrack.BookFavorite.BOOK_DESCRIPTION)));
                books[i].setCategory(cursor.getString(cursor.getColumnIndex(BookContrack.BookFavorite.BOOK_CATEGORY)));
                books[i].setAvgRating(cursor.getString(cursor.getColumnIndex(BookContrack.BookFavorite.BOOK_AVG_RATING)));
                books[i].setWebReaderLink(cursor.getString(cursor.getColumnIndex(BookContrack.BookFavorite.BOOK_WEB_READER_LINK)));
                books[i].setLanguage(cursor.getString(cursor.getColumnIndex(BookContrack.BookFavorite.BOOK_LANG)));
                books[i].setImage(cursor.getString(cursor.getColumnIndex(BookContrack.BookFavorite.BOOK_IMAGE)));
                books[i].setPageCount(cursor.getString(cursor.getColumnIndex(BookContrack.BookFavorite.BOOK_PAGE_COUNT)));
                cursor.moveToNext();
                //Log.v("fav data",books[i].getTitle());
            }
            adapter.setData(books);
            adapter.notifyDataSetChanged();
        }
    }

    public void fromDataBase(){
        Cursor cursor=getContentResolver().query(BookContrack.BookGeneral.CONTENT_GENERAL_URI,
        null,
        null,
        null,
        null);
        if(cursor!=null) {
            books = new Book[cursor.getCount()];
            for (int i = 0; i < cursor.getCount(); i++) {
                books[i] = new Book();
                books[i].setId(cursor.getString(cursor.getColumnIndex(BookContrack.BookGeneral.BOOK_ID)));
                books[i].setTitle(cursor.getString(cursor.getColumnIndex(BookContrack.BookGeneral.BOOK_TITLE)));
                books[i].setAuthors(cursor.getString(cursor.getColumnIndex(BookContrack.BookGeneral.BOOK_AUTHOR)));
                books[i].setPublishedDate(cursor.getString(cursor.getColumnIndex(BookContrack.BookGeneral.BOOK_PUBLISHED_DATE)));
                books[i].setDescription(cursor.getString(cursor.getColumnIndex(BookContrack.BookGeneral.BOOK_DESCRIPTION)));
                books[i].setCategory(cursor.getString(cursor.getColumnIndex(BookContrack.BookGeneral.BOOK_CATEGORY)));
                books[i].setAvgRating(cursor.getString(cursor.getColumnIndex(BookContrack.BookGeneral.BOOK_AVG_RATING)));
                books[i].setWebReaderLink(cursor.getString(cursor.getColumnIndex(BookContrack.BookGeneral.BOOK_WEB_READER_LINK)));
                books[i].setLanguage(cursor.getString(cursor.getColumnIndex(BookContrack.BookGeneral.BOOK_LANG)));
                books[i].setImage(cursor.getString(cursor.getColumnIndex(BookContrack.BookGeneral.BOOK_IMAGE)));
                books[i].setPageCount(cursor.getString(cursor.getColumnIndex(BookContrack.BookGeneral.BOOK_PAGE_COUNT)));
            }
            adapter.setData(books);
            adapter.notifyDataSetChanged();
        }
    }

    public void searchQuery(String s,String s2,String query,boolean flag){
        if(s!=null&&!s.equals("")) {
            Bundle bundle = new Bundle();
            if(flag){
                bundle.putString(KEY_IMG,s);
            }else{
                bundle.putString(KEY, query);
                bundle.putString(KEY_STYPE,s2);
                bundle.putString("S_VAL",s);
            }
            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<String> bookLoader = loaderManager.getLoader(LOADER_ID);
            if (bookLoader == null) {
                loaderManager.initLoader(LOADER_ID, bundle, this);
            } else {
                loaderManager.restartLoader(LOADER_ID, bundle, this);
            }
        }
    }


    public void showBooksData(){
        mRecyclerView.setVisibility(View.VISIBLE);
        mTextView.setVisibility(View.INVISIBLE);
    }

    public void showErrorMessage(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            @Override
            public String loadInBackground() {
                if(args.containsKey(KEY)){
                    String s=null;
                    String s2=null;
                    URL url=null;
                    if(args.containsKey(KEY_STYPE)){
                        s=args.getString(KEY_STYPE);
                        s2=args.getString("S_VAL");
                        url=NetworkUtilities.buildSpecificUrl(s,s2,args.getString(KEY));
                    }else{
                        url=NetworkUtilities.buildUrl(args.getString(KEY));
                    }
                    return NetworkUtilities.getResponseFromHttpUrl(url);
                }else{
                    if(books!=null){
                        for(int i=0;i<books.length;i++){
                            books[i].setHighQualityImage(NetworkUtilities.getHighQualityImage(books[i].getId()));
                        }
                    }else{
                        //Toast.makeText(MainActivity.this, "No Books available with given query.", Toast.LENGTH_SHORT).show();
                        return null;
                    }
                    return "###";
                }
            }

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                forceLoad();
            }
        };
    }


    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if(data!=null&&!data.equals("")&&!data.equals("###")){
            showBooksData();
            books=NetworkUtilities.getParsedData(data);
            searchQuery("###","###","###",true);
            adapter.setData(books);
        }else if(("###").equals(data)){

        }else{
            showErrorMessage();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }

    @Override
    public void onGridItemClickListener(int clickedPosition,ImageView imageView) {
        Intent intent=new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(INTENT_SEND,books[clickedPosition]);
        ActivityOptions options=ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,imageView,getString(R.string.image_transition));
        startActivity(intent,options.toBundle());
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        String s1=sharedPreferences.getString(getString(R.string.book_search_pref_key),getString(R.string.book_search_default_val));
        String s2=sharedPreferences.getString(getString(R.string.list_search_by_key),getString(R.string.search_by_author_key));
        searchQuery(s1,s2,"books",false);
    }
}
