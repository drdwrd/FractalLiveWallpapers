package drwdrd.adev.ui;


import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.app.Activity;


public class ImageListPreference extends DialogPreference implements OnItemSelectedListener
{
	   private CharSequence[] entries;
	   private CharSequence[] entryValues;
	   private int[] imageResources;
	   private String currentValue;
	   private int clickedDialogEntryIndex;

	   public ImageListPreference(Context context, AttributeSet attrs)
	   {
		   super(context, attrs);
	       TypedArray a=context.obtainStyledAttributes(attrs,R.styleable.ImageListPreference,0,0);

	       entries=a.getTextArray(R.styleable.ImageListPreference_entries);
	       entryValues=a.getTextArray(R.styleable.ImageListPreference_entryValues);
	       
	       CharSequence[] entryImages=a.getTextArray(R.styleable.ImageListPreference_entryImages);
	        
	       imageResources=new int[entryImages.length];

	       for(int i=0;i<entryImages.length;i++)
	       {
	    	   String imageName=entryImages[i].toString();
	    	   String name=imageName.substring(imageName.lastIndexOf('/')+1,imageName.lastIndexOf('.'));
	           
	    	   imageResources[i]=context.getResources().getIdentifier(name,"drawable",context.getPackageName());
	       }

	       a.recycle();

	   }

	   public void setEntries(CharSequence[] entries)
	   {
	      this.entries=entries;
	   }

	   public void setEntries(int entriesResId)
	   {
	      setEntries(getContext().getResources().getTextArray(entriesResId));
	   }

	   public CharSequence[] getEntries()
	   {
	      return entries;
	   }

	   public void setEntryValues(CharSequence[] entryValues)
	   {
	      this.entryValues=entryValues;
	   }

	   public void setEntryValues(int entryValuesResId)
	   {
	      setEntryValues(getContext().getResources().getTextArray(entryValuesResId));
	   }

	   public CharSequence[] getEntryValues()
	   {
	      return entryValues;
	   }

	   public void setImageResources(int[] imageResources)
	   {
	      this.imageResources=imageResources;
	   }

	   public int[] getImageResources()
	   {
	      return imageResources;
	   }

	   public void setValue(String value)
	   {
	      currentValue=value;
	      persistString(value);
	   }

	   public void setValueIndex(int index)
	   {
	      if(entryValues!=null)
	      {
	         setValue(entryValues[index].toString());
	      }
	   }

	   public String getValue()
	   {
	      return currentValue;
	   }

	   public CharSequence getEntry()
	   {
	      int index=getValueIndex();
	      return (index>=0)&&(entries!=null)?entries[index]:null;
	   }

	   public int findIndexOfValue(String value)
	   {
	      if((value!=null)&&(entryValues!=null))
	      {
	         for(int i=entryValues.length-1;i>=0;i--)
	         {
	            if(entryValues[i].equals(value))
	            {
	               return i;
	            }
	         }
	      }
	      return -1;
	   }

	   private int getValueIndex()
	   {
	      return findIndexOfValue(currentValue);
	   }

	   @Override
	   protected void onPrepareDialogBuilder(Builder builder)
	   {

		   if((entries==null)||(entryValues==null))
		   {
			   throw new IllegalStateException("ListPreference requires an entries array and an entryValues array.");
		   }

		   clickedDialogEntryIndex=getValueIndex();
		   builder.setSingleChoiceItems(entries,clickedDialogEntryIndex,
				   new DialogInterface.OnClickListener()
		   {
			   public void onClick(DialogInterface dialog,int which)
			   {
				   clickedDialogEntryIndex=which;
	    		  
//				   ImageListPreference.this.onClick(dialog,DialogInterface.BUTTON_POSITIVE);
//				   dialog.dismiss();
			   }
		   });

//		   builder.setPositiveButton(null, null);

		   ImagePreferenceArrayAdapter listAdapter=new ImagePreferenceArrayAdapter(getContext(),R.layout.imagelist_preference,entries,entryValues,imageResources,clickedDialogEntryIndex, this);
		   builder.setAdapter(listAdapter,this);
		   super.onPrepareDialogBuilder(builder);
	   }

	   @Override
	   protected void onDialogClosed(boolean positiveResult)
	   {
	      super.onDialogClosed(positiveResult);

	      if(positiveResult&&clickedDialogEntryIndex>=0&&(entryValues!=null))
	      {
	         String value=entryValues[clickedDialogEntryIndex].toString();
	         if(callChangeListener(value))
	         {
	            setValue(value);
	         }
	      }
	   }

	   @Override
	   protected Object onGetDefaultValue(TypedArray a,int index)
	   {
	      return a.getString(index);
	   }

	   @Override
	   protected void onSetInitialValue(boolean restoreValue,Object defaultValue)
	   {
	      setValue(restoreValue?getPersistedString(currentValue):(String)defaultValue);
	   }

	   @Override
	   protected Parcelable onSaveInstanceState()
	   {
	      final Parcelable superState=super.onSaveInstanceState();
	      if(isPersistent())
	      {
	         // No need to save instance state since it's persistent
	         return superState;
	      }

	      final SavedState myState=new SavedState(superState);
	      myState.value=getValue();
	      return myState;
	   }

	   @Override
	   protected void onRestoreInstanceState(Parcelable state)
	   {
	      if((state==null)||!state.getClass().equals(SavedState.class))
	      {
	         // Didn't save state for us in onSaveInstanceState
	         super.onRestoreInstanceState(state);
	         return;
	      }

	      SavedState myState=(SavedState)state;
	      super.onRestoreInstanceState(myState.getSuperState());
	      setValue(myState.value);
	   }

	   private static class SavedState extends BaseSavedState
	   {
	      String value;

	      public SavedState(Parcel source)
	      {
	         super(source);
	         value = source.readString();
	      }

	      @Override
	      public void writeToParcel(Parcel dest,int flags)
	      {
	         super.writeToParcel(dest, flags);
	         dest.writeString(value);
	      }

	      public SavedState(Parcelable superState)
	      {
	         super(superState);
	      }

	      @SuppressWarnings("unused")
	      public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>()
	      {
	         public SavedState createFromParcel(Parcel in)
	         {
	            return new SavedState(in);
	         }

	         public SavedState[] newArray(int size)
	         {
	            return new SavedState[size];
	         }
	      };
	   }

	   @Override
	   public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,long arg3)
	   {
	      clickedDialogEntryIndex=arg2;
	   }

	   @Override
	   public void onNothingSelected(AdapterView<?> arg0)
	   {
	   
	   }
	   
	   public class ImagePreferenceArrayAdapter extends ArrayAdapter<CharSequence>
	   {
		   private int index;
		   private CharSequence[] entries;
		   private int[] imageResources;
		   private OnItemSelectedListener onItemSelectedListener;

		   public ImagePreferenceArrayAdapter(Context context,int textViewResourceId,CharSequence[] entries,CharSequence[] objects,int[] imageResources,int selected,OnItemSelectedListener onItemSelectedListener)
		   {
		      super(context,textViewResourceId,objects);
		      index=selected;
		      this.entries=entries;
		      this.imageResources=imageResources;
		      this.onItemSelectedListener=onItemSelectedListener;
		   }
		   
		   @Override
		   public View getView(int position,View convertView,ViewGroup parent)
		   {
		      View row;
		      View clickContainer;
		      
		      if(convertView != null)
		      {
		         row=convertView;
		         clickContainer=row.findViewById(R.id.clickContainer);
		      }
		      else
		      {
		         //inflate layout
		         LayoutInflater inflater = ((Activity)getContext()).getLayoutInflater();
		         row=inflater.inflate(R.layout.imagelist_preference,parent,false);
		         clickContainer=row.findViewById(R.id.clickContainer);
		         clickContainer.setOnClickListener(new View.OnClickListener()
		         {
		            @Override
		            public void onClick(View v)
		            {
		               if(onItemSelectedListener!=null)
		               {
		                  onItemSelectedListener.onItemSelected(null,v,(Integer)v.getTag(),(Integer)v.getTag());
		               }
		               index=(Integer)v.getTag();
		               notifyDataSetChanged();
		            }
		         });
		      }
		      row.setTag(position);
		      clickContainer.setTag(position);

		      //set checkbox
		      RadioButton tb=(RadioButton)row.findViewById(R.id.radiobutton);
		      tb.setChecked(position==index);
		      tb.setClickable(false);
		      
		      ImageView img=(ImageView)row.findViewById(R.id.image);
		      if((imageResources.length>position)&&(imageResources[position]>0))
		      {
		         img.setVisibility(View.VISIBLE);
		         img.setImageDrawable(getContext().getResources().getDrawable(imageResources[position]));
				 //img.setImageDrawable(ContextCompat.getDrawable(getContext(),imageResources[position]));
		      }
		      else
		      {
		         img.setVisibility(View.INVISIBLE);
		      }
		      
		      return row;
		   }
		}
	}
