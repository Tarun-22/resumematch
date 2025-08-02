package com.example.resumematch.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.resumematch.R;

public class JobTemplateActivity extends AppCompatActivity {

    private Button btn_Cashier, btn_SalesAssociate, btn_StockClerk, btn_Manager, btn_Custom;
    private ImageView back_Btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_template);

        back_Btn = findViewById(R.id.backButton);
        btn_Cashier = findViewById(R.id.btnCashier);
        btn_SalesAssociate = findViewById(R.id.btnSalesAssociate);
        btn_StockClerk = findViewById(R.id.btnStockClerk);
        btn_Manager = findViewById(R.id.btnManager);
        btn_Custom = findViewById(R.id.btnCustom);

        back_Btn.setOnClickListener(v -> finish());

        btn_Cashier.setOnClickListener(v -> {
            Intent intent = new Intent(JobTemplateActivity.this, CreateJobActivity.class);
            intent.putExtra("template", "cashier");
            intent.putExtra("title", "Cashier");
            intent.putExtra("description", "Looking for a reliable cashier for our store.\n\nRequirements:\n• Customer service skills\n• Cash handling experience\n• Basic math skills\n• Team work ability\n• Weekend availability\n• Morning/Evening shifts\n\nSkills needed:\n• POS system operation\n• Inventory management\n• Customer communication\n• Problem solving");
            startActivity(intent);
            finish();
        });

        btn_SalesAssociate.setOnClickListener(v -> {
            Intent intent = new Intent(JobTemplateActivity.this, CreateJobActivity.class);
            intent.putExtra("template", "sales");
            intent.putExtra("title", "Sales Associate");
            intent.putExtra("description", "Seeking enthusiastic sales associate for customer service.\n\nRequirements:\n• Sales experience preferred\n• Customer service skills\n• Product knowledge\n• Flexible schedule\n• Weekend availability\n• Part-time/Full-time options\n\nSkills needed:\n• Sales techniques\n• Product demonstration\n• Customer assistance\n• Inventory tracking\n• Team collaboration");
            startActivity(intent);
            finish();
        });

        btn_StockClerk.setOnClickListener(v -> {
            Intent intent = new Intent(JobTemplateActivity.this, CreateJobActivity.class);
            intent.putExtra("template", "stock");
            intent.putExtra("title", "Stock Clerk");
            intent.putExtra("description", "Need organized stock clerk for inventory management.\n\nRequirements:\n• Physical stamina\n• Attention to detail\n• Organization skills\n• Early morning availability\n• Weekend shifts\n• Heavy lifting ability\n\nSkills needed:\n• Inventory management\n• Stock rotation\n• Receiving procedures\n• Warehouse organization\n• Safety protocols");
            startActivity(intent);
            finish();
        });

        btn_Manager.setOnClickListener(v -> {
            Intent intent = new Intent(JobTemplateActivity.this, CreateJobActivity.class);
            intent.putExtra("template", "manager");
            intent.putExtra("title", "Store Manager");
            intent.putExtra("description", "Experienced store manager needed for leadership role.\n\nRequirements:\n• Management experience\n• Leadership skills\n• Problem solving ability\n• Flexible schedule\n• Weekend availability\n• Full-time commitment\n\nSkills needed:\n• Team management\n• Sales operations\n• Inventory control\n• Customer service\n• Financial reporting\n• Staff scheduling");
            startActivity(intent);
            finish();
        });

        btn_Custom.setOnClickListener(v -> {
            Intent intent = new Intent(JobTemplateActivity.this, CreateJobActivity.class);
            startActivity(intent);
            finish();
        });
    }
} 