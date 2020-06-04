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

public class LoadFileDialogFragment extends DialogFragment {

    public interface OnLoadListener {
        public void onLoad(String fileName);
    }

    private static final String DEFAULT_FILE_EXTENSION = "";
    private static final String DEFAULT_DIALOG_TITLE = "Select file ...";

    private String dialogTitle = DEFAULT_DIALOG_TITLE;
    private String fileExtension = DEFAULT_FILE_EXTENSION;

    private String fileName=null;
    private OnLoadListener onLoadListener=null;

    public LoadFileDialogFragment() {
        super();
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public void setTitle(String dialogTitle) {
        this.dialogTitle=dialogTitle;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension=fileExtension;
    }

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener=onLoadListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setTitle(dialogTitle);
        View view=inflater.inflate(R.layout.load_file_dialog,null);
        ListView listView=(ListView)view.findViewById(R.id.fileList);
        listView.setAdapter(new ArrayAdapter<String>(getActivity(),android.R.layout.simple_list_item_1,getFilesList()));
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ListView listView=(ListView)adapterView;
                listView.setItemChecked(i, true);
                fileName=listView.getItemAtPosition(i).toString();
                if(!fileName.endsWith(fileExtension)) {
                    fileName=fileName.concat(fileExtension);
                }
                if(onLoadListener!=null) {
                    onLoadListener.onLoad(fileName);
                }
                LoadFileDialogFragment.this.getDialog().dismiss();
            }
        });
        builder.setView(view);
        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fileName=null;
                LoadFileDialogFragment.this.getDialog().cancel();
            }
        });
        return  builder.create();
    }

    public ArrayList<String> getFilesList() {

        String[] savedFiles=getActivity().fileList();

        ArrayList<String> filesList=new ArrayList<String>();

        int count=0;
        for (String file: savedFiles) {
            if (file.endsWith(fileExtension)) {
                filesList.add(file);
            }
        }
        return filesList;
    }
}
