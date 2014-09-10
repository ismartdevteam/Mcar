package mn.ismartdev.mcar.fragment;

import java.sql.SQLException;
import java.util.List;

import mn.ismartdev.mcar.R;
import mn.ismartdev.mcar.adapter.CompanyAdapter;
import mn.ismartdev.mcar.model.Company;
import mn.ismartdev.mcar.model.DatabaseHelper;

import org.lucasr.twowayview.TwoWayView;
import org.lucasr.twowayview.widget.DividerItemDecoration;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFrag extends Fragment {
	private TwoWayView companyList;
	private DatabaseHelper helper;
	private List<Company> comData;
	private CompanyAdapter comAdapter;

	public HomeFrag() {
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		helper = new DatabaseHelper(getActivity());
		try {
			comData = helper.getComDao().queryBuilder().orderBy("order", false).orderBy("type_id", false).query();
			comAdapter = new CompanyAdapter(getActivity(), companyList, comData);

			companyList.setAdapter(comAdapter);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_main, container,
				false);
		companyList = (TwoWayView) rootView.findViewById(R.id.company_list);
		companyList.setHasFixedSize(true);
		companyList.setLongClickable(true);
//		final Drawable divider = getResources().getDrawable(R.drawable.divider);
//		companyList.addItemDecoration(new DividerItemDecoration(divider));
		return rootView;
	}

}
