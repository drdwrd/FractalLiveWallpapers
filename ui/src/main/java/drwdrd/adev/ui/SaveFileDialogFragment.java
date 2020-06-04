package drwdrd.adev.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SaveFileDialogFragment extends DialogFragment {

    public interface OnSaveListener {
        public void onSave(String fileName);
    }

    private View view=null;

    private static final String DEFAULT_FILE_NAME = "New File";
    private static final String DEFAULT_FILE_EXTENSION = "";
    private static final String DEFAULT_DIALOG_TITLE = "Enter File Name";

    private String defaultFileName = DEFAULT_FILE_NAME;
    private String dialogTitle = DEFAULT_DIALOG_TITLE;
    private String fileExtension = DEFAULT_FILE_EXTENSION;

    private String fileName=null;
    private OnSaveListener onSaveListener=null;

    public SaveFileDialogFragment() {
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

    public void setDefaultFileName(String defaultFileName) {
        this.defaultFileName=defaultFileName;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension=fileExtension;
    }

    public  void setOnSaveListener(OnSaveListener onSaveListener) {
        this.onSaveListener=onSaveListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        builder.setTitle(dialogTitle);
        View view=inflater.inflate(R.layout.save_file_dialog,null);
        ((TextView)view.findViewById(R.id.fileName)).setText(defaultFileName);
        builder.setView(view);
        builder.setPositiveButton("Save",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fileName=((EditText)getDialog().findViewById(R.id.fileName)).getText().toString();
                //add extension if not present
                if(!fileName.endsWith(fileExtension)) {
                    fileName=fileName.concat(fileExtension);
                }
                if(onSaveListener!=null) {
                    onSaveListener.onSave(fileName);
                }
            }
        });
        builder.setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                fileName=null;
                SaveFileDialogFragment.this.getDialog().cancel();
            }
        });
        return  builder.create();
    }
}
