package com.android.medicinenotification.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.android.medicinenotification.data.MedicineModel;
import com.android.medicinenotification.databinding.ItemDetailDateBinding;

import java.util.ArrayList;

public class MedicineListAdapter extends RecyclerView.Adapter<MedicineListAdapter.MedicineViewHolder> {
    private ArrayList<MedicineModel> mList;
    public ItemClickListener callback;

    public MedicineListAdapter(ArrayList<MedicineModel> fList, ItemClickListener callback) {
        this.mList = fList;
        this.callback = callback;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDetailDateBinding binding = ItemDetailDateBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new MedicineViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setList(ArrayList<MedicineModel> fList) {
        this.mList = fList;
        notifyDataSetChanged();
    }

    public class MedicineViewHolder extends RecyclerView.ViewHolder{
        private ItemDetailDateBinding binding;

        public MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemDetailDateBinding.bind(itemView);

            itemClickEvent();
        }

        public void bind(MedicineModel medicineModel) {
            binding.tvFoodName.setText(medicineModel.getMedicineName());
            binding.tvDoseDate.setText(medicineModel.getDoseDate());
            binding.tvWhen.setText(medicineModel.getWhen());
            binding.tvDose.setText(medicineModel.getDose());
        }

        private void itemClickEvent() {
            binding.imbtEdit.setOnClickListener(v -> {
                callback.onItemEdit(getAdapterPosition(), mList.get(getAdapterPosition()));
//                MedicineDialog medicineDialog = new MedicineDialog(itemView.getContext(), DlgType.EDIT, mList.get(getAdapterPosition()).doseDate);
//                medicineDialog.show();
//                medicineDialog.setMedicineInfo(mList.get(getAdapterPosition()));
            });

            binding.imbtDelete.setOnClickListener(v -> {
                callback.onItemDelete(getAdapterPosition(), mList.get(getAdapterPosition()));
            });
        }
    }
}




















