package com.tenmiles.helpstack.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.android.volley.Response.ErrorListener;
import com.android.volley.VolleyError;
import com.tenmiles.helpstack.R;
import com.tenmiles.helpstack.helper.HSBaseExpandableListAdapter;
import com.tenmiles.helpstack.logic.HSSource;
import com.tenmiles.helpstack.logic.OnFetchedArraySuccessListener;
import com.tenmiles.helpstack.model.HSTicket;
import com.tenmiles.helpstack.model.HSTicketUpdate;

public class IssueDetailFragment extends HSFragmentParent 
{

	public IssueDetailFragment() {
		// TODO Auto-generated constructor stub
	}
	
	private HSTicket ticket;
	private ExpandableListView mExpandableListView;
	private LocalAdapter mAdapter;
	private HSSource gearSource;
	
	private HSTicketUpdate[] fetchedUpdates;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		View rootView = inflater.inflate(R.layout.fragment_issue_detail, null);
		
		mExpandableListView = (ExpandableListView) rootView.findViewById(R.id.expandableList); 
        mAdapter = new LocalAdapter(getActivity());
        
        mExpandableListView.setAdapter(mAdapter);
        
        gearSource = new HSSource(getActivity());
		
		return rootView;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		if (savedInstanceState == null ) {
			refreshUpdateFromServer();
		}
		else {
			fetchedUpdates = (HSTicketUpdate[]) savedInstanceState.getSerializable("updates");
		}
		
		refreshList();
	}
	
	private void refreshList() {
		
		mAdapter.clearAll();
		
		if (fetchedUpdates != null) {
			mAdapter.addParent(1, "");
			for (int i = 0; i < fetchedUpdates.length; i++) {
				mAdapter.addChild(1, fetchedUpdates[i]);
			}
		}
		
		mAdapter.notifyDataSetChanged();
		
		expandAll();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("updates", fetchedUpdates);
	}

	
	private void refreshUpdateFromServer() {
		
		getHelpStackActivity().setProgressBarIndeterminateVisibility(true);
		
		gearSource.requestAllUpdatesOnTicket(ticket, new OnFetchedArraySuccessListener() {
			
			@Override
			public void onSuccess(Object[] successObject) {
				
				fetchedUpdates = (HSTicketUpdate[]) successObject;
				
				refreshList();
				
				getHelpStackActivity().setProgressBarIndeterminateVisibility(false);
			}
		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				
				getHelpStackActivity().setProgressBarIndeterminateVisibility(false);
			}
		});
	}
	
	private void expandAll() {
		int count = mAdapter.getGroupCount();
		for (int i = 0; i < count; i++) {
			mExpandableListView.expandGroup(i);
		}
	}


	private class LocalAdapter extends HSBaseExpandableListAdapter 
	{
		public LocalAdapter(Context context) {
			super(context);
		}

		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			ChildViewHolder holder;
			if (convertView == null) {
				holder = new ChildViewHolder();
				if (getChildType(groupPosition, childPosition) == 0) {
					convertView = mLayoutInflater.inflate(R.layout.expandable_child_issue_detail_staff_reply, null);
				}
				else {
					convertView = mLayoutInflater.inflate(R.layout.expandable_child_issue_detail_user_reply, null);
				}
				
				holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
				
				
				convertView.setTag(holder);
			}
			else {
				holder = (ChildViewHolder) convertView.getTag();
			}
			
			// This is a dummy view as only 1 group is gonna be used.
			HSTicketUpdate update = (HSTicketUpdate) getChild(groupPosition, childPosition);
			
			holder.textView1.setText(update.getText());
			
			return convertView;
		}

		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			ParentViewHolder holder;
			if (convertView == null) {
				convertView = mLayoutInflater.inflate(R.layout.expandable_parent_issue_detail_default, null);
				holder = new ParentViewHolder();
				holder.parent = convertView;
				
				convertView.setTag(holder);
			}
			else {
				holder = (ParentViewHolder) convertView.getTag();
			}
			
			// This is a dummy view as only 1 group is gonna be used.
			holder.parent.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
				}
			});
			
			return convertView;
		}
		
		@Override
		public int getChildTypeCount() {
			return 2;
		}
		
		@Override
		public int getChildType(int groupPosition, int childPosition) {
			HSTicketUpdate update = (HSTicketUpdate) getChild(groupPosition, childPosition);
			return update.isStaffUpdate()?0:1;
		}
		
		private class ParentViewHolder {
			View parent;
		}
		
		private class ChildViewHolder {
			public TextView textView1;
		}
	}

	/**
	 * @return the ticket
	 */
	public HSTicket getTicket() {
		return ticket;
	}


	/**
	 * @param ticket the ticket to set
	 */
	public void setTicket(HSTicket ticket) {
		this.ticket = ticket;
	}
	
}
