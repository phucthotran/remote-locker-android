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

public class EditComputerDialogFragment extends DialogFragment {

	private ComputerEntryController mComputerController;
	private int mEditId;
	
	public EditComputerDialogFragment() {
		super();
		
		this.mComputerController = ComputerEntryController.getInstance(getActivity());
	}
	
	public void editId(int id) {		
		this.mEditId = id;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();		
		final View fView = inflater.inflate(R.layout.layout_computer_dialog, null);
		
		final EditText fEdtName = (EditText)fView.findViewById(R.id.computer_dialog_edt_computer_name);
		final EditText fEdtIp = (EditText)fView.findViewById(R.id.computer_dialog_edt_computer_ip);
		final EditText fEdtIdCode = (EditText)fView.findViewById(R.id.computer_dialog_edt_computer_idcode);
		final CheckBox fChkType = (CheckBox)fView.findViewById(R.id.computer_dialog_chk_computer_type);
		
		final ComputerEntry fSavedEntry = mComputerController.get(mEditId);
		
		fEdtName.setText(fSavedEntry.getName());
		fEdtIp.setText(fSavedEntry.getIP());
		fEdtIdCode.setText(fSavedEntry.getIdentifyCode());
		fChkType.setChecked(fSavedEntry.getType().equalsIgnoreCase(ComputerEntry.TYPE_REMOTE));
		
		alertBuilder.setView(fView);
		alertBuilder.setTitle(R.string.computer_dialog_edit_title);
		
		alertBuilder.setPositiveButton(R.string.computer_dialog_ok, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {				
				
				fSavedEntry.setName(fEdtName.getText().toString());
				fSavedEntry.setIP(fEdtIp.getText().toString());
				fSavedEntry.setIdentifyCode(fEdtIdCode.getText().toString());
				fSavedEntry.setType(fChkType.isChecked() ? ComputerEntry.TYPE_REMOTE : ComputerEntry.TYPE_LOCAL);
				
				mComputerController.setEntryData(fSavedEntry);				
				
				EditComputerDialogFragment.this.getDialog().dismiss();
				
				if(!mComputerController.update()) {
					Toast.makeText(getActivity(), getString(R.string.db_proccess_error_msg), Toast.LENGTH_SHORT).show();
					return;
				}
				
				Toast.makeText(getActivity(), getString(R.string.computer_dialog_edit_success_msg), Toast.LENGTH_SHORT).show();
				
				if(getActivity().getClass() == MainActivity.class) {
					((MainActivity)getActivity()).updateListFragment();
				}				
			}
		});
		
		alertBuilder.setNegativeButton(R.string.computer_dialog_cancel, new DialogInterface.OnClickListener() {			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditComputerDialogFragment.this.getDialog().cancel();
			}
		});
		
		return alertBuilder.create();
	}
	
}
