package com.example.kartik.foodmanagement;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class OrgRegisterActivity extends FragmentActivity implements OnMapReadyCallback {

	private GoogleMap mMap;
	private LatLng latLng;
	private float DEFAULT_ZOOM;
	private ProgressDialog progressDialog;
	private EditText editName,editMob,editAdd,editPass,editRepPass,editRange;
	private TextInputLayout nameLay,mobLay,addLay,passLay,repPassLay,rangeLay;
	private Button btnGoBack,btnSignup;
	private final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Organizations");
	private FirebaseAuth firebaseAuth;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_org_register);

		firebaseAuth = FirebaseAuth.getInstance();
		editName=findViewById(R.id.editName);
		editMob=(EditText) findViewById(R.id.editMob);
		editAdd=findViewById(R.id.editAdd);
		editPass=(EditText) findViewById(R.id.editPass);
		editRepPass=(EditText) findViewById(R.id.editRepPass);
		editRange=findViewById(R.id.editRange);

		btnGoBack=findViewById(R.id.btnGoBack);
		btnSignup=findViewById(R.id.btnSignup);

		nameLay=findViewById(R.id.name_text_input1);
		mobLay=findViewById(R.id.name_text_input2);
		addLay=findViewById(R.id.name_text_input3);
		passLay=findViewById(R.id.name_text_input4);
		repPassLay=findViewById(R.id.name_text_input5);
		rangeLay=findViewById(R.id.name_text_input6);

		progressDialog=new ProgressDialog(this);
		progressDialog.setCancelable(false);

		FirebaseApp.initializeApp(this);

		// Obtain the SupportMapFragment and get notified when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
		DEFAULT_ZOOM=14.0f;
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;
		mMap.getUiSettings().setAllGesturesEnabled(true);
		mMap.getUiSettings().setScrollGesturesEnabledDuringRotateOrZoom(true);
		mMap.getUiSettings().setCompassEnabled(true);
		mMap.getUiSettings().setMyLocationButtonEnabled(true);
		mMap.getUiSettings().setMapToolbarEnabled(true);
		mMap.setBuildingsEnabled(true);
		mMap.setIndoorEnabled(true);
		mMap.setTrafficEnabled(true);
		mMap.setMyLocationEnabled(true);

		// Add a marker in Prayagraj and move the camera
		latLng = new LatLng(25.494635, 81.867338);
		mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Prayagraj")).setDraggable(true);
		mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));

		mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng latLng1) {
				DEFAULT_ZOOM=mMap.getCameraPosition().zoom;
				mMap.clear();
				latLng = latLng1;
				mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Prayagraj")).setDraggable(true);
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));
			}
		});
		mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
			@Override
			public void onMarkerDragStart(Marker marker) {
				DEFAULT_ZOOM=mMap.getCameraPosition().zoom;
			}

			@Override
			public void onMarkerDrag(Marker marker) {

			}

			@Override
			public void onMarkerDragEnd(Marker marker) {
				latLng=marker.getPosition();
				mMap.addMarker(new MarkerOptions().position(latLng).title("Marker in Prayagraj")).setDraggable(true);
				mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM));
			}
		});
		mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
			@Override
			public void onCameraMove() {
				DEFAULT_ZOOM=mMap.getCameraPosition().zoom;
			}
		});

		btnGoBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		btnSignup.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				checkInputs();
			}
		});

	}

	private void checkInputs() {
		String name,mobNo,add,pass,repPass,range;
		name=editName.getText().toString();
		mobNo=editMob.getText().toString().trim();
		add=editAdd.getText().toString().trim();
		pass=editPass.getText().toString().trim();
		repPass= editRepPass.getText().toString().trim();
		range=editRange.getText().toString();

		if(name.isEmpty())
		{
			nameLay.setError("Enter valid name");
			editName.requestFocus();
			return;
		}
		if(mobNo.isEmpty()) {
			mobLay.setError("Enter valid email");
			editMob.requestFocus();
			return;
		}
		if(add.isEmpty())
		{
			addLay.setError("Enter valid address");
			editAdd.requestFocus();
			return;
		}
		if(pass.length()<6)
		{
			passLay.setError("Enter at least 6 length password");
			editPass.requestFocus();
			return;
		}
		if(!pass.equals(repPass))
		{
			repPassLay.setError("Password doesn't match");
			editRepPass.requestFocus();
			return;
		}
		if(range.isEmpty())
		{
			rangeLay.setError("Please enter valid range");
			editRange.requestFocus();
			return;
		}
		if(latLng.equals(new LatLng(25.494635, 81.867338))) {
			Toast.makeText(this,"Please select starting location!",Toast.LENGTH_LONG).show();
			return;
		}
		progressDialog.setMessage("Registering...");
		progressDialog.show();
		register(name,mobNo,add,pass,Double.parseDouble(range),latLng.latitude,latLng.longitude);
	}

	private void register(String name, final String mobNo, String add, final String pass, double range, double latitude, double longitude) {

		String semail = mobNo;
		String spassword = pass;

		System.out.println("**********************TAG :: " + semail + " " + spassword);

		//creating a new user
		firebaseAuth.createUserWithEmailAndPassword(semail, spassword)
				.addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
					@Override
					public void onComplete(@NonNull Task<AuthResult> task) {

						System.out.println(task.getException());

						//checking if success
						if(task.isSuccessful()){
							// finish();
							Toast.makeText(OrgRegisterActivity.this,"Registered successfully!",Toast.LENGTH_LONG).show();
							FirebaseAuth currentFirebaseUser = FirebaseAuth.getInstance();

							MessageDigest digest = null;
							try {
								digest = MessageDigest.getInstance("SHA-256");
							} catch (NoSuchAlgorithmException e) {
								e.printStackTrace();
							}
							assert digest != null;
							String pass1 = pass;
							byte[] tempPass = digest.digest(pass1.getBytes(StandardCharsets.UTF_8));
							pass1 = Arrays.toString(tempPass);    // pass is now Hashed

							final Organization organization=new Organization(add,latitude,longitude,mobNo.replaceAll("[-+.^:,@*]",""),name,range);

							databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
								@Override
								public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
									if(dataSnapshot.child(organization.getMobNo()).exists()) {
										progressDialog.cancel();
										openDialog(organization);
									}
									else {
										String resultemail = organization.getMobNo();
										databaseReference.child(resultemail).setValue(organization);
										Toast.makeText(OrgRegisterActivity.this,"Organization added!",Toast.LENGTH_LONG).show();
										progressDialog.cancel();
										currentFirebaseUser.signOut();
										startActivity(new Intent(OrgRegisterActivity.this, LoginActivity.class));
										finish();
									}
								}

								@Override
								public void onCancelled(@NonNull DatabaseError databaseError) {
									progressDialog.cancel();
									Toast.makeText(OrgRegisterActivity.this,"Some error occurred!",Toast.LENGTH_LONG).show();
								}
							});

						}else{
							//display some message here
							new SweetAlertDialog(OrgRegisterActivity.this, SweetAlertDialog.ERROR_TYPE)
									.setTitleText("Oops...")
									.setContentText("Registration error!")
									.show();

							FirebaseAuthException e = (FirebaseAuthException )task.getException();
							Toast.makeText(OrgRegisterActivity.this, "Failed Registration: "+e.getMessage(), Toast.LENGTH_SHORT).show();
						}
						progressDialog.dismiss();
					}
				});
	}

	private void openDialog(final Organization organization) {
		AlertDialog alertDialog = new AlertDialog.Builder(this).create();

		TextView title = new TextView(this);

		title.setText("");
		title.setPadding(10, 10, 10, 10);   // Set Position
		title.setGravity(Gravity.CENTER);
		title.setTextColor(Color.BLACK);
		title.setTextSize(20);
		alertDialog.setCustomTitle(title);

		TextView msg = new TextView(this);

		msg.setText("    ORGANIZATION ALREADY EXISTS");
		msg.setTextColor(Color.BLACK);
		msg.setTextSize(20);
		alertDialog.setView(msg);

		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, " UPDATE DATA ", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialogInterface, int i) {
				databaseReference.child(organization.getMobNo()).setValue(organization);
				Toast.makeText(OrgRegisterActivity.this,"Data Updated",Toast.LENGTH_LONG).show();
				finish();
			}
		});

		alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL,"CANCEL  ", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				// No need to write anything here
			}
		});
		new Dialog(getApplicationContext());
		alertDialog.show();

		// Set Properties for OK Button
		final Button okBT = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
		LinearLayout.LayoutParams neutralBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
		neutralBtnLP.gravity = Gravity.FILL_HORIZONTAL;
		okBT.setPadding(50, 10, 10, 10);   // Set Position
		okBT.setTextColor(Color.BLUE);
		okBT.setLayoutParams(neutralBtnLP);

		final Button cancelBT = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
		LinearLayout.LayoutParams negBtnLP = (LinearLayout.LayoutParams) okBT.getLayoutParams();
		negBtnLP.gravity = Gravity.FILL_HORIZONTAL;
		cancelBT.setTextColor(Color.RED);
		cancelBT.setLayoutParams(negBtnLP);
	}
}
