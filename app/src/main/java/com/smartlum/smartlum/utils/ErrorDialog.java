package com.smartlum.smartlum.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.smartlum.smartlum.R;
import com.smartlum.smartlum.adapter.DeviceError;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Этот класс используется для уведомления об ошибках посредством Dialog (всплывающее окно)
 * Принимает в себя тип данных {@link DeviceError} (создал сам для удобства).
 * По сути просто RecyclerView список внутри всплывающего окна
 */

public class ErrorDialog extends DialogFragment {

    public static final String TAG = "ERROR DIALOG";

    private final Context context;
    private final FragmentManager fragmentManager;
    private final ErrorAdapter adapter;
    private final ArrayList<String> errorList;

    private TextView     tvHeader;
    private String       headerText;
    private ImageButton  btnDismiss;
    private RecyclerView recyclerView;

    public ErrorDialog(Context context, FragmentManager fragmentManager) {
        this.context = context;
        this.fragmentManager = fragmentManager;
        errorList = new ArrayList<>();
        adapter   = new ErrorAdapter(context, errorList);
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme())
        {
            @Override
            public void onBackPressed() {

            }
        };
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_SmartLum);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.Theme_SmartLum_SlideVertical);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.error_dialog, container, false);
        tvHeader     = view.findViewById(R.id.tv_error_found_header);
        btnDismiss   = view.findViewById(R.id.btn_dialog_dismiss);
        recyclerView = view.findViewById(R.id.rv_errors);
        tvHeader.setText(headerText);
        LinearLayoutManager layoutManagerGroup = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManagerGroup);
        recyclerView.setAdapter(adapter);
        btnDismiss.setOnClickListener(v -> dismiss());
        return view;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }

    public void handleError(int register, int value) {
        String r = String.valueOf(register);
        if (value == 1) {
            if (!errorList.contains(r)) {
                errorList.add(r);
                headerText = context.getResources().getString(R.string.error_header_found);
                if (!this.isVisible()) {
                    this.show(fragmentManager, TAG);
                }
                adapter.notifyDataSetChanged();
            }
        }

        else if (value == 0) {
            errorList.remove(r);
            if (adapter.getItemCount() == 0) {
                headerText = context.getResources().getString(R.string.error_header_gone);
            }
            adapter.notifyDataSetChanged();
        }
        if (tvHeader != null) {tvHeader.setText(headerText);}
    }

    public void handleError(DeviceError error) {
        String r = String.valueOf(error.getRegister());
        Log.e(TAG, "handleError: error code" + error.getCode());
        if (error.getCode() == 1) {
            if (!errorList.contains(r)) {
                errorList.add(r);
                headerText = context.getResources().getString(R.string.error_header_found);
                if (!this.isVisible()) {
                    this.show(fragmentManager, TAG);
                }
                adapter.notifyDataSetChanged();
            }
        }
        else if (error.getCode() == 0) {
            errorList.remove(r);
            if (adapter.getItemCount() == 0) {
                headerText = context.getResources().getString(R.string.error_header_gone);
            }
            adapter.notifyDataSetChanged();
        }
        if (tvHeader != null) {tvHeader.setText(headerText);}
    }

}

class ErrorAdapter extends RecyclerView.Adapter<ErrorAdapter.ViewHolder> {

    private final String TAG = "ErrorAdapter";
    private final ArrayList<String> errorCode;
    private final String[] errorDescription;
    private final String[] errorName;
    private final Context context;

    // Create constructor
    public ErrorAdapter(Context context, ArrayList<String> list) {
        this.context     = context;
        errorCode        = list;
        errorDescription = context.getResources().getStringArray(R.array.errors_description);
        errorName        = context.getResources().getStringArray(R.array.errors_name);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ui_error_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        Animation animation;
        animation = AnimationUtils.loadAnimation(context, R.anim.slide_left);
        animation.setDuration(400);
        viewHolder.itemView.startAnimation(animation);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvErrorName.setText(getText(errorName, position));
        holder.tvErrorDescription.setText(getText(errorDescription, position));
    }

    @Override
    public int getItemCount() {
        return errorCode.size();
    }

    private String getText(String[] data, int position) {
        return data[Integer.parseInt(errorCode.get(position)) - 1];
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvErrorName;
        TextView tvErrorDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvErrorName        = itemView.findViewById(R.id.tv_error_name);
            tvErrorDescription = itemView.findViewById(R.id.tv_error_description);
        }
    }
}
