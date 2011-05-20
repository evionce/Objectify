package de.hsrm.objectify.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import de.hsrm.objectify.MainActivity;
import de.hsrm.objectify.R;
import de.hsrm.objectify.utils.ImageHelper;

/**
 * A base activity that implements common functionality across app activities
 * such as the actionbar. This class mustn't be used directly, instead
 * activities should inherit from
 * 
 * @author kwolf001
 * 
 */
public abstract class BaseActivity extends Activity {

	private ViewGroup content;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_empty);
		content = (ViewGroup) super.findViewById(R.id.layout_container);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	@Override
	public void setContentView(int layoutResID) {
		content.removeAllViews();
		getLayoutInflater().inflate(layoutResID, content);
	}
	
	@Override
	public View findViewById(int id) {
		return content.findViewById(id);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	/**
	 * Sets up the actionbar with the given title and the color. If the title is
	 * null, then the app name will be shown instead of the title. Otherwise, a
	 * home button and the given title are visible. If the color is null, then
	 * the default color is visible.
	 * 
	 * @param title title of actionbar
	 * @param color color of title
	 */
	protected void setupActionBar(CharSequence title, int color) {
		final ViewGroup actionBar = getActionBar();
		if (actionBar == null) {
			return;
		}
		
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.FILL_PARENT);
		layoutParams.weight = 1;
		
		View.OnClickListener homeClickListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				backToMain();
			}
		};
		
		if (title != null) {
			// adding home button
			addActionButton(R.drawable.ic_title_home, R.string.home, homeClickListener, true);
			
			// adding title text
			TextView titleText = new TextView(this, null, R.attr.actionbarTextStyle);
			titleText.setLayoutParams(layoutParams);
			titleText.setText(title);
			actionBar.addView(titleText);
		} else {
			// adding app name
			TextView appName = new TextView(this, null, R.attr.actionbarTextStyle);
			appName.setText(getString(R.string.app_name));
			actionBar.addView(appName);
			
			// adding layout for aligning items to the right
			View dummy = new View(this);
			dummy.setLayoutParams(layoutParams);
			actionBar.addView(dummy);
		}
	}
	
	/**
	 * Returns the {@link ViewGroup} for the actionbar. May return null.
	 */
	protected ViewGroup getActionBar() {
		return (ViewGroup) super.findViewById(R.id.actionbar);
	}
	
	/**
	 * Disables the actionbar.
	 */
	protected void disableActionBar() {
		ViewGroup actionBar = (ViewGroup) super.findViewById(R.id.actionbar);
		actionBar.setVisibility(View.GONE);
	}
	
	/**
	 * Interface for activities who need to draw action buttons on the action bar
	 * @param iconResId ressource identifier for icon
	 * @param textResId ressource identifier for text
	 * @param clickListener triggered listener when pressing the button
	 */
	protected void addNewActionButton(int iconResId, int textResId, View.OnClickListener clickListener) {
		addActionButton(iconResId, textResId, clickListener, false);
	}
	
	/**
	 * Adds new action button onto the action bar and takes car of drawing the separator between each buttons on the right place
	 * @param iconResId resource identifier for icon
	 * @param textResId resource identifier for text
	 * @param clickListener triggered listener when pressing the button
	 * @param separatorAfter true if there is another button following, else false
	 * @return new action button view
	 */
	private View addActionButton(int iconResId, int textResId, View.OnClickListener clickListener, boolean separatorAfter) {
		final ViewGroup actionBar = getActionBar();
		
		// button separator
		ImageView separator = new ImageView(getApplicationContext(), null, R.attr.actionbarSeparatorStyle);
		separator.setLayoutParams(new ViewGroup.LayoutParams(2, ViewGroup.LayoutParams.FILL_PARENT));
		separator.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_separator));
		
		// new action button
		ImageButton actionButton = new ImageButton(this, null, R.attr.actionbarButtonStyle);
		actionButton.setImageResource(iconResId);
		actionButton.setContentDescription(this.getResources().getString(textResId));
		actionButton.setOnClickListener(clickListener);
		
		// adding separator properly
//		if (!separatorAfter)
//			actionBar.addView(separator);
		
		actionBar.addView(separator);
		actionBar.addView(actionButton);
		
//		if (separatorAfter)
//			actionBar.addView(separator);
		
		return actionButton;
	}
	
//	private View addActionButtonHome(int iconResId, int textResId, View.OnClickListener clickListener) {
//		ImageButton actionButton = new ImageButton(this);
//		actionButton.setLayoutParams(new ViewGroup.LayoutParams((int) this.getResources().getDimension(
//				R.dimen.actionbar_height), ViewGroup.LayoutParams.FILL_PARENT));
//		actionButton.setImageResource(iconResId);
//		actionButton.setScaleType(ImageView.ScaleType.CENTER);
//		actionButton.setContentDescription(this.getResources().getString(textResId));
//		actionButton.setOnClickListener(clickListener);
//		
//		return actionButton;
//	}
	
	/**
	 * Starting home activity, returning to
	 * {@link MainActivity }.
	 */
	private void backToMain() {
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(intent);
	}
}
