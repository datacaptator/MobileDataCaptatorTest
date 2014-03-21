package be.mobiledatacaptator.test;

import java.util.Random;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import be.mobiledatacaptator.activities.SelectFicheActivity;
import be.mobiledatacaptator.activities.SelectProjectActivity;
import be.mobiledatacaptator.activities.TakePhotoActivity;
import be.mobiledatacaptator.model.Project;
import be.mobiledatacaptator.model.UnitOfWork;

import com.robotium.solo.Solo;

public class InitialTest extends ActivityInstrumentationTestCase2<SelectProjectActivity> {

	private Solo solo;
	private Random random;
	private boolean again;
	Project project;

	public InitialTest() {
		super("be.mobiledatacaptator.activities", SelectProjectActivity.class);
	}

	protected void setUp() throws Exception {
		super.setUp();
		solo = new Solo(getInstrumentation(), getActivity());
		random = new Random();
	}

	public void testClickButton() {

		do {
			again = false;
			
			solo.assertCurrentActivity("SelectProjectActivity", SelectProjectActivity.class);

			ListView listViewProjects = (ListView) solo.getView(be.mobiledatacaptator.R.id.listViewProjects);
			int countProjects = listViewProjects.getCount();
			Log.e("COUNT PROJECTS", Integer.toString(countProjects));
			int randomProject = random.nextInt(countProjects - 1) + 1;
			Log.e("SELECTED PROJECT", Integer.toString(randomProject));
			solo.clickInList(randomProject);

			solo.clickOnButton("Open project");

			solo.assertCurrentActivity("SelectFicheActivity", SelectFicheActivity.class);

			ListView listViewFiches = (ListView) solo.getView(be.mobiledatacaptator.R.id.listViewFiches);
		
			int countFiches = listViewFiches.getCount(); // returns always null
		
			Log.e("COUNT FICHES", Integer.toString(countFiches));
			int randomFiche = random.nextInt(10)+1;
			Log.e("SELECTED FICHE", Integer.toString(randomFiche));

			solo.clickOnView(getViewAtIndex(listViewFiches, randomFiche, getInstrumentation()));

			UnitOfWork unitOfWork = UnitOfWork.getInstance();
			Project project = unitOfWork.getActiveProject();
			
			if (project.isLoadPhotoActivity() != true) {
				Log.e("PROJECT - no photos", project.getName());
				again = true;
				solo.goBack();
			}
			else{
				Log.e("PROJECT - photos", project.getName());
			}

		} while (again == true);
				
		solo.clickOnButton("Open Foto");

		solo.assertCurrentActivity("TakeProjectActivity", TakePhotoActivity.class);
		solo.clickOnButton("Omgevingsfoto");

	}

	
	// /////////////////////////////////////////////////////////////////////////////////////
	// Helper functions to select item in listview not visible on screen
	public View getViewAtIndex(final ListView listElement, final int indexInList, Instrumentation instrumentation) {
		ListView parent = listElement;
		if (parent != null) {
			if (indexInList <= parent.getAdapter().getCount()) {
				scrollListTo(parent, indexInList, instrumentation);
				int indexToUse = indexInList - parent.getFirstVisiblePosition();
				return parent.getChildAt(indexToUse);
			}
		}
		return null;
	}

	public <T extends AbsListView> void scrollListTo(final T listView, final int index, Instrumentation instrumentation) {
		instrumentation.runOnMainSync(new Runnable() {
			@Override
			public void run() {
				listView.setSelection(index);
			}
		});
		instrumentation.waitForIdleSync();
	}
	// /////////////////////////////////////////////////////////////////////////////////////
}
