package com.nemogames;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;

public class NativeUIAgent 
{
	private String		ListenerGameObject = "";
	private String		ListenerFunction = "";
	private Activity	RootActivity;
	private boolean		inited = false;
	private boolean		still_waiting = false;
	private AlertDialog	last_dialog = null;
	
	public enum NativeUIEvent
	{
		MessageBoxButton(1),
		InputBoxButton(2),
		PopupMenuButton(3),
		MultiplyInputs(4);
		int value;
		NativeUIEvent(int val) { value = val; }
		public int getValue() { return value; }
	}
	
	private void	SendListenerEvent(String json)
	{
		if (!inited) return;
		UnityPlayer.UnitySendMessage(ListenerGameObject, ListenerFunction, json);
	}
	
	public void		init(String gameobject, String function)
	{
		this.RootActivity = UnityPlayer.currentActivity;
		this.ListenerGameObject = gameobject;
		this.ListenerFunction = function;
		this.inited = true;
	}
	
	public void		ShowMessageBox(final String title, final String message,
			final String button1, final String button2, final String button3)
	{
		if (last_dialog != null && last_dialog.isShowing())
		{
			Log.w("Nemo - NativeUIAgent", "A dialog is already open, cannot create new one!");
			return;
		}
		RootActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run() 
			{
				AlertDialog.Builder ad = new AlertDialog.Builder(RootActivity);  
				ad.setCancelable(false);
				ad.setTitle(title);
				ad.setMessage(message);
				if (!button1.equals("")) ad.setPositiveButton(button1, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface arg0, int arg1) 
					{
						arg0.dismiss();
						JSONObject obj = new JSONObject();
						try 
						{
							obj.put("eid", NativeUIEvent.MessageBoxButton.getValue());
							obj.put("button_index", 0);
						} catch (JSONException e) {} finally
						{
							SendListenerEvent(obj.toString());
						}
					}
				});
				if (!button2.equals("")) ad.setNeutralButton(button2, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface arg0, int arg1) 
					{
						arg0.dismiss();
						JSONObject obj = new JSONObject();
						try 
						{
							obj.put("eid", NativeUIEvent.MessageBoxButton.getValue());
							obj.put("button_index", 1);
						} catch (JSONException e) {} finally
						{
							SendListenerEvent(obj.toString());
						}
					}
				});
				if (!button3.equals("")) ad.setNegativeButton(button3, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface arg0, int arg1) 
					{
						arg0.dismiss();
						JSONObject obj = new JSONObject();
						try 
						{
							obj.put("eid", NativeUIEvent.MessageBoxButton.getValue());
							obj.put("button_index", 2);
						} catch (JSONException e) {} finally
						{
							SendListenerEvent(obj.toString());
						}
					}
				});
				last_dialog = ad.create();
				last_dialog.show();
			}
		});
	}
	public void		ShowMessage(final String message, final boolean short_view)
	{
		RootActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				Toast toast = Toast.makeText(RootActivity.getBaseContext(), message, (short_view? Toast.LENGTH_LONG:Toast.LENGTH_SHORT));
				toast.show();
			}
		});
	}
	public void		ShowCustomInputDialog(final String title, final ArrayList inputplaceholders, final String button1, final String button2, final String button3)
	{
		RootActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run() 
			{
				AlertDialog.Builder dialog = new AlertDialog.Builder(RootActivity);
				dialog.setTitle(title);
				
				LinearLayout ll = new LinearLayout(RootActivity);
				ll.setOrientation(LinearLayout.VERTICAL);
				final EditText[] texts = new EditText[inputplaceholders.size()];
				for (int i = 0; i < inputplaceholders.size(); i++)
				{
					String placeholder = (String) inputplaceholders.get(i);
					texts[i] = new EditText(RootActivity);
					texts[i].setHint(placeholder);
					ll.addView(texts[i], new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
				}
				dialog.setView(ll);
				dialog.setCancelable(false);
				if (button1 != "")
					dialog.setPositiveButton(button1, new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface arg0, int arg1) 
						{
							arg0.dismiss();
							JSONObject obj = new JSONObject();
							try
							{
								obj.put("eid", NativeUIEvent.MultiplyInputs.getValue());
								for (int i = 0; i < inputplaceholders.size(); i++)
									obj.put("string_"+i, texts[i].getText().toString());
								obj.put("button_index", 0);
							} catch (JSONException e) {} finally
							{
								SendListenerEvent(obj.toString());
							}
						}
					});
				if (button2 != "")
					dialog.setNeutralButton(button2, new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface arg0, int arg1) 
						{
							arg0.dismiss();
							JSONObject obj = new JSONObject();
							try 
							{
								obj.put("eid", NativeUIEvent.MultiplyInputs.getValue());
								for (int i = 0; i < inputplaceholders.size(); i++)
									obj.put("string_"+i, texts[i].getText().toString());
								obj.put("button_index", 1);
							} catch (JSONException e) {} finally
							{
								SendListenerEvent(obj.toString());
							}
						}
					});
				if (button3 != "")
					dialog.setNegativeButton(button3, new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface arg0, int arg1) 
						{
							arg0.dismiss();
							JSONObject obj = new JSONObject();
							try 
							{
								obj.put("eid", NativeUIEvent.MultiplyInputs.getValue());
								for (int i = 0; i < inputplaceholders.size(); i++)
									obj.put("string_"+i, texts[i].getText().toString());
								obj.put("button_index", 2);
							} catch (JSONException e) {} finally
							{
								SendListenerEvent(obj.toString());
							}
						}
					});
				dialog.show();
			}			
		});
	}
	
	public void		ShowProgressDialog(final String title, final String message, final boolean indicator)
	{
		still_waiting = true;
		RootActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run() 
			{
				new AsyncTask<Integer, Integer, Long>()
				{
		            ProgressDialog progressDialog;
		            
					@Override
		            protected void onPreExecute()
		            {
		                progressDialog = ProgressDialog.show(NativeUIAgent.this.RootActivity, title,
		                        message);
		            }
					
					@Override
					protected Long doInBackground(Integer... arg0) 
					{
						try 
						{
							while (NativeUIAgent.this.still_waiting) Thread.sleep(500);
						} catch (InterruptedException e) { e.printStackTrace(); }
						return null;
					}
					
					@Override
					protected void onPostExecute(Long result) 
					{
						progressDialog.dismiss();
					}
				}.execute(0);
			}
		});
	}
	
	public void		CloseBusy()
	{
		still_waiting = false;
	}
	
	public void		ShowWebView(final String title, final String url, final int width, final int height)
	{
		RootActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run() 
			{
				WebView wv = new WebView(RootActivity);
				wv.loadUrl(url);
				wv.setWebViewClient(new WebViewClient()
				{
					@Override
		            public boolean shouldOverrideUrlLoading(WebView view, String url)
		            {
		                view.loadUrl(url);
		                return true;
		            }
				});
				
				AlertDialog.Builder dialog = new AlertDialog.Builder(RootActivity);
				dialog.setTitle(title);
				dialog.setView(wv);
				dialog.setCancelable(false);
				dialog.setNegativeButton("Close", new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface arg0, int arg1) 
					{
						arg0.dismiss();
					}
				});
				dialog.show();
			}
		});
	}
	
	public void		ShowMessageBoxWithInput(final String title, final String message,
			final String button1, final String button2, final String button3)
	{
		if (last_dialog != null && last_dialog.isShowing())
		{
			Log.w("Nemo - NativeUIAgent", "A dialog is already open, cannot create new one!");
			return;
		}
		
		RootActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run() 
			{
				final EditText txt = new EditText(RootActivity);
				AlertDialog.Builder dialog = new AlertDialog.Builder(RootActivity);
				dialog.setTitle(title);
				dialog.setMessage(message);
				txt.setText("");
				dialog.setView(txt);
				dialog.setCancelable(false);
				if (!button1.equals("")) dialog.setPositiveButton(button1, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface arg0, int arg1) 
					{
						arg0.dismiss();
						JSONObject obj = new JSONObject();
						try 
						{
							obj.put("eid", NativeUIEvent.InputBoxButton.getValue());
							obj.put("button_index", 0);
							obj.put("input", txt.getText());
						} catch (JSONException e) {} finally
						{
							SendListenerEvent(obj.toString());
						}
					}
				});
				if (!button2.equals("")) dialog.setNeutralButton(button2, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface arg0, int arg1) 
					{
						arg0.dismiss();
						JSONObject obj = new JSONObject();
						try 
						{
							obj.put("eid", NativeUIEvent.InputBoxButton.getValue());
							obj.put("button_index", 1);
							obj.put("input", txt.getText());
						} catch (JSONException e) {} finally
						{
							SendListenerEvent(obj.toString());
						}
					}
				});
				if (!button3.equals("")) dialog.setNegativeButton(button3, new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface arg0, int arg1) 
					{
						arg0.dismiss();
						JSONObject obj = new JSONObject();
						try 
						{
							obj.put("eid", NativeUIEvent.InputBoxButton.getValue());
							obj.put("button_index", 2);
							obj.put("input", txt.getText());
						} catch (JSONException e) {} finally
						{
							SendListenerEvent(obj.toString());
						}
					}
				});
				dialog.show();
			}
		});
	}
	
	public void		ShowPopupMenu(final String[] buttons)
	{
		RootActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run() 
			{
				AlertDialog.Builder ad = new AlertDialog.Builder(RootActivity);
				ad.setItems(buttons, new DialogInterface.OnClickListener()
			    {
			        @Override
			        public void onClick(DialogInterface dialog, int which)
			        {
			        	JSONObject obj = new JSONObject();
			        	try {
			        		obj.put("eid", NativeUIEvent.PopupMenuButton.getValue());
							obj.put("button_index", which);
						} catch (JSONException e) { e.printStackTrace(); } finally
						{
							SendListenerEvent(obj.toString());
						}
			        } 
			    });
				ad.show();
			}
		});
	}
	
	public void		ShowComposeMail(final String address, final String subject, final String htmlBody, final String attachment)
	{
		RootActivity.runOnUiThread(new Runnable()
		{
			@Override
			public void run() 
			{
				Intent intent = new Intent(Intent.ACTION_SEND);
				intent.setType("text/html");
				intent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
				intent.putExtra(Intent.EXTRA_SUBJECT, subject);
				intent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(htmlBody));
				if (!attachment.equals("")) intent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + attachment));
				RootActivity.startActivity(intent);
			}
		});
	}
}
