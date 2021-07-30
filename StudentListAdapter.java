package com.rollermine.unknownapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.SystemClock;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.ExampleViewHolder> {
    private final ArrayList<StudentListItem> mStudentList;

    FragmentActivity fragmentActivity;

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {

        private RequestQueue queue;
        FragmentActivity f;

        public TextView mAvatarSmall;
        public TextView mStudentNameValueSmall;
        public TextView mCleanablesValueSmall;
        public TextView mUncleanablesValueSmall;
        public TextView mStupid;
        public TextView mMess;
        public ConstraintLayout mMiscHolderSmall;
        public ConstraintLayout mMoreHolderSmall;
        public TextView statusInfoSmall;
        public ImageView statusImageSmall;
        long mLastClickTime;
        String oldCle, oldUncle;
        boolean boo = false;

        public ExampleViewHolder(FragmentActivity f, View itemView) {
            super(itemView);
            this.f = f;

            queue = Volley.newRequestQueue(f);
            Vibrator vibrator = (Vibrator) f.getSystemService(Context.VIBRATOR_SERVICE);

            mAvatarSmall = itemView.findViewById(R.id.avatarSmallText);
            mStupid = itemView.findViewById(R.id.dumbestThingEver);
            mStudentNameValueSmall = itemView.findViewById(R.id.studentNameValueSmall);
            mCleanablesValueSmall = itemView.findViewById(R.id.cleanablesValueSmall);
            mUncleanablesValueSmall = itemView.findViewById(R.id.uncleanablesValueSmall);
            mMess = itemView.findViewById(R.id.messyMess);

            mMiscHolderSmall = itemView.findViewById(R.id.miscHolderSmall);
            mMoreHolderSmall = itemView.findViewById(R.id.moreHolderSmall);

            statusInfoSmall = itemView.findViewById(R.id.statusInfoTextViewSmall);
            statusImageSmall = itemView.findViewById(R.id.statusImageViewDetailedOP);

            mMoreHolderSmall.setOnClickListener(v -> {
                mMoreHolderSmall.setVisibility(View.GONE);
                mMiscHolderSmall.setVisibility(View.VISIBLE);
            });

            Button btnClPlusSmall = itemView.findViewById(R.id.buttonClPlusSmall);
            btnClPlusSmall.setOnClickListener(v -> {
                String currentCleanables = mCleanablesValueSmall.getText().toString();
                String currentUncleanables = mUncleanablesValueSmall.getText().toString();
                if (!boo) {
                    oldCle = currentCleanables;
                    oldUncle = currentUncleanables;
                    boo = true;
                }

                String clean = add(currentCleanables, "10");
                mCleanablesValueSmall.setText(clean);

                customizeSmall(clean, currentUncleanables);

                mMiscHolderSmall.setVisibility(View.VISIBLE);
                mMoreHolderSmall.setVisibility(View.GONE);

                vibrator.vibrate(75);
            });

            Button btnClMinusSmall = itemView.findViewById(R.id.buttonClMinusSmall);
            btnClMinusSmall.setOnClickListener(v -> {
                String currentCleanables = mCleanablesValueSmall.getText().toString();
                String currentUncleanables = mUncleanablesValueSmall.getText().toString();
                if (!boo) {
                    oldCle = currentCleanables;
                    oldUncle = currentUncleanables;
                    boo = true;
                }

                String clean = subtract(currentCleanables, "5");
                mCleanablesValueSmall.setText(clean);

                customizeSmall(clean, currentUncleanables);

                mMiscHolderSmall.setVisibility(View.VISIBLE);
                mMoreHolderSmall.setVisibility(View.GONE);

                vibrator.vibrate(75);
            });

            Button btnUnclPlusSmall = itemView.findViewById(R.id.buttonUnclPlusSmall);
            btnUnclPlusSmall.setOnClickListener(v -> {
                String currentCleanables = mCleanablesValueSmall.getText().toString();
                String currentUncleanables = mUncleanablesValueSmall.getText().toString();
                if (!boo) {
                    oldCle = currentCleanables;
                    oldUncle = currentUncleanables;
                    boo = true;
                }

                String unclean = add(currentUncleanables, "10");
                mUncleanablesValueSmall.setText(unclean);

                customizeSmall(currentCleanables, unclean);

                mMiscHolderSmall.setVisibility(View.VISIBLE);
                mMoreHolderSmall.setVisibility(View.GONE);

                vibrator.vibrate(75);
            });

            Button btnUnclMinusSmall = itemView.findViewById(R.id.buttonUnclMinusSmall);
            btnUnclMinusSmall.setOnClickListener(v -> {
                String currentCleanables = mCleanablesValueSmall.getText().toString();
                String currentUncleanables = mUncleanablesValueSmall.getText().toString();
                if (!boo) {
                    oldCle = currentCleanables;
                    oldUncle = currentUncleanables;
                    boo = true;
                }

                String unclean = subtract(currentUncleanables, "5");
                mUncleanablesValueSmall.setText(unclean);

                customizeSmall(currentCleanables, unclean);

                mMiscHolderSmall.setVisibility(View.VISIBLE);
                mMoreHolderSmall.setVisibility(View.GONE);

                vibrator.vibrate(75);
            });

            Button btnSaveSmall = itemView.findViewById(R.id.buttonSaveSmall);
            btnSaveSmall.setOnClickListener(v -> {
                Calendar c = Calendar.getInstance();
                int timeOfDay = c.get(Calendar.HOUR_OF_DAY);
                if (timeOfDay > 22 || timeOfDay < 5) {
                    new AlertDialog.Builder(f)
                            .setTitle("It's sleep time")
                            .setIcon(R.drawable.ic_night)
                            .setMessage("App does not operate between 11:00 PM and 5:00 AM for reasons that are better left unsaid. Sorry for the inconvenience. Meanwhile, you should get some rest too")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    f.finish();
                                    System.exit(0);
                                }
                            })
                            .setCancelable(false).show();
                }
                else {
                    oldCle = mCleanablesValueSmall.getText().toString();
                    oldUncle = mUncleanablesValueSmall.getText().toString();

                    saveChanges();
                    mMiscHolderSmall.setVisibility(View.GONE);
                    mMoreHolderSmall.setVisibility(View.VISIBLE);
                }
            });

            Button btnDiscardSmall = itemView.findViewById(R.id.buttonDiscardSmall);
            btnDiscardSmall.setOnClickListener(v -> {

                mCleanablesValueSmall.setText(oldCle);
                mUncleanablesValueSmall.setText(oldUncle);
                customizeSmall(oldCle, oldUncle);
                boo = false;

                mMiscHolderSmall.setVisibility(View.GONE);
                mMoreHolderSmall.setVisibility(View.VISIBLE);
            });

            Button btnDetails = itemView.findViewById(R.id.buttonMoreActions);
            btnDetails.setOnClickListener(v -> {
                if (SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
                    mMiscHolderSmall.setVisibility(View.GONE);
                    String phoneNumber = mStupid.getText().toString();
                    String studentName = mStudentNameValueSmall.getText().toString();
                    String cleanables = mCleanablesValueSmall.getText().toString();
                    String uncleanables = mUncleanablesValueSmall.getText().toString();
                    String message = mMess.getText().toString();
                    f.getSupportFragmentManager().beginTransaction()
                            .setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top, R.anim.slide_in_bottom, R.anim.slide_out_top)
                            .add(R.id.fragment_container, new DetailedOperationsFragment(phoneNumber,
                                    studentName, cleanables, uncleanables, message)).addToBackStack(null).commit();
                }
                mLastClickTime = SystemClock.elapsedRealtime();
            });
        }

        public void noInternet() {
            NoInternetDialog noInternetDialog = new NoInternetDialog();
            noInternetDialog.show(f.getSupportFragmentManager(), "NO CONNECTION");
        }

        public void operationError() {
            OperationErrorDialog operationErrorDialog = new OperationErrorDialog();
            operationErrorDialog.show(f.getSupportFragmentManager(), "OPERATION ERROR");
        }

        CreateGroupLoading loading;

        public void saveChanges() {

            String phone = mStupid.getText().toString();
            String cle = mCleanablesValueSmall.getText().toString();
            String uncle = mUncleanablesValueSmall.getText().toString();
            String mess = mMess.getText().toString();
            String url = buildURL(phone, cle, uncle, mess);

            loading = new CreateGroupLoading(f);
            loading.startLoading("Saving changes...");
            update(url);
        }

        public void update(String url) {

            String URL = "https://no-more-cashier-list.herokuapp.com/update?" + url;

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL, null,
                    response -> {
                        try {
                            String okay = response.getString("ok");
                            if (okay.equals("true")) {
                                loading.dismissDialog();
                            }
                            else {
                                operationError();
                                loading.dismissDialog();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            operationError();
                            loading.dismissDialog();
                        }
                    },
                    e -> {
                        e.printStackTrace();
                        noInternet();
                        loading.dismissDialog();
                    }
            );

            queue.add(request);
        }

        public String add(String current, String addend) {
            if (Integer.parseInt(current) > 1990)
                return current;
            return String.valueOf((Integer.parseInt(current)) + (Integer.parseInt(addend)));
        }

        public String subtract(String current, String minuend) {
            if (Integer.parseInt(current) < -1995)
                return current;
            return String.valueOf((Integer.parseInt(current)) - (Integer.parseInt(minuend)));
        }

        public void customizeSmall(String cle, String uncle) {

            int cl = Integer.parseInt(cle);
            int uncl = Integer.parseInt(uncle);

            if(cl < 0) {
                if (cl < -14) {
                    mCleanablesValueSmall.setTextColor(Color.parseColor("#FF0000"));
                }
                else {
                    mCleanablesValueSmall.setTextColor(Color.parseColor("#FF9800"));
                }
            }
            else {
                mCleanablesValueSmall.setTextColor(Color.parseColor("#278834"));
            }

            if(uncl < 0) {
                if (uncl < -14) {
                    mUncleanablesValueSmall.setTextColor(Color.parseColor("#FF0000"));
                }
                else {
                    mUncleanablesValueSmall.setTextColor(Color.parseColor("#FF9800"));
                }
            }
            else {
                mUncleanablesValueSmall.setTextColor(Color.parseColor("#278834"));
            }

            if ((cl < -14) || (uncl < -14)) {
                statusInfoSmall.setTextColor(Color.parseColor("#FF0000"));
                statusInfoSmall.setText("Not eligible");
                statusImageSmall.setBackgroundResource(R.drawable.ic_negative);
            }
            else {
                statusInfoSmall.setTextColor(Color.parseColor("#278834"));
                statusInfoSmall.setText("Eligible");
                statusImageSmall.setBackgroundResource(R.drawable.ic_positive);
            }
        }

        public String buildURL(String phone, String cln, String uncln, String msg) {
            String URL = ("phoneNumber=" + phone);
            URL += "&cln=" + cln;
            URL += "&uncln=" + uncln;
            URL += "&msg=" + msg;
            return URL;
        }
    }

    public StudentListAdapter(FragmentActivity c, ArrayList<StudentListItem> exampleList) {
        this.fragmentActivity = c;
        mStudentList = exampleList;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.student_list_item, parent, false);
        return new ExampleViewHolder(fragmentActivity, v);
    }

    @Override
    public void onBindViewHolder(StudentListAdapter.ExampleViewHolder holder, int position) {
        StudentListItem currentItem = mStudentList.get(position);
        //int[] colors = {Color.parseColor("#008000"), Color.parseColor("ADFF2F")};
        int[] colors = new int[2];
        int k = 0;
        String s = currentItem.getColorSmall();
        StringBuilder sub = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != '-')
                sub.append(s.charAt(i));
            else {
                colors[k] = Color.parseColor(sub.toString());
                sub = new StringBuilder();
                k++;
            }
        }
        colors[k] = Color.parseColor(sub.toString());
        GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TL_BR, colors);
        gd.setCornerRadius(0f);
        holder.mAvatarSmall.setBackground(gd);
        holder.mStupid.setText(currentItem.getPhoneNumberSmall());
        String sname = currentItem.getStudentNameValueSmall();
        String init = "";
        init += sname.charAt(0);
        holder.mAvatarSmall.setText(init);
        holder.mStudentNameValueSmall.setText(sname);
        holder.mCleanablesValueSmall.setText(currentItem.getCleanablesValueSmall());
        holder.mUncleanablesValueSmall.setText(currentItem.getUncleanablesValueSmall());
        holder.mMess.setText(currentItem.getMessageSmall());

        holder.customizeSmall(currentItem.getCleanablesValueSmall(), currentItem.getUncleanablesValueSmall());
    }

    @Override
    public int getItemCount() {
        return mStudentList.size();
    }
}