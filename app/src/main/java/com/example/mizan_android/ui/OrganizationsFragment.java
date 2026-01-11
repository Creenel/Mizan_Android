package com.example.mizan_android.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mizan_android.R;
import java.util.ArrayList;
import java.util.List;

public class OrganizationsFragment extends Fragment {

    private RecyclerView orgRecyclerView;
    private LinearLayout indicatorLayout, lawyerSection;
    private List<Organization> organizations;
    private List<Lawyer> lawyers;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_organizations, container, false);

        orgRecyclerView = view.findViewById(R.id.orgRecyclerView);
        indicatorLayout = view.findViewById(R.id.indicatorLayout);
        lawyerSection = view.findViewById(R.id.lawyerSection);

        setupOrganizations();
        setupLawyers();

        return view;
    }

    private void setupOrganizations() {
        organizations = new ArrayList<>();
        organizations.add(new Organization("Adalah", R.drawable.adalah, "Adalah is the first Palestinian Arab-run legal center in Israel..."));
        organizations.add(new Organization("Aman", R.drawable.aman, "Aman center promotes civil and human rights..."));
        organizations.add(new Organization("Zazim", R.drawable.zazim, "Zazim is a civic movement of Arabs and Jews in Israel..."));

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        orgRecyclerView.setLayoutManager(layoutManager);
        orgRecyclerView.setAdapter(new RecyclerView.Adapter<OrgViewHolder>() {
            @NonNull
            @Override
            public OrgViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_organization_card, parent, false);
                return new OrgViewHolder(itemView);
            }

            @Override
            public void onBindViewHolder(@NonNull OrgViewHolder holder, int position) {
                Organization org = organizations.get(position);
                holder.name.setText(org.name);
                holder.image.setImageResource(org.imageRes);
                holder.description.setText(org.summary);
                holder.itemView.setOnClickListener(v -> openChat(org));
            }

            @Override
            public int getItemCount() {
                return organizations.size();
            }
        });
    }

    private void openChat(Organization org) {
        // Placeholder
        Toast.makeText(getContext(), "This chat is not available yet!", Toast.LENGTH_SHORT).show();

    }

    private void setupLawyers() {
        lawyers = new ArrayList<>();
        lawyers.add(new Lawyer("Adv. Sarah Chen", "Human Rights Law", "sarah.chen@lawfirm.com", "email"));
        lawyers.add(new Lawyer("Adv. Omar Said", "Civil Rights & Discrimination", "18005550102", "phone"));
        lawyers.add(new Lawyer("Adv. Yael Levi", "Immigration & Refugee Law", "yael.levi@lawfirm.com", "email"));

        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (Lawyer lawyer : lawyers) {
            View card = inflater.inflate(R.layout.item_lawyer_card, lawyerSection, false);
            TextView name = card.findViewById(R.id.text_lawyer_name);
            TextView specialty = card.findViewById(R.id.text_lawyer_specialty);
            Button contact = card.findViewById(R.id.btn_contact);

            name.setText(lawyer.name);
            specialty.setText(lawyer.specialty);
            contact.setOnClickListener(v -> contactLawyer(lawyer));

            lawyerSection.addView(card);
        }
    }

    private void contactLawyer(Lawyer lawyer) {
        if (lawyer.contactType.equals("email")) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + lawyer.contact));
            startActivity(Intent.createChooser(emailIntent, "Contact " + lawyer.name));
        } else if (lawyer.contactType.equals("phone")) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + lawyer.contact));
            startActivity(callIntent);
        }
    }

    // Models
    static class Organization {
        String name;
        int imageRes;
        String summary;
        Organization(String name, int imageRes, String summary) {
            this.name = name;
            this.imageRes = imageRes;
            this.summary = summary;
        }
    }

    static class Lawyer {
        String name, specialty, contact, contactType;
        Lawyer(String name, String specialty, String contact, String contactType) {
            this.name = name;
            this.specialty = specialty;
            this.contact = contact;
            this.contactType = contactType;
        }
    }

    static class OrgViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView image;

        TextView description;
        public OrgViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.text_name);
            image = itemView.findViewById(R.id.image_org);
            description = itemView.findViewById(R.id.text_description);
        }
    }
}
