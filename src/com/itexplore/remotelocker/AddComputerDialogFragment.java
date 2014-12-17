package com.itexplore.remotelocker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.itexplore.remotelocker.common.ComputerEntry;
import com.itexplore.remotelocker.common.ComputerEntryController;

public class AddComputerDialogFragment extends DialogFragment {
	
	private ComputerEntryController mComputerController;
	private String name;
	private String ip;
	
	public AddComputerDialogFragment() {
		super();
		
		this.mComputerController = ComputerEntryController.getInstance(getActivity());
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();		
		final View fView = inflater.inflate(R.layout.layout_computer_dialog, null);
		
		alertBuilder.setView(fView);
		
		final EditText fEdtName = (EditText)fView.findViewById(R.id.computer_dialog_edt_computer_name);
        final EditText fEdtIp = (EditText)fView.findViewById(R.id.computer_dialog_edt_computer_ip);
        final EditText fEdtIdCode = (EditText)fView.findViewById(R.id.computer_dialog_edt_computer_idcode);
        final CheckBox fChkType = (CheckBox)fView.findViewById(R.id.computer_dialog_chk_computer_type);
        
        if(name != null)
            fEdtName.setText(name);
        
        if(ip != null)
            fEdtIp.setText(ip);
		
		alertBuilder.setTitle(R.string.computer_dialog_add_title);
		
		alertBuilder.setPositiveButton(R.string.computer_dialog_ok, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {				
				ComputerEntry entry = new ComputerEntry();
				entry.setName(fEdtName.getText().toString());
				entry.setIP(fEdtIp.getText().toString());
				entry.setIdentifyCode(fEdtIdCode.getText().toString());
				entry.setType(fChkType.isChecked() ? ComputerEntry.TYPE_REMOTE : ComputerEntry.TYPE_LOCAL);
				
				mComputerController.setEntryData(entry);
				
				AddComputerDialogFragment.this.getDialog().dismiss();
				
				if(mComputerController.insert() == -1) {
					Toast.makeText(getActivity(), getString(R.string.db_proccess_error_msg), Toast.LENGTH_SHORT).show();
					return;
				}
				
				Toast.makeText(getActivity(), getString(R.string.computer_dialog_add_success_msg), Toast.LENGTH_SHORT).show();
				
				if(getActivity().getClass() == MainActivity.class) {
					((MainActivity)getActivity()).updateListFragment();
				}
			}
		});
		
		alertBuilder.setNegativeButton(R.string.computer_dialog_cancel, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				AddComputerDialogFragment.this.getDialog().cancel();
			}
		});
		
		return alertBuilder.create();
	}
	
	public void setEntry(String name, String ip) {
	    this.name = name;
	    this.ip = ip;
	}

}
