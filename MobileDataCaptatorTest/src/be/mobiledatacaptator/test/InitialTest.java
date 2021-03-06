package be.mobiledatacaptator.test;

import java.util.ArrayList;
import java.util.Random;

import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import be.mobiledatacaptator.activities.DisplayPhotoActivity;
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

			// SelectProjectActivity
			solo.assertCurrentActivity("SelectProjectActivity", SelectProjectActivity.class);
			ListView listViewProjects = (ListView) solo.getView(be.mobiledatacaptator.R.id.listViewProjects);
			int countProjects = listViewProjects.getCount();
			int randomProject = random.nextInt(countProjects) + 1;
			Log.e("SELECTED PROJECT", Integer.toString(randomProject));
			solo.clickInList(randomProject);
			solo.clickOnButton(solo.getString(be.mobiledatacaptator.R.string.button_open_project));

			// SelectFicheActivity
			solo.assertCurrentActivity("SelectFicheActivity", SelectFicheActivity.class);
			ListView listViewFiches = (ListView) solo.getView(be.mobiledatacaptator.R.id.listViewFiches);
			int countFiches = listViewFiches.getCount();

			Log.e("COUNT FICHES", Integer.toString(countFiches));
			int randomFiche = random.nextInt(countFiches);
			Log.e("SELECTED FICHE", Integer.toString(randomFiche));

			solo.clickOnView(getViewAtIndex(listViewFiches, randomFiche, getInstrumentation()));

			UnitOfWork unitOfWork = UnitOfWork.getInstance();
			Project project = unitOfWork.getActiveProject();

			int randomKeuze = 0; // random.nextInt(3);

			int randomYesNo = random.nextInt(1);

			switch (randomKeuze) {
			case 0: // open fiche
				Log.e("OpenFiche", "OpenFiche");

				solo.clickOnButton(solo.getString(be.mobiledatacaptator.R.string.button_open_fiche));

				if (randomYesNo == 0) {
					solo.clickOnButton(solo.getString(be.mobiledatacaptator.R.string.button_yes));

					ArrayList<View> vEditTextesults = solo.getCurrentViews(); 
					
					for (int i = 1; i <= vEditTextesults.size(); i++) {
						Log.e("VIEW", vEditTextesults.get(i).toString());
					}
					
					
					
					solo.enterText(1, "test");
					solo.goBackToActivity("SelectProjectActivity");
					
					
					solo.clickOnButton(solo.getString(be.mobiledatacaptator.R.string.dialog_go));


				} else {
					solo.clickOnButton(solo.getString(be.mobiledatacaptator.R.string.button_no));
				}

				try {
					Thread.sleep(5000);
				} catch (InterruptedException e1) {

				}

				again = true;

				break;
			case 1: // + sign
				Log.e("+Sign", "+Sign");

				break;
			case 2: // open photo
				Log.e("OpenFoto", "OpenFoto");

				if (project.isLoadPhotoActivity() != true) {
					Log.e("PROJECT - No Photos", project.getName());
					again = true;
					solo.goBack();
				} else {
					Log.e("PROJECT - photos", project.getName());

					solo.clickOnButton(solo.getString(be.mobiledatacaptator.R.string.button_open_photo));
					solo.assertCurrentActivity("TakePhotoActivity", TakePhotoActivity.class);

					ListView listViewPhotos = (ListView) solo.getView(be.mobiledatacaptator.R.id.listViewPhotos);
					int countPhotos = listViewPhotos.getCount();

					int randomButton = random.nextInt(2) + 1;

					switch (randomButton) {
					case 1: // open photo
						if (countPhotos > 0) {
							Log.e("COUNT PHOTOS", Integer.toString(countPhotos));
							int randomPhoto = random.nextInt(countPhotos);
							solo.clickOnView(getViewAtIndex(listViewPhotos, randomPhoto, getInstrumentation()));
							solo.clickOnButton(solo.getString(be.mobiledatacaptator.R.string.button_display_photo));
							solo.assertCurrentActivity("DisplayPhotoActivity", DisplayPhotoActivity.class);
							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {

							}
							solo.goBackToActivity("SelectProjectActivity");
							again = true;
						} else // no photos for this fiche
						{
							solo.goBackToActivity("SelectProjectActivity");
							again = true;
						}

						break;
					case 2: // delete photo
						if (countPhotos > 0) {
							Log.e("COUNT PHOTOS", Integer.toString(countPhotos));
							int randomPhoto = random.nextInt(countPhotos);
							solo.clickOnView(getViewAtIndex(listViewPhotos, randomPhoto, getInstrumentation()));
							solo.clickOnButton(solo.getString(be.mobiledatacaptator.R.string.button_delete_photo));

							randomYesNo = random.nextInt(1);

							if (randomYesNo == 0) {
								solo.clickOnButton(solo.getString(be.mobiledatacaptator.R.string.button_yes));
							} else {
								solo.clickOnButton(solo.getString(be.mobiledatacaptator.R.string.button_no));
							}

							try {
								Thread.sleep(3000);
							} catch (InterruptedException e) {

							}
							solo.goBackToActivity("SelectProjectActivity");
							again = true;
						} else // no photos for this fiche
						{
							solo.goBackToActivity("SelectProjectActivity");
							again = true;
						}

						try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {

						}

						solo.goBackToActivity("SelectProjectActivity");
						again = true;

						break;

					case 3: // click on take photo - photo category
						if (project.getPhotoCategories().size() > 0) {
							int randomCategory = random.nextInt(project.getPhotoCategories().size());
							String categoryName = project.getPhotoCategories().get(randomCategory).getName();

							solo.clickOnButton(categoryName);
						}
						break;

					case 4: // free suffix

						break;

					default:
						break;
					}

				}

				break;
			case 3: // open drawing
				Log.e("OpenDrawing", "OpenDrawing");

				break;

			default:
				break;
			}

		} while (again == true);

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
