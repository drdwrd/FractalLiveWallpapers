package drwdrd.adev.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

public class ListViewDialogFragment extends DialogFragment {

    public interface OnItemClickListener {
        public void onItemClick(String item);
    }

    private ArrayList<String> itemList=new ArrayList<String>();
    private OnItemClickListener onItemClickListener=null;
    private String dialogTitle="";

    public ListViewDialogFragment() {
        super();
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener=onItemClickListener;
    }

    public void setTitle(String title) {
        this.dialogTitle=title;
    }

    public String getTitle() {
        return dialogTitle;
    }

    public ArrayList<String> getItemList() {
        return itemList;
    }

    public void setItemList(ArrayList<String> itemList) {
        this.itemList=itemList;
    }

    public void setItemList(String[] itemList) {
        this.itemList=new ArrayList<String>(Arrays.asList(itemList));
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setTitle(dialogTitle);
        View view=inflater.inflate(R.layout.load_file_dialog,null);
        ListView listView=(ListView)view.findViewById(R.id.fileList);
        listView.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,itemList));
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListView listView=(ListView)adapterView;
                listView.setItemChecked(i, true);
                if(onItemClickListener!=null) {
                    onItemClickListener.onItemClick(itemList.get(i));
                }
                ListViewDialogFragment.this.getDialog().dismiss();
            }
        });
        builder.setView(view);
        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ListViewDialogFragment.this.getDialog().cancel();
            }
        });
        return  builder.create();
    }
}
